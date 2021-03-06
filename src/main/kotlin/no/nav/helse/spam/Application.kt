package no.nav.helse.spam

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import no.nav.helse.Utbetalingsbehov
import no.nav.helse.Utbetalingslinje
import no.nav.helse.streams.defaultObjectMapper
import org.slf4j.LoggerFactory
import java.util.UUID

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val log = LoggerFactory.getLogger("SpamServer")

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    log.info("Here we do go...")

    val env = environmentFrom(this.environment.config)
    val producer = if (env.disableKafka) null else SpamKafkaProducer(env)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    routing {
        post("/vedtak") {
            var request:SpamVedtak
            try {
                request = call.receive<SpamVedtak>()
            } catch (jsonError: JsonProcessingException) {
                log.info("JsonProcessingException: ", jsonError)
                call.respond(HttpStatusCode.BadRequest,
                    "Feil her: ${jsonError.message}")
                return@post
            }
            if (env.spamPassord != null && (env.spamPassord != request.spamPassord)) {
                call.respond(HttpStatusCode.Unauthorized, "Husk å legge inn SPAM-Passord")
                return@post
            }

            val sakskompleksId = request.soknadId?.trim().let {
                if (it.isNullOrEmpty()) {
                    UUID.randomUUID()
                } else {
                    UUID.fromString(it)
                }
            }

            val utbetalingsbehov = Utbetalingsbehov(
                behov = listOf("Utbetaling"),
                sakskompleksId = sakskompleksId,
                aktørId = request.aktorId,
                organisasjonsnummer = request.arbeidsgiverId,
                maksdato = request.tom.plusMonths(1),
                saksbehandler = "Z999999",
                utbetalingslinjer = listOf(
                    Utbetalingslinje(
                        fom = request.fom,
                        tom = request.tom,
                        grad = 100,
                        dagsats = request.dagsats.toBigDecimal()
                    )
                )
            )
            log.info("Sender: " + defaultObjectMapper.writeValueAsString(utbetalingsbehov))
            producer?.sendBehov(utbetalingsbehov)
            call.respond(utbetalingsbehov)
        }

        get("/isalive") {
            call.respondText("ALIVE", ContentType.Text.Plain)
        }

        get("/isready") {
            call.respondText("READY", ContentType.Text.Plain)
        }

        static {
            defaultResource("index.html", "static")
            resources("static")
        }
    }
}

