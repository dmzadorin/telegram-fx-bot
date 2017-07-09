package com.dmzadorin.telegram.bot.fxbot.common.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class GetRatesCommand extends BotCommand {
    public static final String LOGTAG = "GETRATESCOMMAND";

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
                sb.append(arg).append("=").append(getQuote(arg));
            }
            answer.setText(sb.toString());
        }
        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private String getQuote(String fxPair){
        Optional<FxQuote> quote = Optional.empty();
        try {
            String request = fxPair + "=X";
            quote = Optional.ofNullable(YahooFinance.getFx(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quote.map(FxQuote::getPrice).map(BigDecimal::toString).orElse("failed to get rate for " + fxPair);
    }
}
