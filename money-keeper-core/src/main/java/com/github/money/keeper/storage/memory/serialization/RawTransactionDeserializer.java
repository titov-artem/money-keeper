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
        Integer accountId = null;
        LocalDate date = null;
        SalePoint salePoint = null;
        BigDecimal amount = null;
        String fileHash = null;
        String uploadId = null;

        JsonToken token;
        while ((token = jsonParser.nextToken()) != null) {
            if (token == JsonToken.END_OBJECT) {
                break;
            }
            switch (token) {
                case VALUE_NUMBER_INT:
                    if ("id".equals(jsonParser.getCurrentName())) {
                        id = jsonParser.getValueAsLong();
                    } else if ("accountId".equals(jsonParser.getCurrentName())) {
                        accountId = jsonParser.getValueAsInt();
                    }
                    break;
                case VALUE_STRING:
                    if ("date".equals(jsonParser.getCurrentName())) {
                        date = jsonParser.readValueAs(LocalDate.class);
                    } else if ("fileHash".equals(jsonParser.getCurrentName())) {
                        fileHash = jsonParser.getValueAsString();
                    } else if ("uploadId".equals(jsonParser.getCurrentName())) {
                        uploadId = jsonParser.getValueAsString();
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
        Objects.requireNonNull(accountId, "AccountId attribute missed in json for RawTransaction");
        Objects.requireNonNull(date, "Date attribute missed in json for RawTransaction");
        Objects.requireNonNull(salePoint, "SalePoint attribute missed in json for RawTransaction");
        Objects.requireNonNull(amount, "Amount attribute missed in json for RawTransaction");
        Objects.requireNonNull(fileHash, "FileHash attribute missed in json for RawTransaction");
        Objects.requireNonNull(uploadId, "UploadId attribute missed in json for RawTransaction");
        return new RawTransaction(id, accountId, date, salePoint, amount, fileHash, uploadId);
    }
}
