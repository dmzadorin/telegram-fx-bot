package com.dmzadorin.telegram.bot.fxbot.common.ratesApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Dmitry Zadorin on 08.07.2017
 */
public class FxQuoteApi {
    private static final String FOREX_PF_URL = "http://www.forexpf.ru/_informer_/euusru.php";

    public String getRateForCurrency(CurrencyPair pair) {
        return getCurrencyRate(pair.getPairId());
    }

    private String getCurrencyRate(String pairId) {
        String rate = null;
        try (InputStream is = new URL(FOREX_PF_URL).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.contains(pairId)) {
                    int start = line.indexOf("\">");
                    int end = line.indexOf("</td>");
                    rate = line.substring(start, end);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rate;
    }

    public enum CurrencyPair {
        USDRUB("usdrubbid"),
        EURRUB("eurrubbid");

        private final String pairId;

        CurrencyPair(String pairId) {
            this.pairId = pairId;
        }

        public String getPairId() {
            return pairId;
        }
    }
}
