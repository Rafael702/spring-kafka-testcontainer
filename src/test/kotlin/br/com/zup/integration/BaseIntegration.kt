package br.com.zup.integration

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@SpringBootTest
class BaseIntegration {
    @Container
    final val kafkaImage =
        KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:5.4.3")
                .asCompatibleSubstituteFor("apache/kafka")
        )

    @Bean
    fun kafkaListenContainsFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun overridePropertiesInternal(registry: DynamicPropertyRegistry) {
            BaseIntegration().kafkaImage.start()
            registry.add("spring.kafka.bootstrap-servers", BaseIntegration().kafkaImage::getBootstrapServers)
        }
    }

    @Bean
    fun consumerFactory() = DefaultKafkaConsumerFactory<String, String>(
        mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaImage.bootstrapServers,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.GROUP_ID_CONFIG to "test",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer",
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer"
        )
    )

    @Bean
    fun producerFactory() = DefaultKafkaProducerFactory<String, String>(
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaImage.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer",
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer"
        )
    )

    @Bean
    fun kafkaTemplate() = KafkaTemplate(producerFactory())

    init {
        if (!kafkaImage.isRunning) {
            kafkaImage.start()
        }
    }
}
