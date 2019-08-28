package no.nav.helse.oppslag

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class AnvistPeriodeDTO(
        val fom: LocalDate,
        val tom: LocalDate
)
