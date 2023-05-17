package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CollectionSizeToIntConverter extends AbstractConverter<Set<User>, Integer> {

    @Override
    protected Integer convert(Set<User> hashSet) {
        if(hashSet != null) {
            return hashSet.size();
        } else {
            return 0;
        }
    }
}
