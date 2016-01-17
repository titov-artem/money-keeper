package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.storage.memory.serialization.LocalDateDeserializer;
import com.github.money.keeper.storage.memory.serialization.LocalDateSerializer;
import com.github.money.keeper.storage.memory.serialization.RawTransactionDeserializer;
import com.github.money.keeper.storage.memory.serialization.SalePointDeserializer;

import java.time.LocalDate;

/**
 * @author Artem Titov
 */
class AbstractFileBackedRepo {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected void setUpObjectMapper() {
        SimpleModule modelModule = new SimpleModule();

        modelModule.addSerializer(LocalDate.class, new LocalDateSerializer());

        modelModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        modelModule.addDeserializer(SalePoint.class, new SalePointDeserializer());
        modelModule.addDeserializer(RawTransaction.class, new RawTransactionDeserializer());

        objectMapper.registerModule(modelModule);
    }
}
