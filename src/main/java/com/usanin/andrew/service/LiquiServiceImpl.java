package com.usanin.andrew.service;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.usanin.andrew.client.LiquiHttpClient;
import com.usanin.andrew.db.OrderDao;
import com.usanin.andrew.db.model.Order;
import com.usanin.andrew.exception.ValidationException;
import com.usanin.andrew.util.ArgumentUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LiquiServiceImpl implements LiquiService {

    private static final int DEFAULT_HOURS = 24;
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 150;
    private static final int MAX_LIMIT = 2000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LiquiHttpClient liquiHttpClient;
    private final OrderDao orderDao;
    private final Provider<DateTime> nowDateTimeProvider;

    @Inject
    public LiquiServiceImpl(LiquiHttpClient liquiHttpClient, OrderDao orderDao, Provider<DateTime> nowDateTimeProvider) {
        this.liquiHttpClient = liquiHttpClient;
        this.orderDao = orderDao;
        this.nowDateTimeProvider = nowDateTimeProvider;
    }

    @Override
    public Integer loadLastTrades(String currencyPair, Optional<Integer> limit) {
        ArgumentUtils.notBlank(currencyPair, "currencyPair");
        final String lCurrencyPair = currencyPair.toLowerCase();
        final Map<String, List<Order>> lastTrades = liquiHttpClient.getLastTrades(lCurrencyPair, limit);
        final List<Order> orders = lastTrades.get(lCurrencyPair);
        logger.info("Loaded '{}' orders", orders.size());
        final List<Order> savedOrders = orders.stream()
                                          .filter(order -> !orderDao.findByTid(order.tid).isPresent())
                                          .peek(order -> order.currencyPair = lCurrencyPair)
                                          .map(orderDao::save)
                                          .collect(Collectors.toList());
        logger.info("Saved '{}' new orders", savedOrders.size());
        return savedOrders.size();
    }

    @Override
    public List<Order> findLastTrades(String currencyPair, Optional<DateTime> fromTsOpt, Optional<DateTime> toTsOpt,
                                      Optional<Integer> limitOpt, Optional<Integer> offsetOpt) {
        ArgumentUtils.notBlank(currencyPair, "currencyPair");
        final Pair<DateTime, DateTime> timePeriod = prepareTimePeriod(fromTsOpt, toTsOpt);
        final Integer limit = limitOpt.orElse(DEFAULT_LIMIT);
        ArgumentUtils.positiveArgument(limit, "limit");
        checkLimit(limit);
        final Integer offset = offsetOpt.orElse(DEFAULT_OFFSET);
        ArgumentUtils.positiveArgument(offset, "offset");
        logger.info("Find last trades for currencyPair: '{}' from: '{}' to: '{}' with limit:'{}' and offset:'{}'",
                currencyPair, timePeriod.getLeft(), timePeriod.getRight(), limit, offset);
        final List<Order> orders = orderDao.findLastTrades(currencyPair.toLowerCase(), timePeriod.getLeft(),
                timePeriod.getRight(), limit, offset);
        logger.info("Found '{}' orders", orders.size());
        return orders;
    }

    private Pair<DateTime, DateTime> prepareTimePeriod(Optional<DateTime> fromTs, Optional<DateTime> toTs) {
        final DateTime now = nowDateTimeProvider.get();
        final DateTime from = fromTs.orElse(now.minusHours(DEFAULT_HOURS));
        final DateTime to = toTs.orElse(now);

        if (!from.isBefore(to) && !from.isEqual(to)) {
            throw new ValidationException("fromTs should be before or equal to toTs");
        }

        return Pair.of(from, to);
    }

    private void checkLimit(Integer limit) {
        if (limit > MAX_LIMIT) {
            throw new ValidationException(String.format("Limit max value is %d", MAX_LIMIT));
        }
    }
}
