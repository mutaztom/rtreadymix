package com.rationalteam.rtreadymix;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.jsonb.JsonbConfigCustomizer;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

@Singleton
public class JsonCustomizer implements JsonbConfigCustomizer {

    public void customize(JsonbConfig config) {
        config.setProperty("PropertyAccessor.FIELD", JsonAutoDetect.Visibility.ANY);
        config.setProperty("SerializationFeature.FAIL_ON_EMPTY_BEANS",false);
        config.withSerializers(new CustomerSerializer());
    }

}

