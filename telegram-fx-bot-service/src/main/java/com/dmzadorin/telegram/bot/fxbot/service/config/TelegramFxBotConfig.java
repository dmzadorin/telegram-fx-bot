package com.dmzadorin.telegram.bot.fxbot.service.config;

import com.dmzadorin.telegram.bot.fxbot.common.updatehandlers.FxRatesLongPollingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
@Configuration
public class TelegramFxBotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        ApiContextInitializer.init();
        return new TelegramBotsApi();
    }

    @Bean
    public FxRatesLongPollingBot fxRatesBot(@Value("#{systemProperties.fxBotToken}") String token) {
        return new FxRatesLongPollingBot(token, "fx_rates_poller_bot");
    }

    @Bean(destroyMethod = "stop")
    public BotSession fxBotSession(@Autowired TelegramBotsApi telegramBotsApi, @Autowired FxRatesLongPollingBot fxRatesBox) throws TelegramApiRequestException {
        return telegramBotsApi.registerBot(fxRatesBox);
    }
}
