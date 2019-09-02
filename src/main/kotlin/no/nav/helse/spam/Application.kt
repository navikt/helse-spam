package no.nav.helse.spam

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
import no.nav.helse.streams.defaultObjectMapper
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val log = LoggerFactory.getLogger("SpamServer")

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    log.info("Here we do go...")

    val env = environmentFrom(this.environment.config)
    val producer = SpamKafkaProducer(env)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

   routing {
       post("/vedtak") {
           val request = call.receive<SpamVedtak>()
           if (env.spamPassord != null && (env.spamPassord != request.spamPassord)) {
               call.respond(HttpStatusCode.Unauthorized)
               return@post
           }
           val vedtak = lagVedtak(
               aktorId = request.aktorId,
               arbeidsgiverId = request.arbeidsgiverId,
               fom = request.fom,
               tom = request.tom,
               dagsats = request.dagsats
           )
           log.info("Sender: " + defaultObjectMapper.writeValueAsString(vedtak))
           producer.sendVedtak(vedtak)
           call.respond(vedtak)
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

