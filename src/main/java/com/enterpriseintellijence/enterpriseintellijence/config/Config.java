package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Condition;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final CollectionSizeToIntConverter collectionSizeToIntConverter;
    private final PaymentMethodConverter paymentMethodConverter;

    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true);

        modelMapper.createTypeMap(PaymentMethod.class, PaymentMethodBasicDTO.class).setConverter(paymentMethodConverter);
/*        modelMapper.createTypeMap(User.class, UserDTO.class).setConverter(new AbstractConverter<User, UserDTO>() {
            @Override
            protected UserDTO convert(User user) {
                int followers_number = 0;
                int following_number = 0;
                if(user.getFollowers() != null) {
                    System.out.println("follower_number = " + user.getFollowers().size());
                    followers_number = user.getFollowers().size();
                }

                if(user.getFollowing() != null) {
                    System.out.println("following_number = " + user.getFollowing().size());
                    following_number = user.getFollowing().size();
                }

                return UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .defaultPaymentMethod(null)
                        .address(AddressDTO.builder().city(user.getAddress().getCity())
                                .country(user.getAddress().getCountry())
                                .street(user.getAddress().getStreet())
                                .postalCode(user.getAddress().getPostalCode())
                                .build())
                        .role(user.getRole())
                        .photo(user.getPhoto())
                        .provider(user.getProvider())
                        .followers_number(followers_number)
                        .following_number(following_number)
                        .build();
            }
        });

        modelMapper.createTypeMap(User.class, UserBasicDTO.class).setConverter(new AbstractConverter<User, UserBasicDTO>() {
            @Override
            protected UserBasicDTO convert(User user) {

                return UserBasicDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .photo(user.getPhoto())
                        .followers_number(user.getFollowers().size())
                        .following_number(user.getFollowing().size())
                        .build();
            }
        });*/

        Converter<List<ProductImage>,ProductImageDTO> defaultImageConverter = new AbstractConverter<List<ProductImage>, ProductImageDTO>() {
            @Override
            protected ProductImageDTO convert(List<ProductImage> productImages) {
                return ProductImageDTO.builder()
                        .id(productImages.get(0).getId())
                        .description(productImages.get(0).getDescription())
                        .photo(null)
                        .build();
            }
        };

        modelMapper.addConverter(defaultImageConverter);

        Converter<List<ProductImage>,List<ProductImageDTO>> imageListConverter = new AbstractConverter<List<ProductImage>, List<ProductImageDTO>>() {
            @Override
            protected List<ProductImageDTO> convert(List<ProductImage> productImages) {
                List<ProductImageDTO> productImageDTOS = new ArrayList<>();
                for(ProductImage productImage: productImages){
                    productImageDTOS.add(
                            ProductImageDTO.builder()
                                    .id(productImage.getId())
                                    .description(productImage.getDescription())
                                    .photo(null)
                                    .build()
                    );
                }
                return  productImageDTOS;
            }
        };

        modelMapper.addConverter(imageListConverter);




        Converter<UserImage, UserImageDTO> defaultPhotoProfileConverter = new AbstractConverter<UserImage, UserImageDTO>() {
            @Override
            protected UserImageDTO convert(UserImage userImage) {
                return UserImageDTO.builder()
                        .id(userImage.getId())
                        .description(userImage.getDescription())
                        .photo(null)
                        .build();
            }
        };

        modelMapper.addConverter(defaultPhotoProfileConverter);

        /*modelMapper.createTypeMap(Product.class, ProductBasicDTO.class) .setConverter(new AbstractConverter<Product, ProductBasicDTO>() {
            @Override
            protected ProductBasicDTO convert(Product product) {
                System.out.println("convertendo");
                return ProductBasicDTO.builder()
                        .id(product.getId())
                        .title(product.getTitle())
                        .description(product.getDescription())
                        .customMoney(CustomMoneyDTO.builder()
                                .price(product.getCustomMoney().getPrice())
                                .currency(product.getCustomMoney().getCurrency())
                                .build())
                        .condition(product.getCondition())
                        .likesNumber(product.getLikesNumber())
                        .seller(UserBasicDTO.builder()
                                .id(product.getSeller().getId())
                                .username(product.getSeller().getUsername())
                                .photo(product.getSeller().getPhoto())
                                .followers_number(product.getSeller().getFollowers_number())
                                .following_number(product.getSeller().getFollowing_number())
                                .build()
                            )
                        .defaultImage(ProductImageDTO.builder()
                                .id(product.getProductImages().get(0).getId())
                                .photo(product.getProductImages().get(0).getPhoto())
                                .build()
                        )
                        .productCategory(product.getProductCategory())
                        .build();
            }
        });*/




        return modelMapper;
    }


    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }


    private final JwtContextUtils jwtContextUtils;


}
