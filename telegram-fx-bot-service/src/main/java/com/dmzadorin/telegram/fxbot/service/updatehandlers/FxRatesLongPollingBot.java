package com.dmzadorin.telegram.fxbot.service.updatehandlers;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class FxRatesLongPollingBot extends TelegramLongPollingCommandBot {
    private static final String LOGTAG = "FXRATESHANDLER";
    private final String botToken;
    private final Collection<BotCommand> availableCommands;

    public FxRatesLongPollingBot(String botToken, String botUserName, Collection<BotCommand> availableCommands) {
        super(botUserName);
        this.botToken = botToken;
        this.availableCommands = availableCommands;
    }

    @PostConstruct
    public void init() {
        availableCommands.forEach(super::register);
        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot.");
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(message.getChatId());
                echoMessage.setText("Hey heres your message:\n" + message.getText());

                try {
                    execute(echoMessage);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
