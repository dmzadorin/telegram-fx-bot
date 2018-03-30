package com.dmzadorin.telegram.fxbot.service.config;

import com.dmzadorin.telegram.fxbot.service.commands.GetRatesCommand;
import com.dmzadorin.telegram.fxbot.service.commands.HelloCommand;
import com.dmzadorin.telegram.fxbot.service.commands.StartCommand;
import com.dmzadorin.telegram.fxbot.service.rates.FxQuoteClient;
import com.dmzadorin.telegram.fxbot.service.rates.FxQuoteClientImpl;
import com.dmzadorin.telegram.fxbot.service.updatehandlers.FxRatesLongPollingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
@Configuration
public class TelegramFxBotConfig {
    static {
        BotLogger.setLevel(Level.INFO);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe("TelegramFxBotConfig", e);
        }
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        ApiContextInitializer.init();
        return new TelegramBotsApi();
    }

    @Bean
    public BotCommand helloCommand() {
        return new HelloCommand();
    }

    @Bean
    public BotCommand startCommand() {
        return new StartCommand();
    }

    @Bean
    public BotCommand ratesCommand(@Autowired FxQuoteClient fxQuoteClient) {
        GetRatesCommand ratesCommand = new GetRatesCommand();
        ratesCommand.setFxQuoteClient(fxQuoteClient);
        return ratesCommand;
    }

    @Bean
    public FxQuoteClient fxQuoteClient(@Value("${quote-service.host}") String host, @Value("${quote-service.port}") int port){
        return new FxQuoteClientImpl(host, port);
    }

    @Bean
    public Collection<BotCommand> availableCommands(@Autowired @Qualifier("ratesCommand") BotCommand ratesCommand) {
        return Arrays.asList(helloCommand(), startCommand(), ratesCommand);
    }

    @Bean
    public FxRatesLongPollingBot fxRatesBot(
            @Value("${telegram.fxBotToken}") String token,
            @Autowired Collection<BotCommand> availableCommands) {
        return new FxRatesLongPollingBot(token, "fx_rates_poller_bot", availableCommands);
    }

    @Bean(destroyMethod = "stop")
    public BotSession fxBotSession(@Autowired TelegramBotsApi telegramBotsApi,
                                   @Autowired FxRatesLongPollingBot fxRatesBot) throws TelegramApiRequestException {
        return telegramBotsApi.registerBot(fxRatesBot);
    }
}
