package com.alex.tur.data.net;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class EnumRetrofitConverterFactory extends Converter.Factory {

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter<?, String> converter = null;
        if (type instanceof Class && ((Class<?>)type).isEnum()) {
            converter = new Converter<Object, String>() {
                @Override
                public String convert(Object value) throws IOException {
                    return getSerializedNameValue((Enum) value);
                }
            };
        }
        return converter;
    }

    @Nullable
    private static <E extends Enum<E>> String getSerializedNameValue(E e) {
        String value = null;
        try {
            value = e.getClass().getField(e.name()).getAnnotation(SerializedName.class).value();
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
        }
        return value;
    }
}