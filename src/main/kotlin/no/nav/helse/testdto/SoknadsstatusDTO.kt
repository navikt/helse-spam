package no.nav.helse.testdto

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SoknadsstatusDTO {
    NY,
    SENDT,
    FREMTIDIG,
    KORRIGERT,
    AVBRUTT,
    SLETTET,
    @JsonEnumDefaultValue
    UKJENT
}
