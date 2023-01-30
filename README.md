Kafka streaming with Spark and Flink example
===
Example project on how to use [Apache Kafka](https://kafka.apache.org) and streaming consumers, namely:
- Producer sending random number words to Kafka
- Consumer using Kafka to output received messages
- Streaming Consumer using [Apache Spark](https://spark.apache.org) to count words occurrences
- Streaming Consumer using [Apache Flink](https://flink.apache.org) to count words occurrences 

Requirements
---
- Docker
- Docker Compose
- Java 8
- Maven

Build
---
1. Build Java project
    ```
    mvn clean package
    ```
1. Build Docker image 
    ```
    docker build -t bigdata-project .
    ```

Run
---
1. Start docker containers
    ```
    docker-compose up -d
    ```

Check
---
1. Check producer logs
    ```
    docker logs kafka-spark-flink-example_kafka-producer_1 -f
    ```
    
    Output should be similar to:
    ```
    [main] INFO  org.davidcampos.kafka.producer.KafkaProducerExample - Sent (74b23319-084c-4309-80a7-c0d6f107a092, eight) to topic example @ 1525127107909.
    ```
1. Check consumer with Spark logs
    ```
    docker logs kafka-spark-flink-example_kafka-consumer-spark_1 -f
    ```
    
Kafka Web UI
---
Kafka Manager Web UI available at [http://localhost:9000]().

Spark Web UI
---
Spark Web UI available at [http://localhost:4040]().

Stop
---
1. Stop docker containers
    ```
    docker-compose down
    ```
