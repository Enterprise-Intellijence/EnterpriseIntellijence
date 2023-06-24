package com.enterpriseintellijence.enterpriseintellijence.data.specification;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.SizeRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import jakarta.persistence.criteria.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductSpecification {

    @Data
    public static class Filter {
        private final UserRepository userRepository;
        private final SizeRepository sizeRepository;

        private String title;
        private String description;
        private Double minProductCost;
        private Double maxProductCost;
        private List<String> brands;
        private Condition condition;
        private Integer views;
        private LocalDateTime uploadDate;
        private Availability availability;
        private ProductCategory productCategory;
        private String primaryCat;
        private String secondaryCat;
        private String tertiaryCat;
        private Integer likesNumber;
        private User seller;
        private ProductGender productGender;
        private List<String> sizes;
        private Colour colour;
        private EntertainmentLanguage entertainmentLanguage;
        private HomeMaterial homeMaterial;

        public void setSeller(String userID) {
            if(userID!=null){
                Optional<User> user = userRepository.findById(userID);
                this.seller = user.get();
            }
        }

    }

    public static Specification<Product> withFilters(Filter filter) {
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                try{
                    List<Predicate> predicates = new ArrayList<>();

                    //ATTRIBUTI COMUNI
                    if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("title"), filter.getTitle()));
                    }

                    if (filter.getDescription() != null && !filter.getDescription().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("description"), filter.getDescription()));
                    }
                    if (filter.getMinProductCost() != null && filter.getMaxProductCost() != null) {
                        predicates.add(criteriaBuilder.between(root.get("productCost").get("price"), filter.getMinProductCost(), filter.getMaxProductCost()));
                    }

                    if (filter.getBrands() != null && !filter.getBrands().isEmpty()) {
                        predicates.add(root.get("brand").in(filter.getBrands()));
                    }

                    if (filter.getCondition() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("condition"), filter.getCondition()));
                    }

                    if (filter.getViews() != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("views"), filter.getViews()));
                    }

                    if (filter.getUploadDate() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("uploadDate"), filter.getUploadDate()));
                    }

                    /*if (filter.getAvailability() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("availability"), Availability.AVAILABLE));
                    }*/

                    if(filter.getProductCategory()!=null)
                        predicates.add(criteriaBuilder.equal(root.get("productCategory"),filter.getProductCategory()));

                    if (filter.getPrimaryCat() != null && !filter.getPrimaryCat().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("productCategory").get("primaryCat"), filter.getPrimaryCat()));
                    }

                    if (filter.getSecondaryCat() != null && !filter.getSecondaryCat().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("productCategory").get("secondaryCat"), filter.getSecondaryCat()));
                    }

                    if (filter.getTertiaryCat() != null && !filter.getTertiaryCat().isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("productCategory").get("tertiaryCat"), filter.getTertiaryCat()));
                    }
                    if (filter.getLikesNumber() != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("likesNumber"), filter.getLikesNumber()));
                    }
                    if (filter.getSeller() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("seller"), filter.getSeller()));
                    }
                    //predicates.add(criteriaBuilder.equal(root.get("visibility"), Visibility.PUBLIC));


                    //ATTRIBUTI DELLA CLASSE Clothing.class
                    if (filter.getPrimaryCat()!=null && filter.getPrimaryCat().equals("Clothing")) {
                        Root<Clothing> clothingRoot = query.from(Clothing.class);
                        predicates.add(criteriaBuilder.equal(root.get("id"), clothingRoot.get("id")));

                        if (filter.getProductGender() != null) {
                            predicates.add(criteriaBuilder.equal(clothingRoot.get("productGender"), filter.getProductGender()));
                        }

                        if (filter.getSizes() != null && !filter.getSizes().isEmpty()) {
                            Join<Clothing, Size> sizeJoin = clothingRoot.join("clothingSize");
                            predicates.add(sizeJoin.get("id").in(filter.getSizes()));
                        }

                        if (filter.getColour() != null) {
                            predicates.add(criteriaBuilder.equal(clothingRoot.get("colour"), filter.getColour()));
                        }
                    }

                    //ATTRIBUTO DELLA CLASSE Entertainment.class
                    if (filter.getPrimaryCat()!=null && filter.getPrimaryCat().equals("Entertainment")) {
                        Root<Entertainment> entertainmentRoot = query.from(Entertainment.class);
                        predicates.add(criteriaBuilder.equal(root.get("id"), entertainmentRoot.get("id")));

                        if (filter.getEntertainmentLanguage() != null) {
                            predicates.add(criteriaBuilder.equal(entertainmentRoot.get("entertainmentLanguage"), filter.getEntertainmentLanguage()));
                        }
                    }

                    //ATTRIBUTI SPECIFICI DELLA CLASSE Home.class
                    if (filter.getPrimaryCat()!=null && filter.getPrimaryCat().equals("Home")) {
                        Root<Home> homeRoot = query.from(Home.class);
                        predicates.add(criteriaBuilder.equal(root.get("id"), homeRoot.get("id")));

                        if (filter.getHomeMaterial() != null) {
                            predicates.add(criteriaBuilder.equal(homeRoot.get("homeMaterial"), filter.getHomeMaterial()));
                        }

                        if (filter.getColour() != null) {
                            predicates.add(criteriaBuilder.equal(homeRoot.get("colour"), filter.getColour()));
                        }

                        if (filter.getSizes() != null && !filter.getSizes().isEmpty()) {
                            Join<Home, Size> sizeJoin = homeRoot.join("homeSize");
                            predicates.add(sizeJoin.get("id").in(filter.getSizes()));
                        }
                    }
/*                    if(filter.getSizes()!=null && !filter.getSizes().isEmpty() ){
                        predicates.add(root.get("homeSize").get("id").in(filter.getSizes()));
                    }*/





                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

            }
        };
    }
}
