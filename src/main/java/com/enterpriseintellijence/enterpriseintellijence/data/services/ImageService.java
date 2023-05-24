package com.enterpriseintellijence.enterpriseintellijence.data.services;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    byte[] getUserProfilePhoto(String id) throws IOException;

    byte[] getImageProduct(String id);


    String savePhotoUser(MultipartFile multipartFile, String description) throws IOException;

    void replacePhotoUser(String id, MultipartFile multipartFile) throws IOException;

    void deletePhotoUser(String id) throws IllegalAccessException;

    String saveImageProduct(MultipartFile multipartFile, String product_id, String description) throws IllegalAccessException, IOException;

    void replaceImageProduct(String id, MultipartFile multipartFile) throws IOException;

    void deleteImageProduct(String id) throws IllegalAccessException;
}
