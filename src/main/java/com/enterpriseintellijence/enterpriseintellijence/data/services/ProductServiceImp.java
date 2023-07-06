package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ProductCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.Constants;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ProductCatRepository productCatRepository;
    private final SizeRepository sizeRepository;
    private final TokenStore tokenStore;
    // TODO: 19/06/2023 usare interfaccia
    private final ImageServiceImp imageServiceImp;

    private final Clock clock;


    private final JwtContextUtils jwtContextUtils;

    @Override
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        try {
            LocalDateTime now = getTimeNow();
            Product product = mapToEntityCreation(productCreateDTO);
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            product.setLikesNumber(0);
            product.setViews(0);
            product.setSeller(loggedUser);
            product.setAvailability(Availability.AVAILABLE);
            if (!productCatRepository.existsByIdEqualsAndPrimaryCatEqualsAndSecondaryCatEqualsAndTertiaryCatEquals(
                productCreateDTO.getProductCategory().getId(),
                productCreateDTO.getProductCategory().getPrimaryCat(),
                productCreateDTO.getProductCategory().getSecondaryCat(),
                productCreateDTO.getProductCategory().getTertiaryCat()))
                throw new IllegalArgumentException("Category not found");

            // TODO: 22/06/2023 controllo sulla size
            productRepository.save(product);
            for (MultipartFile multipartFile : productCreateDTO.getProductImages()) {
                imageServiceImp.localProductImageSave(product, multipartFile, new ProductImage(), "ciao");
            }

            notificationService.notifyNewProduct(product);
            return mapToProductDetailsDTO(product);
        } catch (Exception e) {

            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ProductDTO replaceProduct(String id, ProductDTO productDTO) throws IllegalAccessException {
        throwOnIdMismatch(id, productDTO);

        return updateProduct(id, productDTO);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot update product of others");

        //un prodotto può essere modificato solo se non esiste un ordine in corso
        if (product.getOrder() != null)
            throw new IllegalAccessException("Cannot update product while order is active");


        if (patch.getTitle() != null && !product.getTitle().equals(patch.getTitle()))
            product.setTitle(patch.getTitle());
        if (patch.getDescription() != null && !product.getDescription().equals(patch.getDescription()))
            product.setDescription(patch.getDescription());
        if (patch.getVisibility() != null && !product.getVisibility().equals(patch.getVisibility()))
            product.setVisibility(patch.getVisibility());
        if (patch.getCondition() != null && !product.getCondition().equals(patch.getCondition()))
            product.setCondition(patch.getCondition());


        // TODO: 28/05/2023 serve che restituisco il custom money nella funzione?
        if (patch.getProductCost() != null)
            product.setProductCost(checkAndChangeCustomMoney(product.getProductCost(), patch.getProductCost()));
        if (patch.getDeliveryCost() != null)
            product.setDeliveryCost(checkAndChangeCustomMoney(product.getDeliveryCost(), patch.getDeliveryCost()));

        if (patch.getBrand() != null && !product.getBrand().equals(patch.getBrand()))
            product.setBrand(patch.getBrand());
        if (patch.getProductSize() != null && !product.getProductSize().equals(patch.getProductSize()))
            product.setProductSize(patch.getProductSize());

        if (patch instanceof ClothingDTO) {
            // TODO: 22/05/2023

        } else if (patch instanceof EntertainmentDTO) {
            // TODO: 22/05/2023  
        } else if (patch instanceof HomeDTO) {
            // TODO: 22/05/2023  
        }


        productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public void deleteProduct(String id) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot delete product of others");

        if (loggedUser.getRole().equals(UserRole.USER) && product.getOrder() != null)
            throw new IllegalAccessException("Cannot delete product with order active");

        // TODO: 26/06/2023 perchè product sold in una delete?
        notificationService.notifyProductSold(product);
        try {
            productRepository.delete(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProductDTO getProductById(String id, boolean ignoreVisibility) {
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser != null && product.getSeller().getId().equals(loggedUser.getId()))
            return mapToProductDetailsDTO(product);


        if (product.getVisibility().equals(Visibility.PRIVATE) && !ignoreVisibility)
            throw new EntityNotFoundException("Product not found");


        product.setViews(product.getViews() + 1);
        productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductBasicDTO getProductBasicById(String id, boolean ignoreVisibility) {
        return mapToProductBasicDTO(mapToEntity(getProductById(id, ignoreVisibility)));
    }

    @Override
    public Page<ProductBasicDTO> getAllPagedBySellerId(UserBasicDTO userBasicDTO, int page, int size) {
        User user = modelMapper.map(userBasicDTO, User.class);
        Page<Product> products = null;

        if (jwtContextUtils.getUsernameFromContext().isPresent() && jwtContextUtils.getUsernameFromContext().get().equals(user.getUsername()))
            products = productRepository.findAllBySeller(user, PageRequest.of(page, size));
        else
            products = productRepository.findAllBySellerAndVisibilityEquals(user, Visibility.PUBLIC, PageRequest.of(page, size));
        List<ProductBasicDTO> collect = products.stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, products.getPageable(), products.getTotalElements());
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
        Page<Offer> offers = new PageImpl<Offer>(product.getOffers(), PageRequest.of(page, size), product.getOffers().size());
        List<OfferBasicDTO> collect = offers.stream().map(s -> modelMapper.map(s, OfferBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, PageRequest.of(page, size), offers.getTotalElements());
    }

    @Override
    public Page<MessageDTO> getProductMessages(String id, int page, int size) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see messages of others product");
        Page<Message> messages = new PageImpl<Message>(product.getMessages(), PageRequest.of(page, size), product.getMessages().size());
        List<MessageDTO> collect = messages.stream().map(s -> modelMapper.map(s, MessageDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, PageRequest.of(page, size), messages.getTotalElements());
    }


    @Override
    public OrderBasicDTO getProductOrder(String id) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        // TODO: 28/05/2023 metodo riservato da mettere sotto al prodotto nella pagina del seller

        if (!loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see orders of others product");
        if (product.getOrder() != null)
            return modelMapper.map(product.getOrder(), OrderBasicDTO.class);
        else
            throw new EntityNotFoundException("Order not exists");
    }

    @Override
    public Page<ProductBasicDTO> getProductFilteredPage(Specification<Product> withFilters, int page, int size, String sortBy, String sortDirection) {

        Sort sort = null;
        Sort.Direction direction = null;
        if (sortBy != null && !sortBy.isEmpty()) {
            if (sortDirection.equals("DESC") || sortDirection.equals("ASC"))
                direction = Sort.Direction.fromString(sortDirection);
            else
                direction = Sort.Direction.DESC;
            sort = Sort.by(direction, sortBy);
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.findAll(withFilters, pageable);
        List<ProductBasicDTO> collect = products.stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, products.getTotalElements());
    }

    @Override
    public Page<ProductBasicDTO> getMyProducts(int page, int size) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getSellingProducts() != null)
            return new PageImpl<>(
                loggedUser.getSellingProducts().stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList()),
                PageRequest.of(page, size),
                loggedUser.getSellingProducts().size());
        else
            return new PageImpl<>(null, PageRequest.of(page, size), 0);
    }

    @Override
    public Iterable<ProductCategoryDTO> getCategoriesList() {
        List<ProductCategory> productCategories = productCatRepository.findAll();

        return productCategories.stream().map(s -> modelMapper.map(s, ProductCategoryDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Iterable<String> getPrimaryCategoriesList() {
        Map<String, ProductCategory> categoriesMap = new HashMap<>();
        List<ProductCategory> productCategories = productCatRepository.findAll();

        productCategories.forEach(c -> {
            if (!categoriesMap.containsKey(c.getPrimaryCat())) {
                categoriesMap.put(c.getPrimaryCat(), c);
            }
        });
        return new ArrayList<String>(categoriesMap.keySet());
    }

    @Override
    public Iterable<String> getSecondaryCategoriesListByPrimaryCat(String primaryCategory) {
        List<ProductCategory> productCategories = productCatRepository.findAllByPrimaryCat(primaryCategory);
        Map<String, ProductCategory> categoriesMap = new HashMap<>();

        productCategories.forEach(c -> {
            if (!categoriesMap.containsKey(c.getSecondaryCat())) {
                categoriesMap.put(c.getSecondaryCat(), c);
            }
        });
        return new ArrayList<String>(categoriesMap.keySet());
    }

    @Override
    public Iterable<String> getTertiaryCategoriesListBySecondaryCat(String secondaryCategory) {
        List<ProductCategory> productCategories = productCatRepository.findAllBySecondaryCat(secondaryCategory);
        Map<String, ProductCategory> categoriesMap = new HashMap<>();

        productCategories.forEach(c -> {
            if (!categoriesMap.containsKey(c.getTertiaryCat())) {
                categoriesMap.put(c.getTertiaryCat(), c);
            }
        });
        return new ArrayList<String>(categoriesMap.keySet());
    }

    @Override
    public Iterable<SizeDTO> getSizeList() {
        List<Size> sizes = sizeRepository.findAll();

        return sizes.stream().map(s -> modelMapper.map(s, SizeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Iterable<String> getSizeListByCategory(String category) {
        List<Size> sizes = sizeRepository.findAll();
        List<String> filteredSizes = new ArrayList<>();
        sizes.forEach(s -> {
            if(s.getType().equals(category)) {
                filteredSizes.add(s.getSizeName());
            }
        });
        return filteredSizes;

    }

    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }

    private CustomMoney checkAndChangeCustomMoney(CustomMoney customMoney, CustomMoneyDTO customMoneyDTO) {
        if (!customMoney.getPrice().equals(customMoneyDTO.getPrice()))
            customMoney.setPrice(customMoneyDTO.getPrice());
        if (!customMoney.getCurrency().equals(customMoneyDTO.getCurrency()))
            customMoney.setCurrency(customMoneyDTO.getCurrency());
        return customMoney;
    }

    private Product mapToEntityCreation(ProductCreateDTO productCreateDTO) {
        if (productCreateDTO.getProductCategory().getPrimaryCat().equals("Clothing")) {
            return modelMapper.map(productCreateDTO, Clothing.class);
        } else if (productCreateDTO.getProductCategory().getPrimaryCat().equals("Home")) {

            return modelMapper.map(productCreateDTO, Home.class);
        } else if (productCreateDTO.getProductCategory().getPrimaryCat().equals("Entertainment")) {
            return modelMapper.map(productCreateDTO, Entertainment.class);
        } else {
            return modelMapper.map(productCreateDTO, Product.class);
        }
    }


    private Product mapToEntity(ProductDTO productDTO) {
        if (productDTO.getProductCategory().getPrimaryCat().equals("Clothing")) {
            return modelMapper.map(productDTO, Clothing.class);
        } else if (productDTO.getProductCategory().getPrimaryCat().equals("Home")) {

            return modelMapper.map(productDTO, Home.class);
        } else if (productDTO.getProductCategory().getPrimaryCat().equals("Entertainment")) {
            return modelMapper.map(productDTO, Entertainment.class);
        } else {
            return modelMapper.map(productDTO, Product.class);
        }
    }

    private ProductDTO mapToProductDetailsDTO(Product product) {
        if (product.getProductCategory().getPrimaryCat().equals("Clothing"))
            return modelMapper.map(product, ClothingDTO.class);
        else if (product.getProductCategory().getPrimaryCat().equals("Home"))
            return modelMapper.map(product, HomeDTO.class);
        else if (product.getProductCategory().getPrimaryCat().equals("Entertainment"))
            return modelMapper.map(product, EntertainmentDTO.class);
        else
            return modelMapper.map(product, ProductDTO.class);
    }

    private List<ProductBasicDTO> mapToProductBasicDTOList(Page<Product> products) {
        return products.stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
    }

    private ProductBasicDTO mapToProductBasicDTO(Product product) {
        return modelMapper.map(product, ProductBasicDTO.class);
    }


    private void throwOnIdMismatch(String productId, ProductDTO productDTO) {
        if (productDTO.getId() != null && !productDTO.getId().equals(productId)) {
            throw new IdMismatchException();
        }
    }

    public String getCapabilityUrl(String productId) {
        String token = getCapabilityToken(productId);
        return getCapabilityUrlFromToken(token);
    }
    private String getCapabilityUrlFromToken(String token) {
        return Constants.BASE_PATH + "products/capability/" + token;
    }

    @Override
    public CapabilityDTO getCapability(String productId) {
        String token = getCapabilityToken(productId);
        return new CapabilityDTO(token, getCapabilityUrlFromToken(token));
    }


    @Override
    public String getCapabilityToken(String productId) {
        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        if (username.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);
        if (!product.getSeller().getUsername().equals(username.get())) {
            throw new RuntimeException("Unauthorized operation");
        }
        return tokenStore.createCapabilityToken(productId);
    }

}
