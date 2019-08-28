package no.nav.helse.testdto

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class FravarstypeDTO {
    FERIE,
    PERMISJON,
    UTLANDSOPPHOLD,
    UTDANNING_FULLTID,
    UTDANNING_DELTID,
    @JsonEnumDefaultValue
    UKJENT
}
