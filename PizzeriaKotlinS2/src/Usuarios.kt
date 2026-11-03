const val TELEFONO_PREDETERMINADO = "(000) 000-0000"
const val CORREO_PREDETERMINADO = "correo@invalido.com"

fun validarTelefono(entrada: String): String = entrada.trim().takeIf {
    it.matches(Regex("\\+?[0-9]{9,15}"))
} ?: TELEFONO_PREDETERMINADO

fun validarCorreo(entrada: String): String = entrada.trim().takeIf {
    it.matches(Regex("[A-Za-z0-9_+%-]+(?:\\.[A-Za-z0-9_+%-]+)*@[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*(?:\\.[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*)*\\.[A-Za-z]{2,}"))
} ?: CORREO_PREDETERMINADO

// Orden de las zonas para representar cercanía de forma sencilla en el ejercicio.
enum class Zona(val descripcion: String) {
    NORTE("Norte"),
    CENTRO("Centro"),
    SUR("Sur")
}

open class Usuario(val nombre: String, val contacto: String) {
    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío." }
    }

    open fun describir(): String = "$nombre | Contacto: $contacto"
}

class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String,
    val zona: Zona
) : Usuario(nombre.trim(), validarTelefono(telefono)) {
    val correo: String = validarCorreo(correo)

    init {
        require(direccion.isNotBlank()) { "La dirección no puede estar vacía." }
    }

    override fun describir(): String =
        "Cliente: $nombre | Teléfono: $contacto | Correo: $correo"
}

class Repartidor(nombre: String, contacto: String, zona: Zona) : Usuario(nombre, contacto) {
    var zona: Zona = zona
        private set
    var disponible: Boolean = true
        private set
    private var pedidoAsignado: Pedido? = null

    fun asignarPedido(pedido: Pedido) {
        require(disponible) { "$nombre ya tiene un pedido asignado." }
        pedido.iniciarReparto(this)
        pedidoAsignado = pedido
        disponible = false
    }

    fun entregarPedido(pedido: Pedido) {
        require(pedidoAsignado === pedido) { "Este pedido no está asignado a $nombre." }
        pedido.finalizarEntrega(this)
        zona = pedido.cliente.zona
        pedidoAsignado = null
        disponible = true
    }

    override fun describir(): String =
        "Repartidor: $nombre | Zona: ${zona.descripcion} | " +
            if (disponible) "Disponible" else "Ocupado"
}
