package com.usanin.andrew.util;


import com.usanin.andrew.exception.ValidationException;

public class ArgumentUtils {

    /**
     * Checks that object is not null
     */
    public static <T> void notNull(T t, String name) {
        if (t == null) {
            throw new ValidationException(String.format("Argument: '%s' can not be null", name));
        }
    }

    /**
     * Checks that string is not null and not blank
     */
    public static void notBlank(String str, String name) {
        notNull(str, name);
        if (str.length() == 0) {
            throw new ValidationException(String.format("Argument: '%s' can not be blank", name));
        }
    }

    /**
     * Checks that integer is positive
     */
    public static void positiveArgument(Integer arg, String name) {
        if (arg < 0) {
            throw new ValidationException(String.format("Argument: '%s' can not be negative", name));
        }
    }
}
