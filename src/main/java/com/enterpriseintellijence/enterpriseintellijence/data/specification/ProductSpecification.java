package com.enterpriseintellijence.enterpriseintellijence.data.specification;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductSpecification {

    @Data
    public static class Filter {
        private final UserRepository userRepository;

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
        private ProductCategoryParent productCategoryParent;
        private ProductCategoryChild productCategoryChild;
        private Integer likesNumber;
        private User seller;
        private ProductGender productGender;

        public void setSeller(String userID) {
            Optional<User> user = userRepository.findById(userID);
            if(user.isPresent())
                this.seller = user.get();
        }
    }

    public static Specification<Product> withFilters(Filter filter) {
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("title"), filter.getTitle()));
                }

                if (filter.getDescription() != null && !filter.getDescription().isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("description"), filter.getDescription()));
                }
                // TODO: 28/05/2023 fixare 
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
                    predicates.add(criteriaBuilder.equal(root.get("views"), filter.getViews()));
                }

                if (filter.getUploadDate() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("uploadDate"), filter.getUploadDate()));
                }

                if (filter.getAvailability() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("availability"), Availability.AVAILABLE));
                }

                if (filter.getProductCategory() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("productCategory"), filter.getProductCategory()));
                }

                if (filter.getProductCategoryParent() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("productCategoryParent"), filter.getProductCategoryParent()));
                }

                if (filter.getProductCategoryChild() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("productCategoryChild"), filter.getProductCategoryChild()));
                }

                if(filter.getProductGender()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("productGender"),filter.getProductGender()));
                }

                if (filter.getLikesNumber() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("likesNumber"), filter.getLikesNumber()));
                }

                if (filter.getSeller() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("seller"), filter.getSeller()));
                }

                predicates.add(criteriaBuilder.equal(root.get("visibility"), Visibility.PUBLIC));

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
