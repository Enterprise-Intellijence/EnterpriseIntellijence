package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.Demo;
import com.enterpriseintellijence.enterpriseintellijence.security.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
public class DemoController {
    private final Demo demo;

    @RequestMapping("/api/v1/demo")
    public void createDemoData() throws IOException {
        demo.initialize();
    }

    @GetMapping("/api/v1/ip")
    public String getIp() throws UnknownHostException {
        return Constants.BASE_PATH;
    }

}
