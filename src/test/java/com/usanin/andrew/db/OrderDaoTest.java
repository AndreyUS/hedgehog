package com.usanin.andrew.db;

import com.google.inject.Inject;
import com.usanin.andrew.BaseIntegrationTest;
import com.usanin.andrew.db.model.Order;
import com.usanin.andrew.db.model.OrderType;
import com.usanin.andrew.exception.NotFoundException;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertThat;


public class OrderDaoTest extends BaseIntegrationTest {

    private static final String BTC_ETH = "btc_eth";

    @Inject
    private OrderDao orderDao;

    private DateTime now = DateTime.now();

    @After
    public void tearDown() throws Exception {
        inTransaction(() -> session
                .createNativeQuery("DELETE FROM " + Order.TABLE_NAME)
                .executeUpdate());
        super.tearDown();
    }

    @Test
    public void get_notFound() throws Exception {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Order with tid: '1' is not found");
        orderDao.get(1L);
    }

    @Test
    public void save() throws Exception {
        inTransaction(() -> orderDao.save(prepareOrder(1L)));
        final Order order = orderDao.get(1L);
        assertThat(order.currencyPair, CoreMatchers.is(BTC_ETH));
    }

    @Test
    public void findLastTrades_noResult() throws Exception {
        final List<Order> orders = orderDao.findLastTrades(BTC_ETH, now, now, 100, 0);
        assertThat(orders.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void findLastTrades() throws Exception {
        inTransaction(() -> {
            orderDao.save(prepareOrder(1L, now));
            orderDao.save(prepareOrder(3L, now.plusHours(4)));
            orderDao.save(prepareOrder(2L, now.plusHours(1)));
        });
        final List<Order> result = orderDao.findLastTrades(BTC_ETH, now, now.plusHours(3), 100, 0);
        assertThat(result.size(), CoreMatchers.is(2));
        assertThat(result.stream().noneMatch(order -> order.tid == 3L), CoreMatchers.is(true));
    }

    @Test
    public void findByTid_noResult() throws Exception {
        assertThat(orderDao.findByTid(1L).isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void findByTid() throws Exception {
        inTransaction(() -> orderDao.save(prepareOrder(1L, now)));
        assertThat(orderDao.findByTid(1L).isPresent(), CoreMatchers.is(true));
    }

    private Order prepareOrder(Long tid) {
        return prepareOrder(tid, now);
    }

    private Order prepareOrder(Long tid, DateTime tradeTime) {
        final Order order = new Order();
        order.type = OrderType.ASK;
        order.amount = new BigDecimal("5");
        order.price = new BigDecimal("1.0");
        order.tid = tid;
        order.tradeTime = tradeTime;
        order.currencyPair = BTC_ETH;
        return order;
    }
}