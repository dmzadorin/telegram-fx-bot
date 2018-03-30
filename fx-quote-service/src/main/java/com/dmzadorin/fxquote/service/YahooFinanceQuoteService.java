package com.dmzadorin.fxquote.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
public class YahooFinanceQuoteService implements FxQuoteService {
    private static final Logger logger = LoggerFactory.getLogger(YahooFinanceQuoteService.class);

    @Override
    public BigDecimal getQuote(String symbol) {
        Optional<FxQuote> quote = getYahooFinanceQuote(symbol);
        return quote.map(FxQuote::getPrice).orElseThrow(RuntimeException::new);
    }

    private Optional<FxQuote> getYahooFinanceQuote(String fxPair) {
        Optional<FxQuote> quote = Optional.empty();
        try {
            String request = fxPair + "=X";
            quote = Optional.ofNullable(YahooFinance.getFx(request));
        } catch (IOException e) {
            logger.error("Failed to read fx rates for pair" + fxPair, e);
        }
        return quote;
    }
}
