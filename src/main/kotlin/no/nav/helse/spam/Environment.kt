package no.nav.helse.spam

import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI

data class Environment (
    override val kafkaUsername: String,
    override val kafkaPassword: String,
    override val kafkaBootstrapServers: String,
    override val navTruststorePath: String?,
    override val navTruststorePassword: String?,
    override val plainTextKafka: Boolean
) : SpamKafkaConfig

@KtorExperimentalAPI
internal fun environmentFrom(config : ApplicationConfig) = Environment(
    kafkaUsername = config.property("kafka.username").getString(),
    kafkaPassword = config.property("kafka.password").getString(),
    kafkaBootstrapServers = config.property("kafka.bootstrap.servers").getString(),
    navTruststorePath = config.propertyOrNull("nav.truststore.path")?.getString(),
    navTruststorePassword = config.propertyOrNull("nav.truststore.password")?.getString(),
    plainTextKafka = config.property("kafka.plaintext").getString().toBoolean()
)

private fun getEnvVar(varName: String, defaultValue: String? = null) =
    getEnvVarOptional(varName, defaultValue) ?: throw Exception("mangler verdi for $varName")

private fun getEnvVarOptional(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue


