package edu.supmti.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import java.util.Arrays;
import java.util.Properties;

public class WordCountApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: WordCountApp <input-topic> <output-topic>");
            System.exit(1);
        }

        String inputTopic = args[0];
        String outputTopic = args[1];

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app-v1");  // Important : unique !
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> lines = builder.stream(inputTopic);

        lines
            .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))  // split sur espaces, ponctuation...
            .filter((key, value) -> value.length() > 0)                                // élimine mots vides
            .map((key, value) -> new KeyValue<>(value, "1"))                          // clé = mot, valeur = "1"
            .groupByKey()
            .count(Materialized.as("counts-store"))                                    // magasin interne
            .toStream()
            .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));           // CLÉ = mot, VALEUR = Long

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        // Nettoyage propre à la fermeture
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Arrêt de l'application Kafka Streams...");
            streams.close();
        }));

        System.out.println("WordCount Kafka Streams démarré !");
        System.out.println("Input topic  : " + inputTopic);
        System.out.println("Output topic : " + outputTopic);
        streams.start();
    }
}