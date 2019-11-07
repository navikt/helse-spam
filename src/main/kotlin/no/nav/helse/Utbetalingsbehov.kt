package no.nav.helse

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Utbetalingsbehov(
    @JsonProperty("@behov")
    val behov: String,
    val sakskompleksId: UUID,
    val aktørId: String,
    val organisasjonsnummer: String,
    val maksdato: LocalDate,
    val saksbehandler: String,
    val utbetalingslinjer: List<Utbetalingslinje>
)

data class Utbetalingslinje(
    val grad: Int,
    val dagsats: BigDecimal,
    val fom: LocalDate,
    val tom: LocalDate
)