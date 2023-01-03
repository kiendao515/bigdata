package org.davidcampos.kafka.commons;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.davidcampos.kafka.model.Job;

import java.io.IOException;

public class JobSerializer implements Serializer<Job> {

    public byte[] serialize(String s, Job o) {
        try {
            ObjectMapper om = new ObjectMapper();
            byte[] b = om.writeValueAsBytes(o);
            return b;

        } catch (IOException e) { return new byte[0]; }
    }

}
