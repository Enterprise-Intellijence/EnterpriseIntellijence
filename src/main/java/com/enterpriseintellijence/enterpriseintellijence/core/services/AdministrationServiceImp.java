package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Size;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductCatRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.SizeRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdministrationServiceImp implements AdministrationService{

    private final ProductCatRepository productCategoryRepository;
    private final SizeRepository sizeRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductCategoryDTO createNewCategory(ProductCategoryDTO productCategoryDTO) {
        return mapToDTO(productCategoryRepository.save(mapToEntity(productCategoryDTO)));
    }

    @Override
    public void deleteCategory(String catId) {
        productCategoryRepository.deleteById(catId);
    }

    @Override
    public ProductCategoryDTO replaceProductCategory(String catId, ProductCategoryDTO productCategoryDTO) {
        productCategoryRepository.findById(catId).orElseThrow();
        productCategoryDTO.setId(catId);
        return mapToDTO(productCategoryRepository.save(mapToEntity(productCategoryDTO)));
    }

    @Override
    public SizeDTO createNewSize(SizeDTO sizeDTO) {
        return mapToDTO(sizeRepository.save(mapToEntity(sizeDTO)));
    }

    @Override
    public SizeDTO replaceSize(String sizeId, SizeDTO sizeDTO) {
        sizeRepository.findById(sizeId).orElseThrow();
        sizeDTO.setId(sizeId);
        return mapToDTO(sizeRepository.save(mapToEntity(sizeDTO)));
    }

    @Override
    public void deleteSize(String sizeId) {
        sizeRepository.deleteById(sizeId);
    }

    @Override
    public void productCategoryEnableOrDisable(String catId) {

    }

    ProductCategory mapToEntity(ProductCategoryDTO productCategoryDTO){
        return modelMapper.map(productCategoryDTO,ProductCategory.class);
    }

    ProductCategoryDTO mapToDTO(ProductCategory productCategory){
        return modelMapper.map(productCategory,ProductCategoryDTO.class);
    }

    Size mapToEntity(SizeDTO sizeDTO){
        return modelMapper.map(sizeDTO,Size.class);
    }

    SizeDTO mapToDTO(Size size){
        return modelMapper.map(size,SizeDTO.class);
    }
}
