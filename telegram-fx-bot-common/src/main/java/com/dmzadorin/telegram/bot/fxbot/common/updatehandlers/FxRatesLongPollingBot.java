package com.dmzadorin.telegram.bot.fxbot.common.updatehandlers;

import com.dmzadorin.telegram.bot.fxbot.common.commands.GetRatesCommand;
import com.dmzadorin.telegram.bot.fxbot.common.commands.HelloCommand;
import com.dmzadorin.telegram.bot.fxbot.common.commands.StartCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.stream.Stream;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class FxRatesLongPollingBot extends TelegramLongPollingCommandBot {
    private static final String LOGTAG = "FXRATESHANDLER";
    private final String botToken;

    public FxRatesLongPollingBot(String botToken, String botUserName) {
        super(botUserName);
        this.botToken = botToken;
        init();
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
                    sendMessage(echoMessage);
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

    private void init() {
        getAvailableCommands().forEach(super::register);
        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot.");
            try {
                absSender.sendMessage(commandUnknownMessage);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        });
    }

    private Stream<BotCommand> getAvailableCommands() {
        return Stream.of(new HelloCommand(), new StartCommand(), new GetRatesCommand());
    }
}
