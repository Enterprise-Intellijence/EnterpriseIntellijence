package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.*;
import org.modelmapper.builder.ConfigurableConditionExpression;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Set;

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


        //modelMapper.createTypeMap(PaymentMethod.class, PaymentMethodBasicDTO.class).setConverter(paymentMethodConverter);
        modelMapper.createTypeMap(User.class, UserDTO.class).setConverter(new AbstractConverter<User, UserDTO>() {
            @Override
            protected UserDTO convert(User user) {
                int followers_number = 0;
                int following_number = 0;
                if(user.getFollowers() != null) {
                    followers_number = user.getFollowers().size();
                }

                if(user.getFollowing() != null) {

                    System.out.println("following_number = " + user.getFollowing());
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


        return modelMapper;
    }


    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }


    private final JwtContextUtils jwtContextUtils;


}
