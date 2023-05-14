//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enterpriseintellijence.enterpriseintellijence.dto;

import java.util.Arrays;

public class ProductImageDTO {
    private String id;
    private byte[] photo;
    private ProductDTO productDTO;

    public static ProductImageDTO.ProductImageDTOBuilder builder() {
        return new ProductImageDTO.ProductImageDTOBuilder();
    }

    public String getId() {
        return this.id;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public ProductDTO getProductDTO() {
        return this.productDTO;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setPhoto(final byte[] photo) {
        this.photo = photo;
    }

    public void setProductDTO(final ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ProductImageDTO)) {
            return false;
        } else {
            ProductImageDTO other = (ProductImageDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label39: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label39;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label39;
                    }

                    return false;
                }

                if (!Arrays.equals(this.getPhoto(), other.getPhoto())) {
                    return false;
                } else {
                    Object this$productDTO = this.getProductDTO();
                    Object other$productDTO = other.getProductDTO();
                    if (this$productDTO == null) {
                        if (other$productDTO != null) {
                            return false;
                        }
                    } else if (!this$productDTO.equals(other$productDTO)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProductImageDTO;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $id = this.getId();
        int result = result * 59 + ($id == null ? 43 : $id.hashCode());
        result = result * 59 + Arrays.hashCode(this.getPhoto());
        Object $productDTO = this.getProductDTO();
        result = result * 59 + ($productDTO == null ? 43 : $productDTO.hashCode());
        return result;
    }

    public ProductImageDTO() {
    }

    public ProductImageDTO(final String id, final byte[] photo, final ProductDTO productDTO) {
        this.id = id;
        this.photo = photo;
        this.productDTO = productDTO;
    }

    public String toString() {
        String var10000 = this.getId();
        return "ProductImageDTO(id=" + var10000 + ", photo=" + Arrays.toString(this.getPhoto()) + ", productDTO=" + String.valueOf(this.getProductDTO()) + ")";
    }

    public static class ProductImageDTOBuilder {
        private String id;
        private byte[] photo;
        private ProductDTO productDTO;

        ProductImageDTOBuilder() {
        }

        public ProductImageDTO.ProductImageDTOBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public ProductImageDTO.ProductImageDTOBuilder photo(final byte[] photo) {
            this.photo = photo;
            return this;
        }

        public ProductImageDTO.ProductImageDTOBuilder productDTO(final ProductDTO productDTO) {
            this.productDTO = productDTO;
            return this;
        }

        public ProductImageDTO build() {
            return new ProductImageDTO(this.id, this.photo, this.productDTO);
        }

        public String toString() {
            String var10000 = this.id;
            return "ProductImageDTO.ProductImageDTOBuilder(id=" + var10000 + ", photo=" + Arrays.toString(this.photo) + ", productDTO=" + String.valueOf(this.productDTO) + ")";
        }
    }
}
