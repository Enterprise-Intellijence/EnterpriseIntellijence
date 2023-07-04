package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.Demo;
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
        return ip();
    }

    public String ip() {
        try {
            InetAddress localAddress = getLocalAddress();
            return localAddress.getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InetAddress getLocalAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()
                        && address.isSiteLocalAddress()) {
                    return address;
                }
            }
        }
        throw new SocketException("Unable to determine local address");
    }
}
