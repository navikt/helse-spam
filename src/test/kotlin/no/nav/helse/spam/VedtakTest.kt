package no.nav.helse.spam

import no.nav.helse.streams.defaultObjectMapper
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertTrue

class VedtakTest {
    @Test
    fun testGenererTullevedtak() {

        val aktorId = "111122223333"
        val fodselsdato = LocalDate.now().minusYears(25)
        val arbeidsgiverId = "99998888777"
        val fom = LocalDate.now().minusDays(30)
        val tom = LocalDate.now().minusDays(1);

        val vedtak = lagVedtak(
            aktorId = aktorId,
            fodselsdato = fodselsdato,
            arbeidsgiverId = arbeidsgiverId,
            fom = fom,
            tom = tom
        )

        val vedtakString = defaultObjectMapper.writeValueAsString(vedtak)
        println(vedtakString)

        assertTrue(vedtakString.contains(aktorId))
    }
}