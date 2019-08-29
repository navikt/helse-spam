package no.nav.helse.spam

import no.nav.helse.behandling.SykepengeVedtak
import no.nav.helse.streams.defaultObjectMapper
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.io.File

interface SpamKafkaConfig {
    val kafkaUsername: String
    val kafkaPassword: String
    val kafkaBootstrapServers: String
    val navTruststorePath: String?
    val navTruststorePassword: String?
    val plainTextKafka: Boolean
}

class SpamKafkaProducer(val config : SpamKafkaConfig) {

    val vedtakTopic = "aapen-helse-sykepenger-vedtak"
    private val log = LoggerFactory.getLogger("SpamKafkaProducer")

    val producer : KafkaProducer<String, String>

    init {
        producer = KafkaProducer<String, String>(producerProperties(), StringSerializer(), StringSerializer())
    }

    fun sendVedtak(sykepengeVedtak: SykepengeVedtak) {
        producer.send(ProducerRecord(vedtakTopic, sykepengeVedtak.originalSøknad.id, defaultObjectMapper.writeValueAsString(sykepengeVedtak)))
        producer.flush()
    }

    private fun producerProperties(): MutableMap<String, Any> {
        return HashMap<String, Any>().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.kafkaBootstrapServers)
            if (config.plainTextKafka) {
                put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT")
            } else {
                put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
                log.info("navTruststorePath: ${config.navTruststorePath}")
                put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, File(config.navTruststorePath!!).absolutePath)
                put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.navTruststorePassword!!)
            }
            put(SaslConfigs.SASL_MECHANISM, "PLAIN")
            put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"${config.kafkaUsername}\" password=\"${config.kafkaPassword}\";")
        }
    }

}