package com.usanin.andrew.json;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.usanin.andrew.db.model.OrderType;
import com.usanin.andrew.util.HedgehogUtils;

import java.io.IOException;

public class OrderTypeDeserialized extends JsonDeserializer<OrderType> {

    @Override
    public OrderType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final String orderType = jsonParser.getText();
        return HedgehogUtils.parseOrderType(orderType);
    }
}
