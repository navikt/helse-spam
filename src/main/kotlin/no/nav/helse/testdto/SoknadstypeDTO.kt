package no.nav.helse.testdto

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SoknadstypeDTO {
    SELVSTENDIGE_OG_FRILANSERE,
    OPPHOLD_UTLAND,
    ARBEIDSTAKERE,
    @JsonEnumDefaultValue
    UKJENT
}
