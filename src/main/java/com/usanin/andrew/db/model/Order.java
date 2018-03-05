package com.usanin.andrew.db.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = Order.TABLE_NAME)
public class Order {

    public static final String TABLE_NAME = "orders";
    public static final String ORD_RID = "ord_rid";
    public static final String ORD_TID = "ord_tid";
    public static final String ORD_TYPE = "ord_type";
    public static final String ORD_PRICE = "ord_price";
    public static final String ORD_AMOUNT = "ord_amount";
    public static final String ORD_TRADED_TS = "ord_traded_ts";
    public static final String ORD_CURRENCY_PAIR = "ord_currency_pair";
    private static final String ORDERS_ORD_RID_SEQ = "orders_ord_rid_seq";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ORDERS_ORD_RID_SEQ)
    @SequenceGenerator(name = ORDERS_ORD_RID_SEQ, sequenceName = ORDERS_ORD_RID_SEQ, allocationSize = 1)
    @Column(name = ORD_RID)
    public Long rid;

    /**
     * Trade ID
     */
    @Column(name = ORD_TID)
    public Long tid;

    @Enumerated(EnumType.STRING)
    @Column(name = ORD_TYPE)
    public OrderType type;

    /**
     * Buy price/Sell price
     */
    @Column(name = ORD_PRICE)
    public BigDecimal price;

    /**
     * The amount of asset bought/sold
     */
    @Column(name = ORD_AMOUNT)
    public BigDecimal amount;

    /**
     * Time of the trade
     */
    @JsonProperty("timestamp")
    @Column(name = ORD_TRADED_TS)
    public DateTime tradeTime;

    @Column(name = ORD_CURRENCY_PAIR)
    public String currencyPair;
}
