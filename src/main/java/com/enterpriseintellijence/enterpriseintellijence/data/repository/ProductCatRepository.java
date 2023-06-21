package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCatRepository extends JpaRepository<ProductCategory,String>, JpaSpecificationExecutor<ProductCategory> {

    List<ProductCategory> findAllByPrimaryCat(String primaryCat);
}
