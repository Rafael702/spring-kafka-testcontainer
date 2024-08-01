package br.com.zup

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableKafka
@EnableAsync
class Application

fun main() {
    runApplication<Application>()
}
