package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;
import org.springframework.stereotype.Service;

@Service
public class AdministrationServiceImp implements AdministrationService{

    @Override
    public ProductCategoryDTO createNewCategory(ProductCategoryDTO productCategoryDTO) {
        return null;
    }

    @Override
    public void deleteCategory(String catId) {

    }

    @Override
    public ProductCategoryDTO replaceProductCategory(String catId, ProductCategoryDTO productCategoryDTO) {
        return null;
    }

    @Override
    public SizeDTO createNewSize(SizeDTO sizeDTO) {
        return null;
    }

    @Override
    public SizeDTO replaceSize(String sizeId, SizeDTO sizeDTO) {
        return null;
    }

    @Override
    public void deleteSize(String sizeId) {

    }

    @Override
    public void productCategoryEnableOrDisable(String catId) {

    }
}
