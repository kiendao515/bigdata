package org.davidcampos.kafka.cli;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidcampos.kafka.commons.Commons;
import org.davidcampos.kafka.consumer.KafkaConsumerExample;
import org.davidcampos.kafka.consumer.KafkaSparkConsumerExample;
import org.davidcampos.kafka.producer.KafkaProducerExample;
import org.davidcampos.kafka.producer.Producer2;
import org.davidcampos.kafka.producer.Producer3;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(final String... args) throws IOException {
        String EXAMPLE_GOAL = System.getenv("EXAMPLE_GOAL") != null ?
                System.getenv("EXAMPLE_GOAL") : "producer";

        logger.info("Kafka Topic: {}", Commons.EXAMPLE_KAFKA_TOPIC);
        logger.info("Kafka Server: {}", Commons.EXAMPLE_KAFKA_SERVER);
        logger.info("Zookeeper Server: {}", Commons.EXAMPLE_ZOOKEEPER_SERVER);
        logger.info("GOAL: {}", EXAMPLE_GOAL);

        switch (EXAMPLE_GOAL.toLowerCase()) {
            case "producer":
                KafkaProducerExample.main();
                break;
            case "producer2":
                Producer2.main();
                break;
            case "producer3":
                Producer3.main();
            case "consumer.kafka":
                KafkaConsumerExample.main();
                break;
            case "consumer.spark":
                KafkaSparkConsumerExample.main();
                break;
            default:
                logger.error("No valid goal to run.");
                break;
        }
    }
}
