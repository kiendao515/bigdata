package org.davidcampos.kafka.consumer;

import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.bson.Document;
import org.davidcampos.kafka.commons.Commons;
import org.davidcampos.kafka.commons.JobDeserializer;
import org.davidcampos.kafka.model.Job;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.Durations;

public class KafkaSparkConsumerExample {
    private static final Logger logger = LogManager.getLogger(KafkaSparkConsumerExample.class);

    public static void main(final String... args) {
        // Configure Spark to connect to Kafka running on local machine
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Commons.EXAMPLE_KAFKA_SERVER);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JobDeserializer.class.getName());
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "SparkConsumerGroup");

        // Configure Spark to listen messages in topic test
        Collection<String> topics = Arrays.asList(Commons.EXAMPLE_KAFKA_TOPIC);

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("SparkConsumerApplication");

        // Read messages in batch of 30 seconds
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // Start reading messages from Kafka and get DStream
        final JavaInputDStream<ConsumerRecord<String, Job>> stream = KafkaUtils.createDirectStream(jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams));

        stream.map(record -> record.value())
                .foreachRDD(rdd -> {
                    rdd.foreachPartition(value -> {
                        System.out.println(value.next());
                        ConnectionString connectionString = new ConnectionString(
                                "mongodb+srv://kiendao:kiendao2001@cluster0.bnqgz.mongodb.net/?retryWrites=true&w=majority");
                        MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(connectionString)
                                .build();
                        MongoClient mongoClient = MongoClients.create(settings);
                        MongoDatabase database = mongoClient.getDatabase("bigdata");
                        MongoCollection<Document> collection = database.getCollection("jobs");
                        while (value.hasNext()) {
                            Job job = value.next();
                            Document doc = new Document();
                            doc.append("company_name", job.getCompany_name());
                            doc.append("job_title", job.getJob());
                            doc.append("salary", job.getSalary());
                            doc.append("deadline", job.getDeadline());
                            doc.append("post_link", job.getPost_link());
                            doc.append("location", job.getLocation());
                            collection.insertOne(doc);
                        }
                        mongoClient.close();

                    });
                });
        JavaDStream<String> lines = stream
                .map((Function<ConsumerRecord<String, Job>, String>) kafkaRecord -> kafkaRecord.value().job);
        lines.print();
        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            logger.error("An error occurred.", e);
        }

    }

}
