package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.FileUploadUtil;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.UserImage;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService{
    private static String userDir="src/main/resources/user_photos/";
    private static String prodDir="src/main/resources/product_photos/";


    private final ProductImageRepository productImageRepository;
    private final UserImageRepository userImageRepository;
    private final ProductRepository productRepository;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public String savePhotoUser(MultipartFile multipartFile, String description) throws IOException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String uploadDir = userDir + loggedUser.getUsername();
        UserImage userImage = new UserImage();
        userImage.setDescription(description);
        userImage.setUrlPhoto(uploadDir+"/"+fileName);
        userImage.setUser(loggedUser);
        loggedUser.setPhotoProfile(userImage);

        // TODO: 24/05/2023 boolean for check save

        userImage= userImageRepository.save(userImage);


        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return userImage.getId();
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
    public byte[] getUserProfilePhoto(String id) throws IOException {
        UserImage temp = userImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        try{
            BufferedImage bufferedImage = ImageIO.read(new File(temp.getUrlPhoto()));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            return (bos.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
            // TODO: 24/05/2023 gestione errore

            return null;
        }
    }

    @Override
    public String saveImageProduct(MultipartFile multipartFile, String product_id, String description) throws IllegalAccessException, IOException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productRepository.findById(product_id).orElseThrow(EntityNotFoundException::new);
        if(loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("cannot upload image for others product");

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String uploadDir = prodDir + product_id;
        ProductImage productImage = new ProductImage();
        productImage.setDescription(description);
        productImage.setUrlPhoto(uploadDir+"/"+fileName);
        productImage.setProduct(product);

        // TODO: 24/05/2023 boolean for check save

        productImage= productImageRepository.save(productImage);


        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return productImage.getId();
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
    public byte[] getImageProduct(String id) {
        ProductImage temp = productImageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        try{
            BufferedImage bufferedImage = ImageIO.read(new File(temp.getUrlPhoto()));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            return (bos.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
            // TODO: 24/05/2023 gestione errore

            return null;
        }
    }





}
