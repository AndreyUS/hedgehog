package com.usanin.andrew.util;


import com.usanin.andrew.db.model.OrderType;
import com.usanin.andrew.exception.ValidationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;

public class HedgehogUtils {

    public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Parses {@link OrderType} from string.
     * @param orderType, case insensitive
     * @return {@link OrderType}
     * @throws ValidationException if string doesn't match with any of {@link OrderType}
     */
    public static OrderType parseOrderType(String orderType) {
        ArgumentUtils.notBlank(orderType, "orderType");
        try {
            return OrderType.valueOf(orderType.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException(String.format("Order type '%s' is not valid, expected: '%s'", orderType, Arrays.toString(OrderType.values())));
        }
    }

    /**
     * Parses {@link DateTime} from string.
     * @param dateTime, string with date and time in format 'dd.MM.yyyy HH:mm:ss'
     * @return {@link DateTime}
     */
    public static DateTime parseDateTime(String dateTime) {
        ArgumentUtils.notBlank(dateTime, "dateTime");
        try {
            return FORMATTER.parseDateTime(dateTime);
        } catch (Exception e) {
            throw new ValidationException("Unable parse date. Expected format: dd.MM.yyyy HH:mm:ss");
        }
    }
}
