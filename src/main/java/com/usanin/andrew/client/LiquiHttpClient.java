package com.usanin.andrew.client;


import com.usanin.andrew.db.model.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LiquiHttpClient {

    /**
     * This method provides the information about the last trades.
     * @param currencyPair - Mandatory, originCurrency_dstCurrency, example btc_usdt.
     * @param limit - Optional, indicates how many orders should be loaded (150 by default). The maximum allowable value is 2000.
     * @return last trades.
     * @throws  com.usanin.andrew.exception.BusinessException if can't read data from service.
     */
    Map<String, List<Order>> getLastTrades(String currencyPair, Optional<Integer> limit);

}

