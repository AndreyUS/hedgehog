package com.usanin.andrew.client;

import com.google.inject.Inject;
import com.usanin.andrew.BaseIntegrationTest;
import com.usanin.andrew.db.model.Order;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertThat;


public class LiquiHttpClientImplTest extends BaseIntegrationTest {

    @Inject
    private LiquiHttpClient liquiHttpClient;

    @Test
    public void getLastTrades() throws Exception {
        final Map<String, List<Order>> trades = liquiHttpClient.getLastTrades("btc_usdt", Optional.empty());
        assertThat(trades.size(), CoreMatchers.is(1));
    }

}