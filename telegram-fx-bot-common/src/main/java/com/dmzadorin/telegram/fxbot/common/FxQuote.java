package com.dmzadorin.telegram.fxbot.common;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Dmitry Zadorin on 31.03.2018
 */
public class FxQuote {
    private final String symbol;
    private final BigDecimal price;

    public FxQuote(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FxQuote fxQuote = (FxQuote) o;
        return Objects.equals(symbol, fxQuote.symbol) &&
                Objects.equals(price, fxQuote.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, price);
    }
}
