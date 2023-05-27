package com.enterpriseintellijence.enterpriseintellijence.dto.enums;

public enum ClothingSize {

    _3XS("3XS",ProductCategoryParent.CLOTHS),
    _2XS("2XS",ProductCategoryParent.CLOTHS),
    _XS("XS",ProductCategoryParent.CLOTHS),
    _S("S",ProductCategoryParent.CLOTHS),
    _M("M",ProductCategoryParent.CLOTHS),
    _L("L",ProductCategoryParent.CLOTHS),
    _XL("XL",ProductCategoryParent.CLOTHS),
    _2XL("2XL",ProductCategoryParent.CLOTHS),
    _3L("3L",ProductCategoryParent.CLOTHS),
    _4XL("4XL",ProductCategoryParent.CLOTHS),
    _25("25",ProductCategoryParent.SHOES),
    _26("26",ProductCategoryParent.SHOES),
    _27("27",ProductCategoryParent.SHOES),
    _28("28",ProductCategoryParent.SHOES),
    _29("29",ProductCategoryParent.SHOES),
    _30("30",ProductCategoryParent.SHOES),
    _31("31",ProductCategoryParent.SHOES),
    _32("32",ProductCategoryParent.SHOES),
    _33("33",ProductCategoryParent.SHOES),
    _34("34",ProductCategoryParent.SHOES),
    _35("35",ProductCategoryParent.SHOES),
    _36("36",ProductCategoryParent.SHOES),
    _37("37",ProductCategoryParent.SHOES),
    _38("38",ProductCategoryParent.SHOES),
    _39("39",ProductCategoryParent.SHOES),
    _40("40",ProductCategoryParent.SHOES),
    _41("41",ProductCategoryParent.SHOES),
    _42("42",ProductCategoryParent.SHOES),
    _43("43",ProductCategoryParent.SHOES),
    _44("44",ProductCategoryParent.SHOES),
    _45("45",ProductCategoryParent.SHOES),
    _46("46",ProductCategoryParent.SHOES),
    _47("47",ProductCategoryParent.SHOES),
    _48("48",ProductCategoryParent.SHOES),
    _49("49",ProductCategoryParent.SHOES),
    _50("50",ProductCategoryParent.SHOES),
    SMALL_BAG("SMALL_BAG",ProductCategoryParent.BAGS),
    MEDIUM_BAG("MEDIUM_BAG",ProductCategoryParent.BAGS),
    BIG_BAG("BIG_BAG",ProductCategoryParent.BAGS),
    SMALL_ACC("SMALL_ACC",ProductCategoryParent.ACCESSORIES),
    MEDIUM_ACC("MEDIUM_ACC",ProductCategoryParent.ACCESSORIES),
    BIG_ACC("BIG_ACC",ProductCategoryParent.ACCESSORIES);

    public String size;
    public ProductCategoryParent productCategoryParent;

    ClothingSize(String size, ProductCategoryParent productCategoryParent){
        this.size = size;
        this.productCategoryParent=productCategoryParent;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ProductCategoryParent getProductCategoryParent() {
        return productCategoryParent;
    }

    public void setProductCategoryParent(ProductCategoryParent productCategoryParent) {
        this.productCategoryParent = productCategoryParent;
    }
}
