package br.com.zup.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaListener(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @KafkaListener(topics = ["\${spring.kafka.topic}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun receiveMessage(@Payload message: String, ack: Acknowledgment) {
        runCatching {
            println("receive message:$message")
            kafkaTemplate.send("topic-producer", message)
            println("Sent message with successfully")
            ack.acknowledge()
            println("Committed message with successfully")
        }.onFailure {
            println("Error to received message")
            ack.acknowledge()
        }
    }
}
