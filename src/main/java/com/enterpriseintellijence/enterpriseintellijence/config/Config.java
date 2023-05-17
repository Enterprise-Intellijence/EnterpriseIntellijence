package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class Config {

    @Bean
    public ModelMapper getModelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true);

        /*modelMapper.createTypeMap(User.class, UserDTO.class).addMappings(new PropertyMap<User, UserDTO>() {
            @Override
            protected void configure() {
                map().setUsername(source.getUsername());
                map().setEmail(source.getEmail());
                map().setPhoto(source.getPhoto());
                map().setProvider(source.getProvider());
                map().setAddress(modelMapper.map(source.getAddress(), AddressDTO.class));
                map().setRole(source.getRole());
                map().setDefaultPaymentMethod(modelMapper.map(source.getDefaultPaymentMethod(), PaymentMethodDTO.class));
                map().setFollowers(source.getFollowers().size());
                map().setFollows(source.getFollows().size());
                map().setSellingProducts(source.getSellingProducts().stream()
                        .map(product -> modelMapper.map(product, ProductBasicDTO.class)).collect(Collectors.toList()));
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
