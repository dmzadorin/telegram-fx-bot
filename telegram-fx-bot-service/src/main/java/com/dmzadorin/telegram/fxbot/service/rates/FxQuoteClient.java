package com.dmzadorin.telegram.fxbot.service.rates;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
public interface FxQuoteClient {

    public BigDecimal getRate(String symbol);
}
