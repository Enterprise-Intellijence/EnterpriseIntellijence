package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ClothingCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.HomeCreateDTO;
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
            ProductCategory category;
            if(productCreateDTO.getProductCategory().getId() == null) {
                category = productCatRepository.findProductCategoryByTertiaryCat(productCreateDTO.getProductCategory().getTertiaryCat());
                if(category != null)
                    product.setProductCategory(category);
                else throw new IllegalArgumentException("Category not found");
            }

            if(productCreateDTO instanceof ClothingCreateDTO){
                Clothing clothing= (Clothing) product;
                Size size = sizeRepository.findBySizeNameAndType(clothing.getClothingSize().getSizeName(), clothing.getClothingSize().getType());
                if(size == null)
                    throw new IllegalArgumentException("Size not found");

                if(!size.getType().equals(clothing.getProductCategory().getSecondaryCat()))
                    throw new IllegalArgumentException("Can't use this size for this category");
                clothing.setClothingSize(size);
            }
            else if (productCreateDTO instanceof HomeCreateDTO){
                Home home= (Home) product;
                Size size = sizeRepository.findBySizeNameAndType(home.getHomeSize().getSizeName(), home.getHomeSize().getType());
                if(size == null)
                    throw new IllegalArgumentException("Size not found");

                if(!size.getType().equals(home.getProductCategory().getPrimaryCat()))
                    throw new IllegalArgumentException("Can't use this size for this category");
                home.setHomeSize(size);
            }

            productRepository.save(product);

            notificationService.notifyNewProduct(product);
            return mapToProductDetailsDTO(product);
        } catch (Exception e) {

            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot update product of others");

        //un prodotto può essere modificato solo se non esiste un ordine in corso
        if (!product.getAvailability().equals(Availability.AVAILABLE) && !product.getOrder().isEmpty()){
            throw new IllegalAccessException("Cannot update product while order is active");

        }


        if (patch.getDescription() != null && !product.getDescription().equals(patch.getDescription()))
            product.setDescription(patch.getDescription());

        product.setVisibility(patch.getVisibility());
        product.setTitle(patch.getTitle());
        product.setCondition(patch.getCondition());

        product.setProductCost(checkAndChangeCustomMoney(product.getProductCost(), patch.getProductCost()));
        product.setDeliveryCost(checkAndChangeCustomMoney(product.getDeliveryCost(), patch.getDeliveryCost()));

        if (patch.getBrand() != null && !product.getBrand().equals(patch.getBrand()))
            product.setBrand(patch.getBrand());
        if (!product.getProductSize().equals(patch.getProductSize()))
            product.setProductSize(patch.getProductSize());

        if (patch instanceof ClothingDTO) {
            ClothingDTO categorized = (ClothingDTO) patch;
            Size size = sizeRepository.findBySizeNameAndType(categorized.getClothingSize().getSizeName(), categorized.getClothingSize().getType());
            if(size == null)
                throw new IllegalArgumentException("Size not found");

        } else if (patch instanceof HomeDTO) {
            HomeDTO categorized = (HomeDTO) patch;
            Size size = sizeRepository.findBySizeNameAndType(categorized.getHomeSize().getSizeName(), categorized.getHomeSize().getType());
            if(size == null)
                throw new IllegalArgumentException("Size not found");
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

        if (!product.getOrder().isEmpty())
            throw new IllegalAccessException("Cannot delete product with order active");

        //Avvisiamo un utente che aveva messo like al prodotto che questo è stato venduto o rimosso dal venditore
        try {
            productRepository.delete(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
        notificationService.notifyProductSold(product);
    }

    @Override
    public ProductDTO getProductById(String id, boolean ignoreVisibility) {
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String loggedUseridString = "";
        if (loggedUser != null){
           loggedUseridString = loggedUser.getId();
        }

        //TODO: to complete
        if(!product.getSeller().getId().equals(loggedUseridString) && product.getSeller().getStatus().equals(UserStatus.HOLIDAY))
            throw new EntityNotFoundException("Product not found HOLIDAY");

        if (loggedUser != null && product.getSeller().getId().equals(loggedUser.getId()) && loggedUser.getRole().equals(UserRole.USER))
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
    public Page<UserBasicDTO> getUserThatLikedProduct(String id, int page, int size) {
        Product p = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return new PageImpl<>(p.getUsersThatLiked().stream().map(user -> modelMapper.map(user, UserBasicDTO.class)).collect(Collectors.toList()), PageRequest.of(page, size), p.getUsersThatLiked().size());
    }

    @Override
    public Page<OfferBasicDTO> getProductOffers(String id, int page, int size) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.isAdministrator() && !loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see offers of others product");
        Page<Offer> offers = new PageImpl<Offer>(product.getOffers(), PageRequest.of(page, size), product.getOffers().size());
        List<OfferBasicDTO> collect = offers.stream().map(s -> modelMapper.map(s, OfferBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, PageRequest.of(page, size), offers.getTotalElements());
    }

    @Override
    public Page<MessageDTO> getProductMessages(String id, int page, int size) throws IllegalAccessException {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.isAdministrator() || !loggedUser.getId().equals(product.getSeller().getId()))
            throw new IllegalAccessException("Cannot see messages of others product");
        Page<Message> messages = new PageImpl<Message>(product.getMessages(), PageRequest.of(page, size), product.getMessages().size());
        List<MessageDTO> collect = messages.stream().map(s -> modelMapper.map(s, MessageDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, PageRequest.of(page, size), messages.getTotalElements());
    }

    @Override
    public Page<ProductBasicDTO> getProductFilteredPage(Specification<Product> withFilters, int page, int size, String sortBy, String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String loggedUseridString;
        if (loggedUser != null){
            loggedUseridString = loggedUser.getId();
        } else {
            loggedUseridString = "";
        }

        Page<Product> products = productRepository.findAll(withFilters, pageable);

        List<ProductBasicDTO> collect = products.stream().filter(product -> product.getSeller().getId().equals(loggedUseridString) || product.getSeller().getStatus().equals(UserStatus.ACTIVE)).map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());

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
    public String getCategoryId(String category) {
        String id =  productCatRepository.findProductCategoryByTertiaryCat(category).getId();
        return id;
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
        return switch (productDTO.getProductCategory().getPrimaryCat()) {
            case "Clothing" -> modelMapper.map(productDTO, Clothing.class);
            case "Home" -> modelMapper.map(productDTO, Home.class);
            case "Entertainment" -> modelMapper.map(productDTO, Entertainment.class);
            default -> modelMapper.map(productDTO, Product.class);
        };
    }

    private ProductDTO mapToProductDetailsDTO(Product product) {
        return switch (product.getProductCategory().getPrimaryCat()) {
            case "Clothing" -> modelMapper.map(product, ClothingDTO.class);
            case "Home" -> modelMapper.map(product, HomeDTO.class);
            case "Entertainment" -> modelMapper.map(product, EntertainmentDTO.class);
            default -> modelMapper.map(product, ProductDTO.class);
        };
    }

    private List<ProductBasicDTO> mapToProductBasicDTOList(Page<Product> products) {
        return products.stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
    }

    private ProductBasicDTO mapToProductBasicDTO(Product product) {
        return modelMapper.map(product, ProductBasicDTO.class);
    }


    private void throwOnIdMismatch(String productId, ProductDTO productDTO) {
        if (!productDTO.getId().equals(productId)) {
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
