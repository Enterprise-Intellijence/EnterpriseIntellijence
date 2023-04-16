package com.enterpriseintellijence.enterpriseintellijence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CountriesRepositoryTest {

    @Autowired
    private CountriesRepository countriesRepository;


    @Test
    void test() {
        List<Countries> expected = List.of(
            new Countries(1L, "USA"),
            new Countries(2L, "France"),
            new Countries(3L, "Brazil"),
            new Countries(4L, "Italy"),
            new Countries(5L, "Canada")
        );
        Iterable<Countries> all = countriesRepository.findAll();
        assertEquals(expected, all);


    }

}