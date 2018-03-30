package com.dmzadorin.fxquote;

import com.dmzadorin.fxquote.service.FxQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
@RestController
@RequestMapping("/fxQuotes")
public class FxQuoteController {

    @Autowired
    @Qualifier("yahooFinance")
    private FxQuoteService quoteService;

    @RequestMapping(path = "/getQuote", method = RequestMethod.GET, produces = "application/json")
    public BigDecimal getQuote(@RequestParam(name = "symbol") String symbol) {
        return quoteService.getQuote(symbol);
    }
}
