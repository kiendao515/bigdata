package org.davidcampos.kafka.consumer;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidcampos.kafka.commons.Commons;
import org.davidcampos.kafka.commons.JobDeserializer;
import org.davidcampos.kafka.model.Job;
import org.davidcampos.kafka.model.JobModel;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerExample {
    private static final Logger logger = LogManager.getLogger(KafkaConsumerExample.class);

    public static void main(final String... args) throws IOException{
        RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
              new HttpHost("localhost", 9200, "http")));
        System.out.println(client);
      
          // Create a JSON document to store in Elasticsearch
        Map<String, JobModel> jsonMap = new HashMap<>();
        final Consumer<String, Job> consumer = createConsumer();
        IndexRequest indexRequest = new IndexRequest("sampleIndex");
        indexRequest.id("001");
        indexRequest.source("SampleKey","SampleValue");
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("response id: "+indexResponse.getId());
        System.out.println("response name: "+indexResponse.getResult().name());
        // while (true) {
        //     final ConsumerRecords<String, Job> consumerRecords = consumer.poll(1000);
        //     consumerRecords.forEach(record -> {
        //         JobModel job =new JobModel(
        //             record.value().company_name,
        //             record.value().post_link,
        //             record.value().job,
        //             record.value().salary,
        //             record.value().location,
        //             record.value().deadline
        //         );
        //         jsonMap.put(job.job, job);

        //         // Use the Index API to add the document to Elasticsearch
        //         logger.info("({}, {})", job.company_name, job.job);
        //     });
        //     IndexRequest request = new IndexRequest("jobs").id("1").source(jsonMap); 
        //     //     try {
        //     //         IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        //     //         String id = response.getId();
        //     //         System.out.println("Successfully added document with id: " + id);
        //     //     } catch (ElasticsearchException e) {
        //     //         System.out.println(e.getMessage());
        //     //         e.printStackTrace();
        //     // }
        //     consumer.commitAsync();
        // }
    }

    private static Consumer<String, Job> createConsumer() {
        // Create properties
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Commons.EXAMPLE_KAFKA_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumerGroup");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JobDeserializer.class.getName());

        // Create the consumer using properties
        final Consumer<String, Job> consumer = new KafkaConsumer(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(Commons.EXAMPLE_KAFKA_TOPIC));
        return consumer;
    }
}
