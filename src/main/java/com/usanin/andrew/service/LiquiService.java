package com.usanin.andrew.service;


import com.usanin.andrew.db.model.Order;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public interface LiquiService {

    /**
     * Loads the last trades from <a href="http://liqui.io">http://liqui.io</a>
     * and saves orders to a database if it were not saved before.
     * @param currencyPair, Mandatory, originCurrency_dstCurrency, example btc_usdt.
     * @param limit, Optional, max size of result. Max is 2000, default 150.
     * @return The count of saved {@link Order}s.
     */
    Integer loadLastTrades(String currencyPair, Optional<Integer> limit);

    /**
     * Finds the last loaded trades for the specified period of time.
     * @param currencyPair, Mandatory, originCurrency_dstCurrency, example btc_usdt.
     * @param fromTsOpt, Optional, default now()-24h, should be before or equal to {@code toTsOpt}.
     * @param toTsOpt, Optional, default now(), should be after or equal to {@code fromTsOpt}.
     * @param limitOpt, Optional, max size of result. Max is 2000, default 150.
     * @param offsetOpt, Optional, offset for start, default 0.
     * @return {@link List<Order>} in desc order by time of execution. Otherwise, zero records.
     */
    List<Order> findLastTrades(String currencyPair, Optional<DateTime> fromTsOpt, Optional<DateTime> toTsOpt,
                               Optional<Integer> limitOpt, Optional<Integer> offsetOpt);

}
