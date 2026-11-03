import java.math.BigDecimal
import kotlin.math.abs

class Pizzeria(
    val menu: List<Producto>,
    val inventario: Inventario,
    val repartidores: List<Repartidor>
) {
    private val clientes = mutableListOf<Cliente>()
    private var pedidosDelDia: Array<Pedido> = arrayOf()

    // Set evita repetir ingredientes compartidos por varias recetas.
    val ingredientesDelMenu: Set<String> = menu.flatMap { producto ->
        producto.ingredientes.map { it.nombre }
    }.toSet()

    fun registrarCliente(cliente: Cliente) {
        clientes.add(cliente)
    }

    fun consultarClientes(): List<Cliente> = clientes.toList()

    fun consultarPedidos(): Array<Pedido> = pedidosDelDia.copyOf()

    fun confirmarPedido(cliente: Cliente, productos: List<Producto>, notas: String?): Pedido {
        require(cliente in clientes) { "Primero debes registrar al cliente." }
        require(productos.all { it in menu }) { "El pedido contiene una pizza que no está en el menú." }
        val pedido = Pedido(pedidosDelDia.size + 1, cliente, productos, notas)
        pedido.calcularTotal()
        // La lista combina las recetas de todas las pizzas del pedido en un único lote.
        inventario.prepararLote(productos.flatMap { it.ingredientes }, 1)
        // Los arreglos tienen tamaño fijo: + crea uno nuevo y conserva los pedidos anteriores.
        pedidosDelDia += pedido
        return pedido
    }

    fun buscarPedido(id: Int): Pedido? = pedidosDelDia.find { it.id == id }

    fun asignarRepartidor(pedido: Pedido): Repartidor? {
        require(pedidosDelDia.any { it === pedido }) { "El pedido no está registrado." }
        require(pedido.estado == EstadoPedido.LISTO) { "El pedido debe estar listo para asignar repartidor." }
        // Misma zona primero, luego la vecina y finalmente la más alejada.
        // Norte y Sur tienen Centro entre ambas. Un empate conserva el orden de la lista.
        val cercano = repartidores
            .sortedBy { abs(it.zona.ordinal - pedido.cliente.zona.ordinal) }
            .find { it.disponible }
        cercano?.asignarPedido(pedido)
        return cercano
    }
}

fun crearPizzeria(): Pizzeria {
    val base = listOf(Ingrediente("masa"), Ingrediente("salsa"), Ingrediente("queso"))
    val menu = listOf(
        Producto("Margarita", BigDecimal("8500.00"), base),
        Producto("Pepperoni", BigDecimal("9500.00"), base + Ingrediente("pepperoni")),
        Producto("Vegetariana", BigDecimal("9000.00"), base + Ingrediente("champiñones"))
    )
    val inventario = Inventario(mapOf(
        "masa" to 20, "salsa" to 20, "queso" to 20,
        "pepperoni" to 10, "champiñones" to 10
    ))
    val repartidores = listOf(
        Repartidor("Camila", "+56911111111", Zona.NORTE),
        Repartidor("Diego", "+56922222222", Zona.CENTRO),
        Repartidor("Sofía", "+56933333333", Zona.SUR)
    )
    return Pizzeria(menu, inventario, repartidores)
}
