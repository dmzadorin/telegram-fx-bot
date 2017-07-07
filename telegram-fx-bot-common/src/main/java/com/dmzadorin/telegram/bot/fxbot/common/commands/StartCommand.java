package com.dmzadorin.telegram.bot.fxbot.common.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class StartCommand extends BotCommand {
    private static final String LOGTAG = "STARTCOMMAND";

    public StartCommand() {
        super("start", "Using this command you can start the bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = user.getFirstName() + " " + user.getLastName();
        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        answer.setText("Welcome " + userName + "! This bot will help you to monitor fx currency rates on MOEX market");

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
