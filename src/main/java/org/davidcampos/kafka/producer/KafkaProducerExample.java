package org.davidcampos.kafka.producer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidcampos.kafka.commons.Commons;
import org.davidcampos.kafka.commons.JobSerializer;
import org.davidcampos.kafka.model.Job;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaProducerExample {
    private static final Logger logger = LogManager.getLogger(KafkaProducerExample.class);

    public static void main(final String... args) throws IOException {
        // Create topic
        createTopic();
        FileInputStream inputStream = new FileInputStream("/app/data/topcv.json");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        System.out.println(sb.toString());
        ObjectMapper mapper = new ObjectMapper();
        List<Job> jobs = mapper.readValue(sb.toString(), new TypeReference<List<Job>>(){});

        final Producer<String,Job> producer = createProducer();
        int EXAMPLE_PRODUCER_INTERVAL = System.getenv("EXAMPLE_PRODUCER_INTERVAL") != null ?
                Integer.parseInt(System.getenv("EXAMPLE_PRODUCER_INTERVAL")) : 100;

        try {
            while (true){
                for(int i=0;i< jobs.size();i++){
                    String uuid = UUID.randomUUID().toString();
                    ProducerRecord<String, Job> record = new ProducerRecord<>(Commons.EXAMPLE_KAFKA_TOPIC, uuid, jobs.get(i));
                    System.out.println(record.value());
                    //producer.send(record);
                    RecordMetadata metadata = producer.send(record).get();

                    logger.info("Sent ({}, {}) to topic {} @ {}.", uuid, jobs.get(i), Commons.EXAMPLE_KAFKA_TOPIC, metadata.timestamp());

                    Thread.sleep(EXAMPLE_PRODUCER_INTERVAL);
                }
            }
        } catch (InterruptedException e) {
            logger.error("An error occurred.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            producer.flush();
            producer.close();
        }
    }


    private static Producer<String, Job> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Commons.EXAMPLE_KAFKA_SERVER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducerExample");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JobSerializer.class.getName());
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    private static void createTopic() {
        int sessionTimeoutMs = 10 * 1000;
        int connectionTimeoutMs = 8 * 1000;

        ZkClient zkClient = new ZkClient(
                Commons.EXAMPLE_ZOOKEEPER_SERVER,
                sessionTimeoutMs,
                connectionTimeoutMs,
                ZKStringSerializer$.MODULE$);

        boolean isSecureKafkaCluster = false;
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(Commons.EXAMPLE_ZOOKEEPER_SERVER), isSecureKafkaCluster);

        int partitions = 1;
        int replication = 1;

        // Add topic configuration here
        Properties topicConfig = new Properties();
        if (!AdminUtils.topicExists(zkUtils, Commons.EXAMPLE_KAFKA_TOPIC)) {
            AdminUtils.createTopic(zkUtils, Commons.EXAMPLE_KAFKA_TOPIC, partitions, replication, topicConfig, RackAwareMode.Safe$.MODULE$);
            logger.info("Topic {} created.", Commons.EXAMPLE_KAFKA_TOPIC);
        } else {
            logger.info("Topic {} already exists.", Commons.EXAMPLE_KAFKA_TOPIC);
        }

        zkClient.close();
    }
}
