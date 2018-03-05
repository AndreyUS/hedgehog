package com.usanin.andrew.service;

import com.usanin.andrew.client.LiquiHttpClient;
import com.usanin.andrew.db.OrderDao;
import com.usanin.andrew.db.model.Order;
import com.usanin.andrew.exception.ValidationException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LiquiServiceImplMockTest {

    private static final String CURRENCY_PAIR = "eth_btc";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private LiquiServiceImpl liquiService;

    @Mock
    private LiquiHttpClient liquiHttpClient;

    @Mock
    private OrderDao orderDao;

    private DateTime now;

    private List<Order> orders;
    private Order order;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        now = DateTime.now();
        liquiService = new LiquiServiceImpl(liquiHttpClient, orderDao, () -> now);
        order = new Order();
        order.tid = 1L;
        orders = Collections.singletonList(order);
    }

    @Test
    public void loadLastTrades() throws Exception {
        when(liquiHttpClient.getLastTrades(CURRENCY_PAIR, Optional.of(10))).thenReturn(Collections.singletonMap(CURRENCY_PAIR, orders));
        when(orderDao.findByTid(1L)).thenReturn(Optional.empty());
        liquiService.loadLastTrades(CURRENCY_PAIR, Optional.of(10));
        verify(liquiHttpClient, times(1)).getLastTrades(CURRENCY_PAIR, Optional.of(10));
        verify(orderDao, times(1)).save(any(Order.class));
        verify(orderDao, times(1)).findByTid(anyLong());
        verifyNoMoreInteractions(liquiHttpClient, orderDao);
    }

    @Test
    public void loadLastTrades_orderExist() throws Exception {
        when(liquiHttpClient.getLastTrades(CURRENCY_PAIR, Optional.of(10))).thenReturn(Collections.singletonMap(CURRENCY_PAIR, orders));
        when(orderDao.findByTid(1L)).thenReturn(Optional.of(order));
        liquiService.loadLastTrades(CURRENCY_PAIR, Optional.of(10));
        verify(liquiHttpClient, times(1)).getLastTrades(CURRENCY_PAIR, Optional.of(10));
        verify(orderDao, times(1)).findByTid(anyLong());
        verifyNoMoreInteractions(liquiHttpClient, orderDao);
    }

    @Test
    public void findLastTrades_withoutOptParams() throws Exception {
        liquiService.findLastTrades(CURRENCY_PAIR, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        verify(orderDao, times(1)).findLastTrades(CURRENCY_PAIR, now.minusHours(24), now, 150, 0);
        verifyNoMoreInteractions(liquiHttpClient, orderDao);
    }

    @Test
    public void findLastTrades() throws Exception {
        final DateTime from = now.minusHours(1);
        liquiService.findLastTrades(CURRENCY_PAIR, Optional.of(from), Optional.of(now), Optional.of(20), Optional.of(5));
        verify(orderDao, times(1)).findLastTrades(CURRENCY_PAIR, from, now, 20, 5);
        verifyNoMoreInteractions(liquiHttpClient, orderDao);
    }

    @Test
    public void findLastTrades_invalidTimePeriod() throws Exception {
        exceptionRule.expect(ValidationException.class);
        exceptionRule.expectMessage("fromTs should be before or equal to toTs");
        liquiService.findLastTrades(CURRENCY_PAIR, Optional.of(now), Optional.of(now.minusHours(1)), Optional.empty(), Optional.empty());
    }

    @Test
    public void findLastTrades_invalidLimit() throws Exception {
        exceptionRule.expect(ValidationException.class);
        exceptionRule.expectMessage("Limit max value is 2000");
        liquiService.findLastTrades(CURRENCY_PAIR, Optional.of(now), Optional.of(now), Optional.of(20001), Optional.empty());
    }
}