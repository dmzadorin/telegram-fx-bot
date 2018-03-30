package com.dmzadorin.telegram.fxbot.service.commands;

import com.dmzadorin.telegram.fxbot.service.rates.FxQuoteClient;
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
public class GetRatesCommand extends BotCommand {
    private static final String LOGTAG = "GETRATESCOMMAND";

    private FxQuoteClient fxQuoteClient;

    public GetRatesCommand() {
        super("getRates", "Using this command you can get fx rates");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        if (arguments.length < 1) {
            answer.setText("Pass at least one fx rates pair");
        } else {
            StringBuilder sb = new StringBuilder("Here are your fx rates:");
            for (String arg : arguments) {
                sb.append(System.lineSeparator());
                sb.append(arg).append("=").append(fxQuoteClient.getRate(arg));
            }
            answer.setText(sb.toString());
        }
        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    public void setFxQuoteClient(FxQuoteClient fxQuoteClient) {
        this.fxQuoteClient = fxQuoteClient;
    }
}
