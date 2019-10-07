package no.nav.helse.spam

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
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
import no.nav.helse.oppslag.Inntekt
import no.nav.helse.oppslag.Inntektsarbeidsgiver
import no.nav.helse.streams.defaultObjectMapper
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.YearMonth
import java.util.*

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

           var soknadId = UUID.randomUUID()
           if (!request.soknadId.isNullOrBlank()) {
               soknadId = UUID.fromString(request.soknadId!!.trim())
           }

           val vedtak = lagVedtak(
               aktorId = request.aktorId,
               arbeidsgiverId = request.arbeidsgiverId,
               arbeidsgiverNavn = request.arbeidsgiverNavn ?: "thaWorkPlace",
               fom = request.fom,
               tom = request.tom,
               dagsats = request.dagsats,
               beregningsperiode = request.beregningsperiode ?: emptyList(),
               sammenligningsperiode = request.beregningsperiode ?: emptyList(),
               soknadId = soknadId
           )
           log.info("Sender: " + defaultObjectMapper.writeValueAsString(vedtak))
           producer?.sendVedtak(vedtak)
           call.respond(vedtak)
       }

       get("/laginntekt") {
           //call.request.queryParameters[]
           call.respond(Inntekt(
               virksomhet = Inntektsarbeidsgiver(
                   identifikator = "998877665",
                   type = "bedrift"
               ),
               utbetalingsperiode = YearMonth.of(2019, 7),
               beløp =  BigDecimal.valueOf(1000),
               type = "PÆNG",
               ytelse = false,
               kode = null)

           )
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

