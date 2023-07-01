package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.Constants;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductImageDTO {
    private String id;
    private String description;
    private String urlPhoto;

    @JsonSetter
    public void setUrlPhoto(String url) {
        if(url != null && !url.startsWith("http"))
            this.urlPhoto = Constants.BASE_PATH + url;
        else
            this.urlPhoto = url;
    }
}
