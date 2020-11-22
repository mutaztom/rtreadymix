package com.rationalteam.rtreadymix;
import io.quarkus.jsonb.JsonbConfigCustomizer;
import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

@Singleton
public class JsonCustomizer implements JsonbConfigCustomizer {

    public void customize(JsonbConfig config) {
        config.withSerializers(new CustomerSerializer());
    }
}

