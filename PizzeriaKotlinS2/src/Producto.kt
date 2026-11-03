import java.math.BigDecimal

data class Ingrediente(val nombre: String, val cantidad: Int = 1) {
    init {
        require(nombre.isNotBlank()) { "El ingrediente necesita un nombre." }
        require(cantidad > 0) { "La cantidad de un ingrediente debe ser positiva." }
    }
}

data class Producto(
    val nombre: String,
    val precio: BigDecimal,
    val ingredientes: List<Ingrediente>
) {
    init {
        require(nombre.isNotBlank()) { "La pizza necesita un nombre." }
        require(precio > BigDecimal.ZERO) { "El precio debe ser mayor que cero." }
        require(ingredientes.isNotEmpty()) { "La pizza necesita una receta." }
    }

    fun detallarProducto(): String {
        val receta = ingredientes.joinToString { "${it.nombre} (${it.cantidad} porción/es)" }
        return "$nombre | ${formatearMonto(precio)} | Ingredientes: $receta"
    }
}
