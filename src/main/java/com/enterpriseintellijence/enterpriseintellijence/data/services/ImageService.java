package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.dto.ProductImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    byte[] getUserProfilePhoto(String id) throws IOException;

    byte[] getImageProduct(String id);


    UserImageDTO savePhotoUser(MultipartFile multipartFile, String description) throws IOException;

    void replacePhotoUser(String id, MultipartFile multipartFile) throws IOException;

    void deletePhotoUser(String id) throws IllegalAccessException;

    ProductImageDTO saveImageProduct(MultipartFile multipartFile, String product_id, String description) throws IllegalAccessException, IOException;

    void replaceImageProduct(String id, MultipartFile multipartFile) throws IOException;

    void deleteImageProduct(String id) throws IllegalAccessException;
}
