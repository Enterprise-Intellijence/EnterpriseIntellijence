package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.UserImage;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService{
    private final ProductImageRepository productImageRepository;
    private final UserImageRepository userImageRepository;

    @Override
    public byte[] getUserProfilePhoto(String id) {
        UserImage temp = userImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return temp.getPhoto();
    }

    @Override
    public byte[] getProductCardPhoto(String id) {
        ProductImage temp = productImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return temp.getPhoto();
    }


}
