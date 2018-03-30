package com.dmzadorin.telegram.fxbot.service.rates;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 31.03.2018
 */
public class FxQuoteClientImpl implements FxQuoteClient {
    private static final String PATH = "fxQuotes/getQuote";
    private static final String REQUEST_PARAM = "symbol={symbol}";
    private static final String SCHEME = "http";
    private final RestTemplate restTemplate;
    private final String url;

    public FxQuoteClientImpl(String host, int port) {
        this.restTemplate = new RestTemplate();
        this.url = UriComponentsBuilder.newInstance()
                .scheme(SCHEME)
                .host(host)
                .port(port)
                .path(PATH)
                .queryParam(REQUEST_PARAM)
                .build()
                .toUriString();
    }

    @Override
    public BigDecimal getRate(String symbol) {
        return restTemplate.getForObject(url, BigDecimal.class, symbol);
    }
}
