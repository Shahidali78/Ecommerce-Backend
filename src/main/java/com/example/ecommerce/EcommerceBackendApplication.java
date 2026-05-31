package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the e‑commerce backend application. Running this class
 * boots an embedded Tomcat server and exposes the REST API described in
 * the controllers package. This skeleton is intentionally light on logic
 * to serve as a starting point for building a fully featured system.
 */
@SpringBootApplication
public class EcommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceBackendApplication.class, args);
    }
}