package no.nav.helse.fastsetting

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = Vurdering.Uavklart::class, name = "Uavklart"),
        JsonSubTypes.Type(value = Vurdering.Avklart::class, name = "Avklart"))
sealed class Vurdering<out V, out G>(val begrunnelse: String, val grunnlag: G, val vurderingstidspunkt: LocalDateTime = LocalDateTime.now()) {
    class Avklart<V, G>(val fastsattVerdi: V,
                        begrunnelse: String,
                        grunnlag: G,
                        val fastsattAv: String): Vurdering<V, G>(begrunnelse, grunnlag) {

        override fun toString(): String {
            return "Avklart(fastsattVerdi=$fastsattVerdi, begrunnelse=$begrunnelse)"
        }

    }
    class Uavklart<V,G>(val årsak: Årsak,
                        val underårsak: String = "UKJENT",
                        begrunnelse: String,
                        grunnlag: G): Vurdering<V, G>(begrunnelse, grunnlag) {

        enum class Årsak {
            KREVER_SKJØNNSMESSIG_VURDERING,
            FORSTÅR_IKKE_DATA,
            HAR_IKKE_DATA,
            FALLER_UTENFOR_MVP
        }

        override fun toString(): String {
            return "Uavklart(årsak=$årsak, begrunnelse=$begrunnelse)"
        }


    }
}
