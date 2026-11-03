enum class EstadoPedido(val descripcion: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado")
}

class Pedido(
    val id: Int,
    val cliente: Cliente,
    productos: List<Producto>,
    notas: String? = null
) {
    // Cada elemento representa una pizza; se agrupan para mostrar las cantidades.
    val productos: List<Producto> = productos.toList()
    val notas: String = notas?.trim()?.takeIf { it.isNotEmpty() } ?: "Sin notas"
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set
    var repartidor: Repartidor? = null
        private set

    init {
        require(id > 0) { "El identificador del pedido debe ser positivo." }
        require(productos.isNotEmpty()) { "El pedido necesita al menos una pizza." }
    }

    fun calcularTotal(): Totales = calcularTotal(productos)

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        require(nuevoEstado != EstadoPedido.ENTREGADO) {
            "Confirma la entrega mediante el repartidor para liberar su disponibilidad."
        }
        val permitido = when (estado) {
            EstadoPedido.EN_PREPARACION -> nuevoEstado == EstadoPedido.LISTO
            EstadoPedido.LISTO -> nuevoEstado == EstadoPedido.EN_CAMINO && repartidor != null
            EstadoPedido.EN_CAMINO -> false
            EstadoPedido.ENTREGADO -> false
        }
        require(permitido) { "No se puede pasar de ${estado.descripcion} a ${nuevoEstado.descripcion}." }
        estado = nuevoEstado
    }

    internal fun finalizarEntrega(repartidor: Repartidor) {
        require(estado == EstadoPedido.EN_CAMINO && this.repartidor === repartidor) {
            "Solo el repartidor asignado puede finalizar un pedido en camino."
        }
        estado = EstadoPedido.ENTREGADO
    }

    internal fun iniciarReparto(repartidor: Repartidor) {
        require(estado == EstadoPedido.LISTO) { "El pedido debe estar listo antes de salir a reparto." }
        require(this.repartidor == null) { "El pedido ya tiene repartidor." }
        this.repartidor = repartidor
        cambiarEstado(EstadoPedido.EN_CAMINO)
    }
}
