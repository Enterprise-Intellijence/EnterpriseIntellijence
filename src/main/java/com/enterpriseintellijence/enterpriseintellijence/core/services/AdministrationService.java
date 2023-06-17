package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;

public interface AdministrationService {
    ProductCategoryDTO createNewCategory(ProductCategoryDTO productCategoryDTO);

    void deleteCategory(String catId);

    ProductCategoryDTO replaceProductCategory(String catId, ProductCategoryDTO productCategoryDTO);

    SizeDTO createNewSize(SizeDTO sizeDTO);

    SizeDTO replaceSize(String sizeId, SizeDTO sizeDTO);

    void deleteSize(String sizeId);

    void productCategoryEnableOrDisable(String catId);
}
