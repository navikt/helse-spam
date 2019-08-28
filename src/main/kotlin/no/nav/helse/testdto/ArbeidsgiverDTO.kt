package no.nav.helse.testdto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ArbeidsgiverDTO(val navn: String, val orgnummer: String)
