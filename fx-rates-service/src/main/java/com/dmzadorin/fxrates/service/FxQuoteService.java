package com.dmzadorin.fxrates.service;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
public interface FxQuoteService {
    public BigDecimal getQuote(String symbol);
}
