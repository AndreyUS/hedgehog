package com.usanin.andrew.db;


import com.google.inject.Inject;
import com.usanin.andrew.db.model.Order;
import com.usanin.andrew.exception.NotFoundException;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class OrderDao extends AbstractDAO<Order> {

    @Inject
    public OrderDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Order get(Long tid) {
        return Optional.ofNullable(super.get(tid)).orElseThrow(() -> new NotFoundException(String.format("Order with tid: '%s' is not found", tid)));
    }

    public Optional<Order> findByTid(Long tid) {
        final String sql = "SELECT * FROM " + Order.TABLE_NAME + " WHERE " + Order.ORD_TID + " = ?1";
        final NativeQuery<Order> query = currentSession().createNativeQuery(sql, Order.class);
        query.setParameter(1, tid);
        return Optional.ofNullable(query.uniqueResult());
    }

    public Order save(Order order) {
        return persist(order);
    }

    public List<Order> findLastTrades(String currencyPair, DateTime from, DateTime to, Integer limit, Integer offset) {
        final String sql = "SELECT * FROM " +
                Order.TABLE_NAME +
                " WHERE " +
                Order.ORD_CURRENCY_PAIR + " = ?1" +
                " AND " +
                Order.ORD_TRADED_TS + " BETWEEN ?2 AND ?3 " +
                "ORDER BY " + Order.ORD_TRADED_TS + " DESC ";
        final NativeQuery<Order> query = currentSession().createNativeQuery(sql, Order.class);
        query.setParameter(1, currencyPair);
        query.setParameter(2, new Timestamp(from.getMillis()));
        query.setParameter(3, new Timestamp(to.getMillis()));
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.getResultList();
    }
}
