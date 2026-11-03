import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal

object Pruebas {
    private var realizadas = 0

    @JvmStatic
    fun main(args: Array<String>) {
        probar("Tres pizzas no reciben descuento") {
            val totales = calcularTotal(List(3) { crearPizzeria().menu.first() })
            dinero("25500.00", totales.subtotal)
            dinero("0.00", totales.descuento)
            dinero("4845.00", totales.impuestos)
            dinero("30345.00", totales.total)
        }
        probar("Cuatro pizzas reciben descuento antes del impuesto") {
            val pizza = crearPizzeria().menu.first()
            val totales = calcularTotal(List(4) { pizza })
            dinero("34000.00", totales.subtotal)
            dinero("3400.00", totales.descuento)
            dinero("5814.00", totales.impuestos)
            dinero("36414.00", totales.total)
        }
        probar("Pedido mixto y tasa configurable") {
            val totales = calcularTotal(crearPizzeria().menu, BigDecimal.ZERO)
            dinero("27000.00", totales.total)
            dinero("0.00", totales.impuestos)
            rechaza<IllegalArgumentException> { calcularTotal(emptyList()) }
            rechaza<IllegalArgumentException> { calcularTotal(crearPizzeria().menu, BigDecimal("-0.1")) }
        }
        probar("Cálculo decimal exacto y redondeo") {
            val pizza = Producto("Prueba", BigDecimal("0.10"), listOf(Ingrediente("masa")))
            val totales = calcularTotal(List(4) { pizza })
            dinero("0.40", totales.subtotal)
            dinero("0.04", totales.descuento)
            dinero("0.07", totales.impuestos)
            dinero("0.43", totales.total)
            check(formatearMonto(BigDecimal("8500.5")) == "8.500,50 pesos")
        }
        probar("Contactos válidos y valores predeterminados") {
            check(validarTelefono(" +56912345678 ") == "+56912345678")
            check(validarTelefono("abc") == TELEFONO_PREDETERMINADO)
            check(validarTelefono("") == TELEFONO_PREDETERMINADO)
            check(validarCorreo(" ana.perez+curso@duoc.cl ") == "ana.perez+curso@duoc.cl")
            for (correo in listOf("", "ana@", "ana..perez@duoc.cl", "ana@-duoc.cl", "ana duoc.cl")) {
                check(validarCorreo(correo) == CORREO_PREDETERMINADO)
            }
        }
        probar("Producto valida el precio y Usuario aplica polimorfismo") {
            rechaza<IllegalArgumentException> {
                Producto("Prueba", BigDecimal("-1"), listOf(Ingrediente("masa")))
            }
            val usuarios: List<Usuario> = listOf(cliente(), Repartidor("Camila", "+56911111111", Zona.NORTE))
            check(usuarios[0].describir().startsWith("Cliente:"))
            check(usuarios[1].describir().startsWith("Repartidor:"))
        }
        probar("Notas nulas o vacías usan el valor predeterminado") {
            val pizza = crearPizzeria().menu.first()
            check(Pedido(1, cliente(), listOf(pizza)).notas == "Sin notas")
            check(Pedido(2, cliente(), listOf(pizza), "  ").notas == "Sin notas")
            check(Pedido(3, cliente(), listOf(pizza), " Sin aceitunas ").notas == "Sin aceitunas")
            check(capturar { notificarCliente(null, "Prueba") }.isEmpty())
            check(capturar { notificarCliente(cliente(), "Prueba") }.contains("Aviso para Ana"))
        }
        probar("Lote suma ingredientes repetidos y multiplica cantidades") {
            val inventario = Inventario(mapOf("queso" to 10))
            val receta = listOf(Ingrediente("queso", 1), Ingrediente("queso", 2))
            check(inventario.prepararLote(receta, 3) == mapOf("queso" to 9))
            check(inventario.consultar()["queso"] == 1)
        }
        probar("Stock insuficiente no descuenta parcialmente") {
            val inventario = Inventario(mapOf("masa" to 4, "queso" to 0))
            val antes = inventario.consultar()
            val error = rechaza<StockInsuficienteException> {
                inventario.prepararLote(listOf(Ingrediente("masa"), Ingrediente("queso")), 2)
            }
            check(inventario.consultar() == antes)
            check(error.faltantes.single().cantidadAReponer == 2)
            check(capturar { mostrarFaltantes(error.faltantes) }.contains("Reabastecer ingrediente"))
        }
        probar("Ingrediente inexistente se informa como faltante") {
            val inventario = Inventario(mapOf("masa" to 1))
            val faltantes = inventario.verificarDisponibilidad(mapOf("tomate" to 2))
            check(faltantes.single() == Faltante("tomate", 0, 2))
        }
        probar("Inventario rechaza negativos y desbordamientos sin modificarse") {
            rechaza<IllegalArgumentException> { Inventario(mapOf("masa" to -1)) }
            val inventario = Inventario(mapOf("masa" to Int.MAX_VALUE))
            rechaza<IllegalArgumentException> { inventario.reabastecer("masa", -1) }
            rechaza<ArithmeticException> { inventario.reabastecer("masa", 1) }
            rechaza<ArithmeticException> { inventario.prepararLote(listOf(Ingrediente("masa", 2)), Int.MAX_VALUE) }
            check(inventario.consultar()["masa"] == Int.MAX_VALUE)
        }
        probar("Reabastecer permite aceptar un pedido antes rechazado") {
            val inventario = Inventario(mapOf("masa" to 0))
            rechaza<StockInsuficienteException> { inventario.prepararLote(listOf(Ingrediente("masa")), 1) }
            inventario.reabastecer("masa", 2)
            inventario.prepararLote(listOf(Ingrediente("masa")), 1)
            check(inventario.consultar()["masa"] == 1)
        }
        probar("Pedidos confirmados conservan identificadores y stock") {
            val pizzeria = crearPizzeria()
            val cliente = cliente()
            pizzeria.registrarCliente(cliente)
            val primero = pizzeria.confirmarPedido(cliente, List(4) { pizzeria.menu.first() }, null)
            val segundo = pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu[1]), "Tocar timbre")
            check(primero.id == 1 && segundo.id == 2)
            check(pizzeria.consultarPedidos().size == 2)
            check(pizzeria.inventario.consultar()["masa"] == 15)
            check(pizzeria.inventario.consultar()["pepperoni"] == 9)
            check(pizzeria.ingredientesDelMenu.size == 5)
        }
        probar("Pedido rechazado no se registra ni consume identificador") {
            val pizzeria = crearPizzeria()
            val cliente = cliente()
            pizzeria.registrarCliente(cliente)
            val antes = pizzeria.inventario.consultar()
            rechaza<StockInsuficienteException> {
                pizzeria.confirmarPedido(cliente, List(21) { pizzeria.menu.first() }, null)
            }
            check(pizzeria.consultarPedidos().isEmpty())
            check(pizzeria.inventario.consultar() == antes)
            check(pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu.first()), null).id == 1)
        }
        probar("Cliente no registrado y pedido vacío se rechazan") {
            val pizzeria = crearPizzeria()
            val cliente = cliente()
            rechaza<IllegalArgumentException> { pizzeria.confirmarPedido(cliente, pizzeria.menu, null) }
            pizzeria.registrarCliente(cliente)
            rechaza<IllegalArgumentException> { pizzeria.confirmarPedido(cliente, emptyList(), null) }
            check(pizzeria.inventario.consultar()["masa"] == 20)
        }
        probar("Repartidor de la misma zona, entrega y recuperación de disponibilidad") {
            val pizzeria = crearPizzeria()
            val cliente = cliente(Zona.CENTRO)
            pizzeria.registrarCliente(cliente)
            val pedido = pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu.first()), null)
            rechaza<IllegalArgumentException> { pizzeria.asignarRepartidor(pedido) }
            pedido.cambiarEstado(EstadoPedido.LISTO)
            val repartidor = pizzeria.asignarRepartidor(pedido) ?: error("Falta repartidor")
            check(repartidor.nombre == "Diego")
            check(!repartidor.disponible && pedido.estado == EstadoPedido.EN_CAMINO)
            rechaza<IllegalArgumentException> { pedido.cambiarEstado(EstadoPedido.ENTREGADO) }
            repartidor.entregarPedido(pedido)
            check(pedido.estado == EstadoPedido.ENTREGADO && repartidor.disponible)
            check(repartidor.zona == cliente.zona)
            rechaza<IllegalArgumentException> { pedido.cambiarEstado(EstadoPedido.LISTO) }
            rechaza<IllegalArgumentException> { repartidor.entregarPedido(pedido) }
        }
        probar("Asignación prioriza la misma zona, la vecina y desempata por orden de registro") {
            val casos = mapOf(
                Zona.NORTE to listOf("Camila", "Diego", "Sofía"),
                Zona.CENTRO to listOf("Diego", "Camila", "Sofía"),
                Zona.SUR to listOf("Sofía", "Diego", "Camila")
            )
            for ((zona, esperados) in casos) {
                val pizzeria = crearPizzeria()
                val cliente = cliente(zona)
                pizzeria.registrarCliente(cliente)
                val nombres = List(3) {
                    val pedido = pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu.first()), null)
                    pedido.cambiarEstado(EstadoPedido.LISTO)
                    pizzeria.asignarRepartidor(pedido)?.nombre
                }
                check(nombres == esperados) { "Orden incorrecto para la zona $zona: $nombres" }
            }
        }
        probar("Una entrega actualiza la zona usada en la siguiente asignación") {
            val base = crearPizzeria()
            val camila = Repartidor("Camila", "+56911111111", Zona.NORTE)
            val diego = Repartidor("Diego", "+56922222222", Zona.CENTRO)
            val pizzeria = Pizzeria(base.menu, base.inventario, listOf(camila, diego))
            val clienteSur = cliente(Zona.SUR)
            val clienteCentro = cliente(Zona.CENTRO)
            pizzeria.registrarCliente(clienteSur)
            pizzeria.registrarCliente(clienteCentro)
            val primero = pizzeria.confirmarPedido(clienteSur, listOf(pizzeria.menu.first()), null)
            primero.cambiarEstado(EstadoPedido.LISTO)
            check(pizzeria.asignarRepartidor(primero) === diego)
            diego.entregarPedido(primero)
            check(diego.zona == Zona.SUR && diego.disponible)
            val segundo = pizzeria.confirmarPedido(clienteCentro, listOf(pizzeria.menu.first()), null)
            segundo.cambiarEstado(EstadoPedido.LISTO)
            check(pizzeria.asignarRepartidor(segundo) === camila)
        }
        probar("Repartidores ocupados no se asignan a dos pedidos") {
            val pizzeria = crearPizzeria()
            val cliente = cliente()
            pizzeria.registrarCliente(cliente)
            val pedidos = List(4) {
                pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu.first()), null)
                    .also { it.cambiarEstado(EstadoPedido.LISTO) }
            }
            val asignados = pedidos.take(3).map { pizzeria.asignarRepartidor(it) }
            check(asignados.toSet().size == 3)
            check(pizzeria.asignarRepartidor(pedidos[3]) == null)
            check(pedidos[3].estado == EstadoPedido.LISTO)
            rechaza<IllegalArgumentException> { asignados.first()?.entregarPedido(pedidos[1]) }
            asignados.first()?.entregarPedido(pedidos[0])
            check(pizzeria.asignarRepartidor(pedidos[3]) === asignados.first())
        }
        probar("Reporte separa completados y pendientes e incluye resumen") {
            val pizzeria = crearPizzeria()
            val cliente = cliente()
            pizzeria.registrarCliente(cliente)
            val primero = pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu.first()), null)
            pizzeria.confirmarPedido(cliente, listOf(pizzeria.menu[1]), "Tocar timbre")
            primero.cambiarEstado(EstadoPedido.LISTO)
            pizzeria.asignarRepartidor(primero)?.entregarPedido(primero)
            val reporte = capturar { mostrarReporte(pizzeria.consultarPedidos()) }
            for (texto in listOf("Completados (1): #1", "Pendientes (1): #2", "1 x Margarita", "Sin notas", "Tocar timbre", "Impuestos (19%", "Teléfono:", "Correo:")) {
                check(texto in reporte) { "Falta $texto en el reporte" }
            }
        }
        probar("La lectura con try-catch recupera texto, negativos y desbordamientos") {
            val entradaAnterior = System.`in`
            try {
                System.setIn(ByteArrayInputStream("abc\n-1\n999999999999\n3\n".toByteArray()))
                val salida = capturar { check(leerEntero("Cantidad: ", 0, 10) == 3) }
                check("Entrada inválida" in salida && "entre 0 y 10" in salida)
            } finally {
                System.setIn(entradaAnterior)
            }
        }
        println("\n$realizadas pruebas completadas correctamente.")
    }

    private fun cliente(zona: Zona = Zona.NORTE) =
        Cliente("Ana", "+56912345678", "ana@duoc.cl", "Calle de prueba 123", zona)

    private fun dinero(esperado: String, actual: BigDecimal) {
        check(actual.compareTo(BigDecimal(esperado)) == 0) { "Esperado $esperado, obtenido $actual" }
    }

    private fun probar(nombre: String, bloque: () -> Unit) {
        bloque()
        realizadas++
        println("OK: $nombre")
    }

    private inline fun <reified T : Throwable> rechaza(bloque: () -> Unit): T {
        try {
            bloque()
        } catch (error: Throwable) {
            check(error is T) { "Se esperaba ${T::class.simpleName}, ocurrió $error" }
            return error
        }
        error("Se esperaba ${T::class.simpleName} y no se lanzó ninguna excepción")
    }

    private fun capturar(bloque: () -> Unit): String {
        val salidaAnterior = System.out
        val buffer = ByteArrayOutputStream()
        try {
            System.setOut(PrintStream(buffer, true, Charsets.UTF_8))
            bloque()
        } finally {
            System.setOut(salidaAnterior)
        }
        return buffer.toString(Charsets.UTF_8)
    }
}
