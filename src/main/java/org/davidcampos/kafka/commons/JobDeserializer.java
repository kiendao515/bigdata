package org.davidcampos.kafka.commons;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.davidcampos.kafka.model.Job;

public class JobDeserializer implements Deserializer<Job> {


    @Override
    public Job deserialize(String topic, byte[] data) {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        ObjectMapper mapper = new ObjectMapper(factory);
        Job fooObj = null;
        try {
            fooObj = mapper.reader().forType(Job.class).readValue(data); // BREAKS HERE!!!


        }
        catch (Exception e) { e.printStackTrace(); }

        return fooObj;
    }
}
