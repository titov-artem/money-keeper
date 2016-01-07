package com.github.money.keeper.storage.memory.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Artem Titov
 */
public class RawTransactionDeserializer extends JsonDeserializer<RawTransaction> {

    @Override
    public RawTransaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Long id = null;
        LocalDate date = null;
        SalePoint salePoint = null;
        BigDecimal amount = null;

        JsonToken token;
        while ((token = jsonParser.nextToken()) != null) {
            if (token == JsonToken.END_OBJECT) {
                break;
            }
            switch (token) {
                case VALUE_NUMBER_INT:
                    if ("id".equals(jsonParser.getCurrentName())) {
                        id = jsonParser.getValueAsLong();
                    }
                    break;
                case VALUE_STRING:
                    if ("date".equals(jsonParser.getCurrentName())) {
                        date = jsonParser.readValueAs(LocalDate.class);
                    }
                    break;
                case VALUE_NUMBER_FLOAT:
                    if ("amount".equals(jsonParser.getCurrentName())) {
                        amount = jsonParser.readValueAs(BigDecimal.class);
                    }
                    break;
                case START_OBJECT:
                    if ("salePoint".equals(jsonParser.getCurrentName())) {
                        salePoint = jsonParser.readValueAs(SalePoint.class);
                    }
                    break;
                default:
                    break;
            }
        }
        Objects.requireNonNull(id, "Id attribute missed in json for RawTransaction");
        Objects.requireNonNull(date, "Date attribute missed in json for RawTransaction");
        Objects.requireNonNull(salePoint, "SalePoint attribute missed in json for RawTransaction");
        Objects.requireNonNull(amount, "Amount attribute missed in json for RawTransaction");
        return new RawTransaction(id, date, salePoint, amount);
    }
}
