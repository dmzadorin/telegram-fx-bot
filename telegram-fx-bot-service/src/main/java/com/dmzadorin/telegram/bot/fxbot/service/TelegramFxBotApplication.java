package com.dmzadorin.telegram.bot.fxbot.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.dmzadorin.telegram.bot.fxbot.service.config")
public class TelegramFxBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramFxBotApplication.class);
    }
}
