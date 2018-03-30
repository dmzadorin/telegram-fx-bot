package com.dmzadorin.telegram.fxbot.service.updatehandlers;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramWebhookBot;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class FxWebHookBot extends TelegramWebhookBot {

    private final String botToken;
    private final String botUserName;
    private final String botPath;

    public FxWebHookBot(String botToken, String botUserName, String botPath) {
        this.botToken = botToken;
        this.botUserName = botUserName;
        this.botPath = botPath;
        init();
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    private void init() {

    }
}
