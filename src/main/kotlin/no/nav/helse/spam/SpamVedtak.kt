package no.nav.helse.spam

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class SpamVedtak(
    val aktorId: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val arbeidsgiverId: String,
    val arbeidsgiverNavn: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val tom: LocalDate,
    val dagsats: Long,
    val soknadId: String?,

    val spamPassord: String
)