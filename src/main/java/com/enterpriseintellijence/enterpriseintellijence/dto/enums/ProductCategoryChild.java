package com.enterpriseintellijence.enterpriseintellijence.dto.enums;

public enum ProductCategoryChild {
    JEANS("JEANS", ProductCategoryParent.CLOTHS),
    DRESS("DRESS", ProductCategoryParent.CLOTHS),
    SKIRT("SKIRT", ProductCategoryParent.CLOTHS),
    T_SHIRT("T_SHIRT", ProductCategoryParent.CLOTHS),
    SWEATERS("SWEATERS", ProductCategoryParent.CLOTHS),
    TROUSERS("TROUSERS", ProductCategoryParent.CLOTHS),
    BOOTS("BOOTS", ProductCategoryParent.SHOES),
    HEELS("HEELS", ProductCategoryParent.SHOES),
    SPORT("SPORT", ProductCategoryParent.SHOES),
    TRAINERS("TRAINERS", ProductCategoryParent.SHOES),
    SANDALS("SANDALS", ProductCategoryParent.SHOES),
    SHOULDER_BAG("SHOULDER_BAG", ProductCategoryParent.BAGS),
    HANDBAGS("HANDBAGS", ProductCategoryParent.BAGS),
    LUGGAGE("LUGGAGE", ProductCategoryParent.BAGS),
    BACKPACKS("BACKPACKS", ProductCategoryParent.BAGS),
    WATCHES("WATCHES", ProductCategoryParent.ACCESSORIES),
    SUNGLASSES("SUNGLASSES", ProductCategoryParent.ACCESSORIES),
    BELTS("BELTS", ProductCategoryParent.ACCESSORIES),
    HATS_CAPS("HATS_CAPS", ProductCategoryParent.ACCESSORIES),
    CONSOLES_XBOXONE("CONSOLES_XBOXONE", ProductCategoryParent.VIDEOGAMES),
    CONSOLES_PLAYSTATION_FIVE("CONSOLES_PLAYSTATION_FIVE", ProductCategoryParent.VIDEOGAMES),
    CONSOLES_PLAYSTATION_OLDER("CONSOLES_PLAYSTATION_OLDER", ProductCategoryParent.VIDEOGAMES),
    CONSOLES_PCGAMES("CONSOLES_PCGAMES", ProductCategoryParent.VIDEOGAMES),
    MUSIC("MUSIC", ProductCategoryParent.MEDIA),
    VIDEO("VIDEO", ProductCategoryParent.MEDIA),
    NON_FICTION("NON_FICTION", ProductCategoryParent.BOOKS),
    KIDS("NON_FICTION", ProductCategoryParent.BOOKS),
    LITERATURE("NON_FICTION", ProductCategoryParent.BOOKS),
    FICTION("NON_FICTION", ProductCategoryParent.BOOKS);

    public String childProductCategory;
    public ProductCategoryParent productCategoryParent;

    ProductCategoryChild(String childProductCategory, ProductCategoryParent productCategoryParent){
        this.childProductCategory = childProductCategory;
        this.productCategoryParent = productCategoryParent;
    }

    public String getChildProductCategory() {
        return childProductCategory;
    }

    public void setChildProductCategory(String childProductCategory) {
        this.childProductCategory = childProductCategory;
    }

    public ProductCategoryParent getSubCategoryType() {
        return productCategoryParent;
    }

    public void setSubCategoryType(ProductCategoryParent productCategoryParent) {
        this.productCategoryParent = productCategoryParent;
    }
}
