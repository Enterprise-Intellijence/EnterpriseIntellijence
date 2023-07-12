package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.FileUploadUtil;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.UserImage;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService{
    private static final String userDir = System.getProperty("user.dir") + "/images/user_photos/";
    private static final String prodDir = System.getProperty("user.dir") + "/images/product_photos/";
    private static final String imagesGetDir = System.getProperty("user.dir")+"/images/";


    private final ProductImageRepository productImageRepository;
    private final UserImageRepository userImageRepository;
    private final ProductRepository productRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;

    @Override
    public Resource getImage(String url) throws IOException {
        try{
            String myPath=imagesGetDir+url;

            Path filePath = Paths.get(myPath);

            return new FileSystemResource(filePath);
        }catch (Exception e){
            e.printStackTrace();
            throw new IOException("Something gone wrong, try again.");
        }
    }



    @Override
    public UserImageDTO savePhotoUser(MultipartFile multipartFile, String description) throws IOException, IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getPhotoProfile()!=null)
            throw new IllegalAccessException("Cannot upload more photo, replace previous photo");

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        String localStorageDir = userDir + loggedUser.getUsername();
        UserImage userImage = new UserImage();
        userImage.setDescription(description);
        userImage.setUrlPhoto("images/user_photos/"+loggedUser.getUsername()+"/"+fileName);
        userImage.setUser(loggedUser);
        loggedUser.setPhotoProfile(userImage);

        FileUploadUtil.saveFile(localStorageDir, fileName, multipartFile);
        userImage= userImageRepository.save(userImage);

        return modelMapper.map(userImage,UserImageDTO.class);
    }

    @Override
    public void replacePhotoUser(String id, MultipartFile multipartFile) throws IOException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        UserImage userImage = userImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        String uploadDir = userDir + loggedUser.getUsername();
        // TODO: 24/05/2023 remove previous image


        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        userImage.setUrlPhoto(uploadDir+"/"+fileName);
        userImageRepository.save(userImage);
    }

    @Override
    public void deletePhotoUser(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        UserImage userImage = userImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getPhotoProfile().getId().equals(userImage.getId()))
            throw new IllegalAccessException("Cannot delete image of others");
        userImage.setUser(null);
        loggedUser.setPhotoProfile(null);

        // TODO: 24/05/2023 remove from system
        userImageRepository.delete(userImage);
    }



    @Override
    public ProductImageDTO saveImageProduct(MultipartFile multipartFile, String product_id, String description) throws IllegalAccessException, IOException {

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productRepository.findById(product_id).orElseThrow(EntityNotFoundException::new);
        if(loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("cannot upload image for others product");

        ProductImage productImage = new ProductImage();


        return localProductImageSave(product,multipartFile,productImage,description);

    }

    public ProductImageDTO localProductImageSave(Product product, MultipartFile multipartFile, ProductImage productImage,String description) {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(":", "");

        try{
        String localStorageDir = prodDir +product.getId();
        productImage.setDescription(description);
        productImage.setUrlPhoto("images/product_photos/"+product.getId()+"/"+fileName);
        productImage.setProduct(product);
        FileUploadUtil.saveFile(localStorageDir, fileName, multipartFile);
        productImageRepository.save(productImage);

        return modelMapper.map(productImage,ProductImageDTO.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void replaceImageProduct(String id, MultipartFile multipartFile) throws IOException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        ProductImage productImage = productImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        // TODO: 24/05/2023 remove previous image
        String uploadDir = prodDir + loggedUser.getUsername();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        productImage.setUrlPhoto(uploadDir+"/"+fileName);
        productImageRepository.save(productImage);

    }

    @Override
    public void deleteImageProduct(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        ProductImage productImage = productImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = productImage.getProduct();

        if(loggedUser.getRole().equals(UserRole.USER) && (!loggedUser.getId().equals(product.getSeller().getId()) || !product.getProductImages().contains(productImage)))
            throw new IllegalAccessException("Cannot delete image of others");


        // TODO: 24/05/2023 remove from system
        productImageRepository.delete(productImage);

    }

    @Override
    public void saveUserImage(UserImage userImage) {
        userImageRepository.save(userImage);
    }

}
