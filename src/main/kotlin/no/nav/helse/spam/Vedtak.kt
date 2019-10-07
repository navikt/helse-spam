package no.nav.helse.spam

import no.nav.helse.Grunnlagsdata
import no.nav.helse.Yrkesstatus
import no.nav.helse.behandling.*
import no.nav.helse.behandling.søknad.Sykepengesøknad
import no.nav.helse.fastsetting.Aldersgrunnlag
import no.nav.helse.fastsetting.Opptjeningsgrunnlag
import no.nav.helse.fastsetting.Sykepengegrunnlag
import no.nav.helse.fastsetting.Vurdering
import no.nav.helse.oppslag.Inntekt
import no.nav.helse.oppslag.Inntektsarbeidsgiver
import no.nav.helse.oppslag.arbeidinntektytelse.dto.ArbeidInntektYtelseDTO
import no.nav.helse.oppslag.arbeidinntektytelse.dto.ArbeidsforholdDTO
import no.nav.helse.oppslag.arbeidinntektytelse.dto.ArbeidsgiverDTO
import no.nav.helse.oppslag.arbeidinntektytelse.dto.YtelserDTO
import no.nav.helse.sykepenger.beregning.Beregningsresultat
import no.nav.helse.testdto.*
import no.nav.nare.core.evaluations.Evaluering
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

fun lagVedtak(aktorId: String,
              arbeidsgiverId:String,
              fom:LocalDate,
              tom:LocalDate,
              dagsats:Long,
              beregningsperiode : List<Inntekt> = emptyList(),
              sammenligningsperiode : List<Inntekt> = emptyList(),
              utbetalTilArbeidsgiver:Boolean = true,
              soknadId:UUID = UUID.randomUUID()) : SykepengeVedtak {


    val mottager = if (utbetalTilArbeidsgiver) arbeidsgiverId else aktorId

    val perioder = listOf(
        Vedtaksperiode(
            fom = fom,
            tom = tom,
            grad = 100,
            dagsats = BigDecimal.valueOf(dagsats),
            fordeling = listOf(
                Fordeling(
                    mottager = mottager,
                    andel = 100
                )
            )
        )
    )

    val soknad = SykepengesøknadV2DTO(
        id = soknadId.toString(),
        type = SoknadstypeDTO.ARBEIDSTAKERE,
        aktorId = aktorId,
        status = SoknadsstatusDTO.NY,
        arbeidsgiver = ArbeidsgiverDTO(
            navn = "TheWorkpplace",
            orgnummer = arbeidsgiverId
        ),
        soktUtenlandsopphold = false,
        fom = fom,
        tom = tom,
        startSyketilfelle = fom,
        sendtNav = LocalDateTime.now(),
        soknadsperioder = listOf(
            SoknadsperiodeDTO(
                fom = fom,
                tom = tom,
                sykmeldingsgrad = 100
            )
        ),
        fravar = emptyList(),
        andreInntektskilder = emptyList()
    )

    val fodselsdato = LocalDate.now().minusYears(25)
    val tpsfakta = Tpsfakta(
        fodselsdato = fodselsdato,
        status = "status",
        bostedland = "Norge",
        diskresjonskode = null,
        statsborgerskap = "Norsk"
    )

    val arbeidsforhold = ArbeidsforholdDTO(
        type = "jobb",
        arbeidsgiver = ArbeidsgiverDTO(
            identifikator = arbeidsgiverId,
            type = "bedrift"
        ),
        startdato = fom.minusYears(5),
        sluttdato = null
    )

    val sykepengegrunnlag = Sykepengegrunnlag(
        sykepengegrunnlagNårTrygdenYter = Vurdering.Avklart(500000, "derfor", emptyList(), "spa"),
        sykepengegrunnlagIArbeidsgiverperioden = Vurdering.Avklart(500000, "derfor", emptyList(), "spa")
    )

    val arbeidsforholdVurdering = Vurdering.Avklart(arbeidsforhold, "derfor", listOf(arbeidsforhold), "spam")

    val vedtak = SykepengeVedtak(
        originalSøknad = Sykepengesøknad(soknad.asJsonNode()),
        faktagrunnlag = Faktagrunnlag(
            tps = tpsfakta,
            beregningsperiode = beregningsperiode,
            sammenligningsperiode = sammenligningsperiode,
            sykepengehistorikk = emptyList(),
            arbeidInntektYtelse = ArbeidInntektYtelseDTO(
                arbeidsforhold = emptyList(),
                inntekter = emptyList(),
                ytelser = emptyList()
            ),
            ytelser = YtelserDTO(
                infotrygd = emptyList(),
                arena = emptyList()
            )
        ),
        avklarteVerdier = AvklarteVerdier(
            medlemsskap = Vurdering.Avklart(true, "derfor", tpsfakta, "spam"),
            alder = Vurdering.Avklart(25, "derfor", Aldersgrunnlag(fodselsdato), "spam"),
            maksdato = Vurdering.Avklart(fom.plusDays(240), "derfor",
                Grunnlagsdata(fom, fom, 25, Yrkesstatus.ARBEIDSTAKER, tidligerePerioder = emptyList()), "spam"),
            sykepengehistorikk = emptyList(),
            arbeidsforhold = arbeidsforholdVurdering,
            opptjeningstid = Vurdering.Avklart(5 * 365L, "derfor", Opptjeningsgrunnlag(
                førsteSykdomsdag = fom,
                arbeidsforhold = arbeidsforholdVurdering
            ), "spam"),
            sykepengegrunnlag = Vurdering.Avklart(sykepengegrunnlag, "derfor", listOf(
                Inntekt(
                    virksomhet = Inntektsarbeidsgiver(
                        identifikator = arbeidsgiverId,
                        type = "bedrift"),
                    utbetalingsperiode = YearMonth.now().minusMonths(1),
                    beløp = BigDecimal.valueOf(41666L),
                    type = "jobb",
                    ytelse = false,
                    kode = null)
            ), "spam")
        ),
        vilkårsprøving = Evaluering.ja("todo bien"),
        beregning = Beregningsresultat(
            dagsatser = emptyList(),
            delresultater = emptyList()
        ),
        behandlingsId = "spam" + soknadId,
        vedtak = Vedtak(
            perioder = perioder
        )
    )

    return vedtak
}