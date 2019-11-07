package no.nav.helse.spam

import no.nav.helse.Utbetalingsbehov
import no.nav.helse.Utbetalingslinje
import no.nav.helse.streams.defaultObjectMapper
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue

class VedtakTest {
    @Test
    fun testGenererTullevedtak() {

        val aktorId = "111122223333"
        val arbeidsgiverId = "99998888777"
        val fom = LocalDate.now().minusDays(30)
        val tom = LocalDate.now().minusDays(1);

        val vedtak = Utbetalingsbehov(
            behov = "Utbetaling",
            sakskompleksId = UUID.randomUUID(),
            aktørId = aktorId,
            organisasjonsnummer = arbeidsgiverId,
            utbetalingslinjer = listOf(
                Utbetalingslinje(
                    grad = 100,
                    dagsats = "1320".toBigDecimal(),
                    fom = fom,
                    tom = tom
                )
            ),
            maksdato = tom.plusYears(1),
            saksbehandler = "Z999999"
        )

        val vedtakString = defaultObjectMapper.writeValueAsString(vedtak)
        println(vedtakString)

        assertTrue(vedtakString.contains(aktorId))
        assertTrue(vedtakString.contains("1320"))
    }
}