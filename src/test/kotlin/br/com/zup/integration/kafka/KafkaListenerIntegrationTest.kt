package br.com.zup.integration.kafka

import br.com.zup.integration.BaseIntegration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.kafka.core.KafkaTemplate

@ExtendWith(OutputCaptureExtension::class)
class KafkaListenerIntegrationTest(
    val out: CapturedOutput
) : BaseIntegration() {

    @Autowired
    private lateinit var kafka: KafkaTemplate<String, String>

    @Test
    fun `should receive message in listen with successfully`() {
        val test = "test"
        kafka.send("topic", test)

        Thread.sleep(50000)
        assertThat(out.out).contains("receive message:$test")
        assertThat(out.out).contains("Sent message with successfully")
        assertThat(out.out).contains("Committed message with successfully")
    }
}
