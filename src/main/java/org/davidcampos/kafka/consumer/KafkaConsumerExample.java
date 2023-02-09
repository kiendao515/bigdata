package org.davidcampos.kafka.consumer;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.davidcampos.kafka.commons.Commons;
import org.davidcampos.kafka.commons.JobDeserializer;
import org.davidcampos.kafka.model.Job;
import org.davidcampos.kafka.model.JobModel;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerExample {
    private static final Logger logger = LogManager.getLogger(KafkaConsumerExample.class);

    public static void main(final String... args) throws IOException {
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
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        stream.map(record -> record.value())
                .foreachRDD(rdd -> {
                    rdd.foreachPartition(value -> {
                        while (value.hasNext()) {
                            Job job = value.next();
                            Map<String, Object> jsonMap = new HashMap<>();
                            jsonMap.put("company_name", job.company_name);
                            jsonMap.put("post_link", job.post_link);
                            jsonMap.put("job", job.job);
                            jsonMap.put("salary", job.salary);
                            jsonMap.put("location", job.location);
                            jsonMap.put("deadline", job.deadline);
                            IndexRequest indexRequest = new IndexRequest("jobs")
                                    .id("1").source(jsonMap);
                                    try {
                                        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                                    } catch(ElasticsearchException e) {
                                        System.out.println(e.getMessage());
                                    }
                        }
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
