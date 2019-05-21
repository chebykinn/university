package com.ghostflow.jackson.date;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDate;

public class LocalDateModule extends SimpleModule {

    public LocalDateModule() {
        super("LocalDateModule");
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
    }
}
