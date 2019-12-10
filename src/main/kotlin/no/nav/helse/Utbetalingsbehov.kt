package no.nav.helse

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.codec.binary.Base32
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.time.LocalDate
import java.util.UUID

data class Utbetalingsbehov(
    @JsonProperty("@behov")
    val behov: List<String>,
    val sakskompleksId: UUID,
    val utbetalingsreferanse: String = sakskompleksId.base32Encode(),
    val aktørId: String,
    val organisasjonsnummer: String,
    val maksdato: LocalDate,
    val saksbehandler: String,
    val utbetalingslinjer: List<Utbetalingslinje>
)

private fun UUID.base32Encode(): String {
    val pad = '='
    return Base32(pad.toByte())
        .encodeAsString(this.byteArray())
        .replace(pad.toString(), "")
}

private fun UUID.byteArray() = ByteBuffer.allocate(Long.SIZE_BYTES * 2).apply {
    putLong(this@byteArray.mostSignificantBits)
    putLong(this@byteArray.leastSignificantBits)
}.array()

data class Utbetalingslinje(
    val grad: Int,
    val dagsats: BigDecimal,
    val fom: LocalDate,
    val tom: LocalDate
)