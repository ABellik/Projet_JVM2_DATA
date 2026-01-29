package com.projet_JVM2_DATA.editeur;

import com.example.events.CauseFermetureSession;
import com.example.events.Session;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class CrashFilterStream {

    private static final long THRESHOLD = 10L;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "crash-filter-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final String schemaRegistryUrl = "http://localhost:8081";
        final Map<String, String> serdeConfig =
                Collections.singletonMap("schema.registry.url", schemaRegistryUrl);

        final Serde<Session> sessionSerde = new SpecificAvroSerde<>();
        sessionSerde.configure(serdeConfig, false);

        StreamsBuilder builder = new StreamsBuilder();

        //Ecoute du flux de session
        KStream<String, Session> sessionStream = builder.stream(
                "game-sessions",
                Consumed.with(Serdes.String(), sessionSerde)
        );

        sessionStream
                .filter((key, session) -> session.getCauseFermeture() == CauseFermetureSession.CRASH)
                .to("detected-crashs", Produced.with(Serdes.String(), sessionSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        streams.start();
        System.out.println("Stream de filtrage démarré : game-sessions -> detected-crashs");
    }
}
