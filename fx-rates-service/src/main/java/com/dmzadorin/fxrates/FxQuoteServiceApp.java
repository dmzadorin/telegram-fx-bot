package com.dmzadorin.fxrates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.dmzadorin.fxrates")
public class FxQuoteServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(FxQuoteServiceApp.class);
    }
}
