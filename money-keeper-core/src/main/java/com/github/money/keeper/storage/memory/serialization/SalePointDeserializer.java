package com.github.money.keeper.storage.memory.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.money.keeper.model.SalePoint;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Artem Titov
 */
public class SalePointDeserializer extends JsonDeserializer<SalePoint> {

    @Override
    public SalePoint deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String name = null;
        String categoryDescription = null;
        JsonToken token;
        while ((token = jsonParser.nextToken()) != null) {
            if (token == JsonToken.END_OBJECT) {
                break;
            }
            switch (token) {
                case VALUE_STRING:
                    if ("name".equals(jsonParser.getCurrentName())) {
                        name = jsonParser.getText();
                    } else if ("categoryDescription".equals(jsonParser.getCurrentName())) {
                        categoryDescription = jsonParser.getText();
                    }
                    break;
                default:
                    break;
            }
        }
        Objects.requireNonNull(name, "Name attribute missed in json for SalePoint");
        return new SalePoint(name, categoryDescription);
    }
}
