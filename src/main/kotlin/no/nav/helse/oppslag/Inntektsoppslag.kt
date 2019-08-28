package no.nav.helse.oppslag

import arrow.core.Try
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class InntektsoppslagResultat(val inntekter : List<Inntekt>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Inntektsarbeidsgiver(val identifikator: String, val type: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Inntekt(val virksomhet: Inntektsarbeidsgiver, val utbetalingsperiode: YearMonth, val beløp: BigDecimal, val type: String, val ytelse: Boolean, val kode: String?)
