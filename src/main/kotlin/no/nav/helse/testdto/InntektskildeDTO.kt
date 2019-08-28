package no.nav.helse.testdto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class InntektskildeDTO(
        val type: String,
        val sykemeldt: Boolean
)
