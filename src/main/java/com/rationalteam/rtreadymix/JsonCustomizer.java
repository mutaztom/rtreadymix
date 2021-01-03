package com.rationalteam.rtreadymix;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.jsonb.JsonbConfigCustomizer;
import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

@Singleton
public class JsonCustomizer implements JsonbConfigCustomizer {

    public void customize(JsonbConfig config) {
        config.withSerializers(new CustomerSerializer());
    }
}

