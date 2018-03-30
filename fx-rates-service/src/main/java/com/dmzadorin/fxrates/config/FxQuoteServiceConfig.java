package com.dmzadorin.fxrates.config;

import com.dmzadorin.fxrates.service.FxQuoteService;
import com.dmzadorin.fxrates.service.YahooFinanceQuoteService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Created by Dmitry Zadorin on 30.03.2018
 */
@SpringBootConfiguration
public class FxQuoteServiceConfig {

    @Bean
    public FxQuoteService yahooFinance(){
        return new YahooFinanceQuoteService();
    }

}
