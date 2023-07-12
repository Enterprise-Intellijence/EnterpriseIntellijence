package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Currency;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final CollectionSizeToIntConverter collectionSizeToIntConverter;
    private final PaymentMethodConverter paymentMethodConverter;
    private final PaymentMethodConverterFull paymentMethodConverterFull;

    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true);

        modelMapper.createTypeMap(PaymentMethod.class, PaymentMethodBasicDTO.class).setConverter(paymentMethodConverter);

        modelMapper.createTypeMap(PaymentMethod.class, PaymentMethodDTO.class).setConverter(paymentMethodConverterFull);



        Converter<CustomMoneyDTO, CustomMoney> defaultCustomMoneyConverter = new AbstractConverter<CustomMoneyDTO, CustomMoney>() {
            @Override
            protected CustomMoney convert(CustomMoneyDTO customMoneyDTO) {
                return new CustomMoney(customMoneyDTO.getPrice(),customMoneyDTO.getCurrency());
            }
        };

        modelMapper.addConverter(defaultCustomMoneyConverter);



        return modelMapper;
    }


    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }



    private final JwtContextUtils jwtContextUtils;


}
