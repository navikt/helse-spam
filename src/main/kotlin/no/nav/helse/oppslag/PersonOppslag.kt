package no.nav.helse.oppslag

import arrow.core.Try
import no.nav.helse.behandling.søknad.Sykepengesøknad
import no.nav.helse.behandling.Tpsfakta
import java.time.LocalDate
import java.util.*

enum class Kjønn {
    MANN, KVINNE, UKJENN
}

data class PersonDTO(
        val aktørId: String,
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
        val fdato: LocalDate,
        val kjønn: Kjønn,
        val statsborgerskap: String,
        val status: String,
        val bostedsland: String?,
        val diskresjonskode: String?
)

data class Person(
        val id: AktørId,
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
        val fdato: LocalDate,
        val kjønn: Kjønn,
        val statsborgerskap: String,
        val status: String,
        val bostedsland: String? = null,
        val diskresjonskode: String? = null
)

data class AktørId(val aktor: String) {
    init {
        if (aktor.isEmpty()) {
            throw IllegalArgumentException("$aktor cannot be empty")
        }
    }
}

