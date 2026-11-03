import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

// Precios sin impuesto y tasa configurable del 19%.
val TASA_IMPUESTO = BigDecimal("0.19")
private val TASA_DESCUENTO = BigDecimal("0.10")

data class Totales(
    val subtotal: BigDecimal,
    val descuento: BigDecimal,
    val impuestos: BigDecimal,
    val total: BigDecimal
)

fun calcularTotal(productos: List<Producto>, tasaImpuesto: BigDecimal = TASA_IMPUESTO): Totales {
    require(productos.isNotEmpty()) { "Agrega al menos una pizza al pedido." }
    require(tasaImpuesto >= BigDecimal.ZERO && tasaImpuesto <= BigDecimal.ONE) {
        "La tasa de impuestos debe estar entre 0 y 1."
    }
    val subtotal = productos.fold(BigDecimal.ZERO) { suma, producto -> suma + producto.precio }
        .setScale(2, RoundingMode.HALF_UP)
    val descuento = (if (productos.size > 3) subtotal * TASA_DESCUENTO else BigDecimal.ZERO)
        .setScale(2, RoundingMode.HALF_UP)
    val baseImponible = subtotal - descuento
    val impuestos = (baseImponible * tasaImpuesto).setScale(2, RoundingMode.HALF_UP)
    return Totales(subtotal, descuento, impuestos, baseImponible + impuestos)
}

fun formatearMonto(monto: BigDecimal): String {
    val formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL"))
    formato.minimumFractionDigits = 2
    formato.maximumFractionDigits = 2
    return "${formato.format(monto)} pesos"
}
