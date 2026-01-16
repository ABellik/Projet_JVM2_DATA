package com.projet_JVM2_DATA.producers

import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import org.apache.kafka.common.serialization.StringSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializer

private fun getProducerProps(): Properties {
    return Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
        put(ProducerConfig.ACKS_CONFIG, "all")
    }
}

object KafkaProducerManager {
    private val producer: KafkaProducer<String, Any> by lazy {
        KafkaProducer<String, Any>(getProducerProps())
    }

    fun send(topic: String, key: String, event: Any) {
        val record = ProducerRecord(topic, key, event)
        producer.send(record) { metadata, exception ->
            if (exception != null) {
                println("❌ Erreur : ${exception.message}")
            } else {
                println("✅ Envoyé dans ${metadata.topic()} à l'offset ${metadata.offset()}")
            }
        }
    }

    fun close() {
        producer.close()
    }
}