package com.github.money.keeper.view.providers;

import org.springframework.format.datetime.standard.TemporalAccessorParser;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class JSR330ParamConverterProvider implements ParamConverterProvider {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss.SSS]");

    @Override
    public <T> ParamConverter<T> getConverter(
            Class<T> rawType, Type genericType, Annotation[] antns) {

        if (TemporalAccessor.class.isAssignableFrom(rawType)) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(String string) {
                    try {
                        TemporalAccessorParser parser = new TemporalAccessorParser((Class<TemporalAccessor>) rawType, formatter);
                        return (T) parser.parse(string, Locale.US);
                    } catch (ParseException | IllegalStateException e) {
                        throw new BadRequestException(e);
                    }
                }

                @Override
                public String toString(T t) {
                    return formatter.format((TemporalAccessor) t);
                }
            };
        }

        return null;
    }
}
