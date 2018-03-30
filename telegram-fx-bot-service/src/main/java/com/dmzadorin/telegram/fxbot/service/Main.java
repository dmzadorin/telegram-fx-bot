package com.dmzadorin.telegram.fxbot.service;

import com.dmzadorin.telegram.fxbot.service.commands.GetRatesCommand;
import com.dmzadorin.telegram.fxbot.service.commands.HelloCommand;
import com.dmzadorin.telegram.fxbot.service.commands.StartCommand;
import com.dmzadorin.telegram.fxbot.service.updatehandlers.FxRatesLongPollingBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * Created by Dmitry Zadorin on 08.07.2017.
 */
public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) throws IOException {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
                Collection<BotCommand> availableCommands =
                        Arrays.asList(new HelloCommand(), new StartCommand(), new GetRatesCommand());
                FxRatesLongPollingBot ratesPollerBot = new FxRatesLongPollingBot(System.getProperty("fxBotToken"),
                        "fx_rates_poller_bot", availableCommands);
                BotSession botSession = telegramBotsApi.registerBot(ratesPollerBot);
                System.out.println("Starting bot session");
                while (true) {
                    Scanner sc = new Scanner(System.in);
                    boolean stop = false;
                    switch (sc.next()) {
                        case "stop":
                            stop = true;
                            System.out.println("Stopping bot session");
                            break;
                        default:
                            System.out.println("Unrecognized command");
                            break;
                    }
                    if (stop) {
                        break;
                    }
                }
                botSession.stop();
                System.out.println("Bot stopped");
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
