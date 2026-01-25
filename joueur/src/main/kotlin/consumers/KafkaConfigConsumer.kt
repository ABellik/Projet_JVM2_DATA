package com.projet_JVM2_DATA.consumers

import java.util.Properties
import java.time.Duration
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.kafka.common.serialization.StringDeserializer

private fun getConsumerProps(groupId: String): Properties {
    return Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)
        put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true)
        put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }
}

object KafkaConsumerManager {
    /**
     * Lance une écoute sur un topic donné.
     * @param topic Le nom du topic Kafka
     * @param groupId Le nom du groupe (ex: "joueur-group")
     * @param processFunction Une fonction qui définit quoi faire avec l'événement reçu
     */
    fun <T> listen(topic: String, groupId: String, processFunction: (String, T) -> Unit) {
        val props = getConsumerProps(groupId)
        val consumer = KafkaConsumer<String, T>(props)

        consumer.subscribe(listOf(topic))
        println("Consumer démarré sur le topic : $topic (Groupe: $groupId)")

        try {
            while (true) {
                val records = consumer.poll(Duration.ofMillis(100))

                for (record in records) {
                    try {
                        processFunction(record.key(), record.value())
                    } catch (e: Exception) {
                        println("Erreur lors du traitement d'un message : ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("Erreur fatale Consumer : ${e.message}")
        } finally {
            consumer.close()
        }
    }
}