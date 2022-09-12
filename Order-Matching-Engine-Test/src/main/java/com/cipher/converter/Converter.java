package com.cipher.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Converter {

    public static <S, T> T convert(S order, Class<T> tClass) {
        T t;
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            t = constructor.newInstance();
            Field[] fields = order.getClass().getDeclaredFields();
            for (Field value : fields) {
                try {
                    value.setAccessible(true);
                    Field field = tClass.getDeclaredField(value.getName());
                    field.setAccessible(true);
                    if (field.getType().equals(value.getType())) {
                        field.set(t, value.get(order));
                    }
                } catch (Exception ignored) {

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }
}