package edu.supmti.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class WordCountConsumer {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java WordCountConsumer <topic>");
            return;
        }

        String topic = args[0];

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "wordcount-group-interactive");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer"); // CLÉ ICI !
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        System.out.println("WordCount Consumer démarré – Lecture du topic : " + topic);
        System.out.println("Taper des phrases dans le producer pour voir le compteur augmenter en temps réel)");

        while (true) {
            ConsumerRecords<String, Long> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, Long> record : records) {
                System.out.printf("Mot: %-15s → count = %d%n", record.key(), record.value());
            }
        }
    }
}