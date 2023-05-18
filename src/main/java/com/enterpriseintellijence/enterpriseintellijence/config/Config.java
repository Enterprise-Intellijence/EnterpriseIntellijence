package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final CollectionSizeToIntConverter collectionSizeToIntConverter;
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true);

        modelMapper.createTypeMap(User.class, UserDTO.class).addMappings(new PropertyMap<User, UserDTO>() {

            Converter<PaymentMethod, PaymentMethodBasicDTO> paymentMethodConverter = new AbstractConverter<PaymentMethod, PaymentMethodBasicDTO>() {
                @Override
                protected PaymentMethodBasicDTO convert(PaymentMethod paymentMethod) {
                    return PaymentMethodBasicDTO.builder()
                            .id(paymentMethod.getId())
                            .creditCard("**** **** **** "+paymentMethod.getCreditCard().substring(15,19))
                            .build();

                }
            };

            @Override
            protected void configure() {
                using(paymentMethodConverter).map(source.getDefaultPaymentMethod(), destination.getDefaultPaymentMethod());
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
