package no.nav.helse.spam

import io.ktor.application.*
import io.ktor.http.ContentType
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val log = LoggerFactory.getLogger("SpamServer")

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    log.info("Here we go...")

   routing {
        get("/hello") {
            call.respondText("<b>hello</b>", ContentType.Text.Html)
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
