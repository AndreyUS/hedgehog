package com.usanin.andrew.client;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usanin.andrew.db.model.Order;
import com.usanin.andrew.exception.BusinessException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class LiquiHttpClientImpl implements LiquiHttpClient {

    public static final String CLIENT_NAME = "Liqui-Http-Client";
    private static final String METHOD_TRADE = "trades";
    private static final String QUERY_PARAM_LIMIT = "limit";

    private final String baseUrl;
    private final Client httpClient;
    private final ObjectMapper objectMapper;

    private LiquiHttpClientImpl(String baseUrl, Client httpClient, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, List<Order>> getLastTrades(String currencyPair, Optional<Integer> limit) {
        final WebTarget target = httpClient.target(baseUrl)
                                           .path(METHOD_TRADE)
                                           .path(currencyPair);
        final WebTarget finalTarget = limit.map(l -> target.queryParam(QUERY_PARAM_LIMIT, l))
                                           .orElse(target);
        final Invocation invocation = finalTarget
                .request(APPLICATION_JSON_TYPE)
                .buildGet();
        return invoke(invocation,
                rawResponse -> {
                    try {
                        return objectMapper.readValue(rawResponse, new TypeReference<Map<String, List<Order>>>() {
                        });
                    } catch (IOException e) {
                        throw new BusinessException(String.format("Unable read json. Reason: %s", e.getMessage()), e);
                    }
                });
    }

    private <T> T invoke(Invocation invocation, Function<String, T> readValueFunc) {
        try {
            final Response response = invocation.invoke();
            final String rawResponse = response.readEntity(String.class);
            return readValueFunc.apply(rawResponse);
        } catch (Throwable e) {
            throw new BusinessException(String.format("Liqui request failed: %s", e.getMessage()), e);
        }
    }

    public static class Builder {
        private String baseUrl;
        private Client httpClient;
        private ObjectMapper objectMapper;

        public Builder(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Builder setHttpClient(Client httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public LiquiHttpClientImpl build() {
            return new LiquiHttpClientImpl(baseUrl, httpClient, objectMapper);
        }
    }
}
