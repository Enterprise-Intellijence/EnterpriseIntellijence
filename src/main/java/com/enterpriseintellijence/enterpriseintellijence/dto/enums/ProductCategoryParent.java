package com.enterpriseintellijence.enterpriseintellijence.dto.enums;

public enum ProductCategoryParent {
    CLOTHS("CLOTHS",ProductCategory.CLOTHING),
    SHOES("SHOES",ProductCategory.CLOTHING),
    BAGS("BAGS",ProductCategory.CLOTHING),
    ACCESSORIES("ACCESSORIES",ProductCategory.CLOTHING),
    VIDEOGAMES("VIDEOGAMES",ProductCategory.ENTERTAINMENT),
    MEDIA("MEDIA",ProductCategory.ENTERTAINMENT),
    BOOKS("BOOKS",ProductCategory.ENTERTAINMENT),
    TEXTILES("TEXTILES",ProductCategory.HOME),
    HOME("HOME",ProductCategory.HOME),
    TABLEWARE("TABLEWARE",ProductCategory.HOME),
    OTHER("OTHER",ProductCategory.OTHER );

    public String parentProductCategory;
    public ProductCategory productCategory;

    ProductCategoryParent(String parentProductCategory, ProductCategory productCategory){
        this.parentProductCategory = parentProductCategory;
        this.productCategory=productCategory;
    }

    public String getParentProductCategory() {
        return parentProductCategory;
    }

    public void setParentProductCategory(String parentProductCategory) {
        this.parentProductCategory = parentProductCategory;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
}
