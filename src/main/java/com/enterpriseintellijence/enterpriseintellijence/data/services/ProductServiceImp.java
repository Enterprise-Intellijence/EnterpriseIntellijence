package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ClothingRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.EntertainmentRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.HomeRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ClothingRepository clothingRepository;
    private final EntertainmentRepository entertainmentRepository;
    private final HomeRepository homeRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TokenStore tokenStore;

    private final Clock clock;


    private final JwtContextUtils jwtContextUtils;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!loggedUser.getId().equals(productDTO.getSeller().getId()))
            throw new IllegalAccessException("Seller Id missmatch with logged user, cannot insert product for other user");

        try{
            LocalDateTime now = getTimeNow();
            Product product = mapToEntity(productDTO);
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            product.setLikesNumber(0);
            product.setViews(0);
            product.setSeller(loggedUser);
            productRepository.save(product);

            return mapToProductDetailsDTO(product);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProductDTO replaceProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        Product oldProduct = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = mapToEntity(productDTO);

        User userRequesting = jwtContextUtils.getUserLoggedFromContext();
        if (!oldProduct.getSeller().getUsername().equals(userRequesting.getUsername())
                && userRequesting.getRole().equals(UserRole.USER))
            throw new EntityNotFoundException("Product not found");

        product = productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot update product of others");

        //un prodotto può essere modificato solo se non esiste un ordine in corso
        if(product.getOrder()!=null)
            throw new IllegalAccessException("Cannot update product while order is active");


        if(patch.getTitle()!=null && !product.getTitle().equals(patch.getTitle()))
            product.setTitle(patch.getTitle());
        if(patch.getDescription()!=null && !product.getDescription().equals(patch.getDescription()) )
            product.setDescription(patch.getDescription());
        if(patch.getVisibility()!=null && !product.getVisibility().equals(patch.getVisibility()))
            product.setVisibility(patch.getVisibility());
        if(patch.getCondition()!=null && !product.getCondition().equals(patch.getCondition()))
            product.setCondition(patch.getCondition());


        // TODO: 28/05/2023 serve che restituisco il custom money nella funzione?
        if(patch.getProductCost()!= null)
            product.setProductCost(checkAndChangeCustomMoney(product.getProductCost(),patch.getProductCost()));
        if(patch.getDeliveryCost()!=null)
            product.setDeliveryCost(checkAndChangeCustomMoney(product.getDeliveryCost(),patch.getDeliveryCost()));


        // TODO: 28/05/2023 il !=null qui mi sa che è superfluo, sono cmq settati dalla deserializzazione
        if(!product.getProductCategoryChild().equals(patch.getProductCategoryChild())){
            product.setProductCategoryChild(patch.getProductCategoryChild());
            product.setProductCategoryParent(patch.getProductCategoryParent());
            product.setProductCategory(patch.getProductCategory());
            // TODO: 28/05/2023 se cambia l'istanza del prodotto?
        }

        if(patch.getAddress()!=null && !product.getAddress().equals(patch.getAddress()) )
            product.setAddress(modelMapper.map(patch.getAddress(), Address.class) );
        if(patch.getBrand()!=null && !product.getBrand().equals(patch.getBrand()))
            product.setBrand(patch.getBrand());
        if(patch.getProductSize()!=null && !product.getProductSize().equals(patch.getProductSize()))
            product.setProductSize(patch.getProductSize());

        if(patch instanceof ClothingDTO){
            // TODO: 22/05/2023

        }
        else if(patch instanceof EntertainmentDTO){
            // TODO: 22/05/2023  
        }
        else if(patch instanceof HomeDTO){
            // TODO: 22/05/2023  
        }



        productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public void deleteProduct(String id) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot delete product of others");
        if(product.getOrder()!=null)
            throw new IllegalAccessException("Cannot delete product with order active");

        // TODO: 25/05/2023 controllare che cosa cancella
        productRepository.delete(product);
    }

    @Override
    public ProductDTO getProductById(String id, boolean capability) {
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));
        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        if(username.isPresent()) {
            User userRequesting = userRepository.findByUsername(username.get());
            if (product.getSeller().getUsername().equals(userRequesting.getUsername()) || capability)
                return mapToProductDetailsDTO(product);
        }

        if (product.getVisibility().equals(Visibility.PRIVATE) && !capability)
            throw new EntityNotFoundException("Product not found");
        product.setViews(product.getViews()+1);
        productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public Iterable<ProductBasicDTO> findAll() {
        return productRepository.findAll().stream()
                .map(s -> modelMapper.map(s, ProductBasicDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductBasicDTO> getAllPaged(int page, int size) {
        Page<Product> products = productRepository.findAllByVisibility(Visibility.PUBLIC, PageRequest.of(page,size));//la dimensione deve arrivare tramite parametro
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getProductFilteredForCategoriesPaged(int page, int size, ProductCategory productCategory) {
        Page<Product> products = productRepository.findAllByProductCategoryAndVisibility(productCategory,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getAllPagedBySellerId(UserBasicDTO userBasicDTO, int page, int size) {
        User user=modelMapper.map(userBasicDTO,User.class);
        Page<Product> products = null;

        if(jwtContextUtils.getUsernameFromContext().isPresent() && jwtContextUtils.getUsernameFromContext().get().equals(user.getUsername()))
            products= productRepository.findAllBySeller(user,PageRequest.of(page,size));
        else
            products = productRepository.findAllBySellerAndVisibilityEquals(user, Visibility.PUBLIC,PageRequest.of(page, size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s,ProductBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getClothingByTypePaged(int page, int size, ClothingType clothingType) {
        Page<Product> products = clothingRepository.findAllByClothingTypeAndVisibility(clothingType,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getEntertainmentByTypePaged(int page, int size, EntertainmentType entertainmentType) {
        Page<Product> products = entertainmentRepository.findAllByEntertainmentTypeAndVisibility(entertainmentType,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getHomeByTypePaged(int page, int size, HomeType homeType) {
        Page<Product> products = homeRepository.findAllByHomeTypeAndVisibility(homeType,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> searchProduct(String keystring, int page, int size) {

//        List<Product> products = productRepository.search(keystring,PageRequest.of(page,size));
        Page<Product> products = productRepository.findAllByTitleContainingOrDescriptionContainingAndVisibility(keystring,keystring,Visibility.PUBLIC,PageRequest.of(page,size));

        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);

    }

    @Override
    public Page<ProductBasicDTO> searchProductByPrice(Double startPrice, Double endPrice, int page, int size) {
        Page<Product> products = productRepository.getByProductByPrice(startPrice,endPrice,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getMostLikedProducts(int page, int size) {
        Page<Product> products = productRepository.findAllByVisibility(Visibility.PUBLIC,PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"likesNumber")));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getMostViewedProducts(int page, int size) {
        Page<Product> products = productRepository.findAllByVisibility(Visibility.PUBLIC,PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"views")));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }


    private Product mapToEntity(ProductDTO productDTO) {
        if(productDTO.getProductCategory().equals(ProductCategory.CLOTHING)) {
            System.out.println("ma pure qui 1");
            return modelMapper.map(productDTO, Clothing.class);
        }
        else if(productDTO.getProductCategory().equals(ProductCategory.HOME)) {
            System.out.println("ma pure qui 2");

            return modelMapper.map(productDTO, Home.class);
        }
        else if(productDTO.getProductCategory().equals(ProductCategory.ENTERTAINMENT)) {
            System.out.println("ma pure qui 3");
            return modelMapper.map(productDTO, Entertainment.class);
        }
        else {
            System.out.println("ma pure qui 4");
            return modelMapper.map(productDTO, Product.class);
        }
    }

    private ProductDTO mapToProductDetailsDTO(Product product) {
        if(product.getProductCategory().equals(ProductCategory.CLOTHING))
            return modelMapper.map(product, ClothingDTO.class);
        else if(product.getProductCategory().equals(ProductCategory.HOME))
            return modelMapper.map(product, HomeDTO.class);
        else if(product.getProductCategory().equals(ProductCategory.ENTERTAINMENT))
            return modelMapper.map(product, EntertainmentDTO.class);
        else
            return modelMapper.map(product, ProductDTO.class);
    }

    private List<ProductBasicDTO> mapToProductBasicDTOList(Page<Product> products) {
        return products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
    }



    private void throwOnIdMismatch(String id, ProductDTO productDTO) {
        if (productDTO.getId() != null && !productDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }

    public String getCapabilityUrl(String id) {

        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        if (username.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!product.getSeller().getUsername().equals(username.get())){
            throw new RuntimeException("Unauthorized operation");
        }
        String token = tokenStore.createCapabilityToken(id);
        return  "https://localhost:8443/api/v1/products/capability/" + token;
    }

    @Override
    public Page<UserBasicDTO> getUserThatLikedProduct(String id, int page, int size) {
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty()) {
            throw new EntityNotFoundException("Product not found");
        }
        return userRepository.findAllByLikedProducts(id, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserBasicDTO.class));
    }

    @Override
    public Page<OfferBasicDTO> getProductOffers(String id, int page, int size) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see offers of others product");
        Page<Offer> offers = new PageImpl<Offer>(product.getOffers(),PageRequest.of(page,size),product.getOffers().size());
        List<OfferBasicDTO> collect = offers.stream().map(s->modelMapper.map(s, OfferBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public Page<MessageDTO> getProductMessages(String id, int page, int size) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see messages of others product");
        Page<Message> messages = new PageImpl<Message>(product.getMessages(), PageRequest.of(page,size),product.getMessages().size());
        List<MessageDTO> collect = messages.stream().map(s->modelMapper.map(s, MessageDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public OrderBasicDTO getProductOrder(String id) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see orders of others product");
        if(product.getOrder()!=null)
            return modelMapper.map(product.getOrder(),OrderBasicDTO.class);
        else
            throw new EntityNotFoundException("Order not exists");
    }

    private LocalDateTime getTimeNow(){
        return LocalDateTime.now(clock);
    }

    private CustomMoney checkAndChangeCustomMoney(CustomMoney customMoney, CustomMoneyDTO customMoneyDTO){
        if(!customMoney.getPrice().equals(customMoneyDTO.getPrice()))
            customMoney.setPrice(customMoneyDTO.getPrice());
        if(!customMoney.getCurrency().equals(customMoneyDTO.getCurrency()))
            customMoney.setCurrency(customMoneyDTO.getCurrency());
        return customMoney;
    }


}
