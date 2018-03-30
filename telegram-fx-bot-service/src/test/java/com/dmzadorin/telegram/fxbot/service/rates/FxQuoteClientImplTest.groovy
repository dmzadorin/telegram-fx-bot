package com.dmzadorin.telegram.fxbot.service.rates

import spock.lang.Specification

/**
 * Created by Dmitry Zadorin on 31.03.2018
 */
class FxQuoteClientImplTest extends Specification {
    def "GetRate"() {
        given:
        def client = new FxQuoteClientImpl('localhost', 8080)
        when:
        def rate = client.getRate('USDEUR')
        then:
        rate != null
        rate != BigDecimal.ZERO
    }
}
