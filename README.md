# Pizzería Kotlin

Aplicación de consola para registrar clientes, crear pedidos de pizzas, controlar ingredientes y coordinar las entregas por zona. Permite consultar el inventario, reabastecer ingredientes y obtener un reporte con los pedidos completados y pendientes.

## Configuración inicial

Cada ejecución comienza con el menú, el inventario y los repartidores indicados a continuación. Los clientes y pedidos se registran durante la sesión. Los datos se mantienen en memoria; al cerrar y volver a abrir la app, se recupera la configuración inicial.

### Menú de pizzas

Los precios están expresados en pesos y no incluyen impuesto. Cada ingrediente de la receta consume una porción por pizza.

| Pizza | Precio | Ingredientes |
| --- | --- | --- |
| Margarita | 8.500,00 | Masa, salsa y queso |
| Pepperoni | 9.500,00 | Masa, salsa, queso y pepperoni |
| Vegetariana | 9.000,00 | Masa, salsa, queso y champiñones |

### Inventario

| Ingrediente | Porciones iniciales |
| --- | --- |
| Masa | 20 |
| Salsa | 20 |
| Queso | 20 |
| Pepperoni | 10 |
| Champiñones | 10 |

Antes de confirmar un pedido se comprueba que alcance para todas sus pizzas. Los ingredientes se descuentan una sola vez, al confirmar. Si falta alguno, se muestran la cantidad que falta y los pasos para reponerlo. Cancelar el pedido no descuenta ingredientes.

La opción 5 permite agregar porciones a un ingrediente. Después de reabastecer, vuelve a crear el pedido que había sido rechazado.

### Precios, descuento e impuesto

- **Subtotal:** suma del precio de todas las pizzas.
- **Descuento:** 10% cuando el pedido contiene cuatro pizzas o más, sumando todas las variedades.
- **Impuesto:** 19% sobre el subtotal después del descuento.
- **Total:** subtotal menos descuento, más impuesto.

Los cálculos usan `BigDecimal`, con redondeo a dos decimales. Los montos se muestran con separadores de Chile, por ejemplo `38.020,50 pesos`.

### Repartidores y zonas

| Repartidor | Zona inicial | Disponibilidad inicial |
| --- | --- | --- |
| Camila | Norte | Disponible |
| Diego | Centro | Disponible |
| Sofía | Sur | Disponible |

Al registrar un cliente se selecciona su zona: `1` Norte, `2` Centro o `3` Sur. La asignación del repartidor es automática cuando el pedido pasa de Listo a En camino.

Se busca primero un repartidor disponible de la misma zona y luego se sigue este orden:

| Zona del cliente | Orden de búsqueda |
| --- | --- |
| Norte | Norte → Centro → Sur |
| Centro | Centro → Norte o Sur, con la misma prioridad |
| Sur | Sur → Centro → Norte |

Si dos repartidores tienen la misma prioridad, se conserva el orden de registro: Camila, Diego y Sofía. Cada repartidor atiende un pedido a la vez. Si todos están ocupados, el pedido permanece Listo hasta volver a intentar la asignación.

Al registrar una entrega, el repartidor queda disponible en la zona del cliente. Esa zona se utiliza para las siguientes asignaciones.

### Datos del cliente y notas

El registro solicita nombre, teléfono, correo, dirección y zona. El nombre y la dirección son obligatorios.

El teléfono admite entre 9 y 15 dígitos, con `+` inicial opcional. Si su formato es incorrecto, se guarda `(000) 000-0000`. Si el formato del correo es incorrecto, se guarda `correo@invalido.com`.

Las notas del pedido son opcionales. Si se dejan vacías, aparece `Sin notas`. Son comentarios de texto: escribir “doble queso” o “sin queso” no modifica la receta, el precio ni el inventario.

## Ejecutar la app

### Desde IntelliJ IDEA

1. Abre la carpeta `PizzeriaKotlinS2` mediante **File > Open**.
2. En **File > Project Structure > Project SDK**, selecciona un JDK 17 o superior instalado.
3. Ejecuta la configuración **Pizzería Semana 2**, o abre `src/Main.kt` y ejecuta su función `main`.

El proyecto compila para JVM 17. La biblioteca estándar de Kotlin está incluida en `lib/kotlin-stdlib.jar` y las rutas de configuración son relativas a la carpeta del proyecto.

### Desde la terminal

En macOS o Linux, abre una terminal en la carpeta del proyecto y ejecuta:

```bash
./ejecutar.sh
```

El script compila las fuentes y ejecuta `MainKt`. Guarda el archivo compilado en `build/pizzeria.jar`.

El compilador se busca en la variable `KOTLINC`, en el `PATH` o en la instalación de IntelliJ IDEA de macOS. Java se toma de `JAVA_HOME` cuando está definido; en macOS se intenta localizar un JDK 21 y, como alternativa, otro JDK instalado. En Windows, ejecuta la app desde IntelliJ IDEA.

## Opciones del programa

| Opción | Acción |
| --- | --- |
| 1 | Registrar un cliente con sus datos de contacto, dirección y zona. |
| 2 | Seleccionar cliente, agregar pizzas y cantidades, escribir notas y confirmar el pedido. |
| 3 | Marcar un pedido como Listo, asignar el reparto o registrar su entrega, según su estado actual. |
| 4 | Consultar las porciones disponibles y los ingredientes del menú. |
| 5 | Agregar porciones a un ingrediente del inventario. |
| 6 | Consultar el reporte del día y los resúmenes de los pedidos. |
| 7 | Consultar la zona y disponibilidad actual de cada repartidor. |
| 0 | Mostrar el reporte final y cerrar la app. |

Para crear un pedido, primero registra al cliente. Durante la selección de pizzas, elige una variedad y su cantidad; puedes repetir el proceso para agregar otras variedades. Escribe `0` para terminar la selección y confirma con `s`, o cancela con `n`.

### Preparación, reparto y entrega

Un pedido confirmado comienza **En preparación**. La opción 3 muestra la siguiente acción de cada pedido. Seleccionar su número realiza un paso:

| Estado actual | Acción | Resultado |
| --- | --- | --- |
| En preparación | Marcar como listo | Queda Listo, todavía sin repartidor. |
| Listo | Asignar repartidor y salir a reparto | Se asigna un repartidor disponible por zona y queda En camino. |
| En camino | Confirmar entrega al cliente | Queda Entregado y se libera al repartidor. |
| Entregado | Sin acciones pendientes | El pedido ya está completado. |

Para recorrer el flujo completo, vuelve a la opción 3 y selecciona el mismo pedido en cada paso. Los cambios los registra el operador en la consola, donde también aparecen los avisos al cliente.

El reporte incluye cliente, pizzas, cantidades, totales, estado, repartidor y notas. Los pedidos Entregados se agrupan como completados; los demás, como pendientes.

## Ejemplo de uso

El archivo [RESUMEN_EJEMPLO.txt](RESUMEN_EJEMPLO.txt) explica el recorrido completo de dos pedidos:

- **Carolina Muñoz, Centro:** dos Margarita, una Pepperoni y una Vegetariana. Recibe el descuento del 10% y su total es `38.020,50 pesos`. Diego entrega el pedido.
- **Matías Rojas, Sur:** dos Pepperoni, con un total de `22.610,00 pesos`. Su pedido queda En camino con Sofía.

Para ejecutar ese recorrido automáticamente:

```bash
./ejecutar.sh < scripts/ejemplo-entrada.txt
```

El comando ingresa las respuestas del archivo, muestra un pedido completado y uno pendiente, y cierra la sesión. Para seguir el recorrido manualmente, ejecuta `./ejecutar.sh` y utiliza los datos y pasos de `RESUMEN_EJEMPLO.txt`.

## Dónde cambiar la configuración

| Configuración | Ubicación |
| --- | --- |
| Pizzas, precios y recetas | `crearPizzeria()` en `src/Pizzeria.kt` |
| Cantidades iniciales del inventario | `crearPizzeria()` en `src/Pizzeria.kt` |
| Repartidores y sus zonas iniciales | `crearPizzeria()` en `src/Pizzeria.kt` |
| Impuesto y porcentaje de descuento | `TASA_IMPUESTO` y `TASA_DESCUENTO` en `src/Calculos.kt` |
| Condición para aplicar el descuento | `calcularTotal()` en `src/Calculos.kt` |
| Zonas y validación de contactos | `src/Usuarios.kt` |
| Prioridad de asignación por zona | `asignarRepartidor()` en `src/Pizzeria.kt` |

Los cambios en el código se aplican al volver a compilar y ejecutar la app. Los cambios de inventario realizados desde el menú duran hasta cerrar la sesión.

## Organización del código

| Archivo | Responsabilidad |
| --- | --- |
| `src/Main.kt` | Inicio, menú y operaciones de consola. |
| `src/Consola.kt` | Lectura de entradas, avisos, resúmenes y reportes. |
| `src/Pizzeria.kt` | Configuración inicial, clientes, pedidos y asignación de repartidores. |
| `src/Producto.kt` | Pizzas, ingredientes y detalle de las recetas. |
| `src/Pedido.kt` | Productos del pedido, notas y cambios de estado. |
| `src/Usuarios.kt` | Clientes, repartidores, zonas y validación de contactos. |
| `src/Inventario.kt` | Disponibilidad, consumo y reposición de ingredientes. |
| `src/Calculos.kt` | Subtotal, descuento, impuesto y formato de montos. |
| `tests/Pruebas.kt` | Pruebas automatizadas. |

## Ejecutar las pruebas

```bash
./verificar.sh
```

Se ejecutan 21 pruebas que cubren cálculos, validación de datos, ingredientes por lote, inventario, registro de pedidos, reparto por zonas, estados, disponibilidad de repartidores, reportes y recuperación de entradas inválidas. El archivo compilado de pruebas se genera en `build/pruebas.jar`.
