package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.Demo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DemoController {
    private final Demo demo;

    @RequestMapping("/api/v1/demo")
    public void createDemoData() throws IOException {
        demo.initialize();
    }
}
