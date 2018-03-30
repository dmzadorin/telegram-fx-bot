package com.dmzadorin.fxrates.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
public class ForexProFxQuoteService implements FxQuoteService {
    private static final String REQUEST_URL = "http://www.forexpf.ru/_informer_/euusru.php";

    @Override
    public BigDecimal getQuote(String symbol) {
        return getCurrencyRate(CurrencyPair.from(symbol)).orElse(null);
    }

    private Optional<BigDecimal> getCurrencyRate(CurrencyPair pair) {
        Optional<String> rate = Optional.empty();
        try (InputStream is = new URL(REQUEST_URL).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.contains(pair.pairId)) {
                    int start = line.indexOf("\">");
                    int end = line.indexOf("</td>");
                    rate = Optional.of(line.substring(start, end));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rate.map(BigDecimal::new);
    }

    enum CurrencyPair {
        USDRUB("USDRUB", "usdrubbid"),
        EURRUB("EURRUB", "eurrubbid");

        private final String pair;
        private final String pairId;

        CurrencyPair(String pair, String pairId) {
            this.pair = pair;
            this.pairId = pairId;
        }

        public static CurrencyPair from(String pair) {
            for (CurrencyPair currencyPair : CurrencyPair.values()) {
                if (currencyPair.pair.equals(pair)) {
                    return currencyPair;
                }
            }
            throw new IllegalArgumentException("Currency pair with name " + pair + " not supported!");

        }
    }
}
