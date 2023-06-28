package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.security.Constants;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserImageDTO {
    private String id;
    private String description;
    private String urlPhoto;

    @JsonSetter
    public void setUrlPhoto(String url) {
        if(url.contains("localhost"))
            this.urlPhoto = Constants.BASE_PATH + url;
        else
            this.urlPhoto = url;
    }
}
