# Ejemplo de uso: pedidos a domicilio

Este recorrido muestra cómo registrar clientes, confirmar pedidos, preparar pizzas, asignar repartidores y consultar el inventario y el reporte del día.

Los datos se conservan únicamente durante cada ejecución del programa.

## 1. Registrar clientes — Opción 1

Registrar los siguientes clientes:

| Dato | Carolina Muñoz | Matías Rojas |
|---|---|---|
| Teléfono | +56900001001 | +56900001002 |
| Correo | carolina.munoz@example.com | matias.rojas@example.com |
| Dirección | Av. Los Aromos 1240, departamento 302 | Pasaje Las Acacias 485, casa 2 |
| Zona | Centro | Sur |

Carolina realiza un pedido familiar y Matías solicita dos pizzas para compartir.

## 2. Confirmar el pedido de Carolina — Opción 2

Seleccionar a Carolina y agregar:

| Pizza | Cantidad | Precio unitario sin impuesto |
|---|---:|---:|
| Margarita | 2 | 8.500,00 pesos |
| Pepperoni | 1 | 9.500,00 pesos |
| Vegetariana | 1 | 9.000,00 pesos |

**Notas:** Entregar en conserjería, departamento 302.

El pedido contiene cuatro pizzas, por lo que recibe un **10% de descuento**, aunque sean de distintas variedades.

| Concepto | Monto |
|---|---:|
| Subtotal | 35.500,00 pesos |
| Descuento del 10% | 3.550,00 pesos |
| Impuesto del 19% sobre el subtotal descontado | 6.070,50 pesos |
| **Total final** | **38.020,50 pesos** |

Confirmar con `s`. El programa descuenta los ingredientes del inventario y registra el **pedido #1** con estado **En preparación**, todavía sin repartidor.

## 3. Preparar y despachar el pedido #1 — Opción 3

Seleccionar el pedido #1 dos veces:

1. La primera selección lo marca como **Listo**.
2. La segunda asigna un repartidor y cambia su estado a **En camino**.

Diego está disponible en la zona Centro, por lo que recibe el pedido.

```text
Pedido #1 marcado como Listo. Aún no tiene repartidor.
Repartidor asignado: Diego (zona Centro).
Pedido #1 En camino. Diego tiene este pedido asignado hasta confirmar la entrega.
```

## 4. Atender a Matías mientras Diego está en reparto

En la **opción 2**, seleccionar a Matías y agregar **2 pizzas Pepperoni**.

**Notas:** Llamar al llegar; portón gris.

| Concepto | Monto |
|---|---:|
| Subtotal | 19.000,00 pesos |
| Descuento | 0,00 pesos |
| Impuesto del 19% | 3.610,00 pesos |
| **Total final** | **22.610,00 pesos** |

Este pedido no recibe descuento porque contiene solo dos pizzas.

Confirmar con `s`. Luego, en la **opción 3**, seleccionar el **pedido #2** dos veces para marcarlo como **Listo** y despacharlo con Sofía, de la zona Sur.

```text
Repartidor asignado: Sofía (zona Sur).
Pedido #2 En camino. Sofía tiene este pedido asignado hasta confirmar la entrega.
```

## 5. Registrar la entrega a Carolina — Opción 3

Cuando Diego entregue el pedido en conserjería, seleccionar el **pedido #1** para registrar la entrega.

```text
Entrega del pedido #1 registrada. Diego vuelve a estar disponible.
```

El pedido #1 queda **Entregado**, mientras que el pedido #2 continúa **En camino**.

## 6. Consultar inventario y repartidores — Opciones 4 y 7

Se confirmaron seis pizzas en total, por lo que se consumieron:

- Seis porciones de masa.
- Seis porciones de salsa.
- Seis porciones de queso.
- Tres porciones de pepperoni.
- Una porción de champiñones.

Los ingredientes se descuentan al confirmar el pedido. Registrar la entrega no vuelve a descontarlos.

### Inventario resultante — Opción 4

```text
masa: 14
salsa: 14
queso: 14
pepperoni: 7
champiñones: 9
Ingredientes distintos del menú: masa, salsa, queso, pepperoni, champiñones
```

### Disponibilidad de repartidores — Opción 7

Al despachar, el programa busca primero un repartidor disponible en la zona del cliente y luego en las zonas más cercanas.

Después de una entrega, el repartidor queda disponible en la zona del cliente.

```text
Repartidor: Camila | Zona: Norte | Disponible
Repartidor: Diego | Zona: Centro | Disponible
Repartidor: Sofía | Zona: Sur | Ocupado
```

## 7. Revisar el reporte del día — Opción 6

Al finalizar este recorrido hay un pedido completado y uno pendiente.

```text
=== Reporte de pedidos del día ===
#1 | Carolina Muñoz | 2 x Margarita, 1 x Pepperoni, 1 x Vegetariana | 38.020,50 pesos | Entregado
#2 | Matías Rojas | 2 x Pepperoni | 22.610,00 pesos | En camino

Completados (1): #1
Pendientes (1): #2
```

### Resumen del pedido #1

```text
========================================
RESUMEN DEL PEDIDO #1
========================================
Cliente: Carolina Muñoz | Teléfono: +56900001001 | Correo: carolina.munoz@example.com
Dirección: Av. Los Aromos 1240, departamento 302
Zona de entrega: Centro

Productos (precios sin impuesto):
- Margarita | 8.500,00 pesos c/u | Cantidad: 2
- Pepperoni | 9.500,00 pesos c/u | Cantidad: 1
- Vegetariana | 9.000,00 pesos c/u | Cantidad: 1

Subtotal: 35.500,00 pesos
Descuento (10%): 3.550,00 pesos
Impuestos (19% sobre el subtotal descontado): 6.070,50 pesos
TOTAL FINAL: 38.020,50 pesos

Estado: Entregado
Repartidor: Diego
Notas: Entregar en conserjería, departamento 302.
========================================
```

### Resumen del pedido #2

```text
========================================
RESUMEN DEL PEDIDO #2
========================================
Cliente: Matías Rojas | Teléfono: +56900001002 | Correo: matias.rojas@example.com
Dirección: Pasaje Las Acacias 485, casa 2
Zona de entrega: Sur

Productos (precios sin impuesto):
- Pepperoni | 9.500,00 pesos c/u | Cantidad: 2

Subtotal: 19.000,00 pesos
Descuento (sin descuento): 0,00 pesos
Impuestos (19% sobre el subtotal descontado): 3.610,00 pesos
TOTAL FINAL: 22.610,00 pesos

Estado: En camino
Repartidor: Sofía
Siguiente paso: opción 3, pedido #2 → Confirmar entrega al cliente.
Notas: Llamar al llegar; portón gris.
========================================
```

Para completar la entrega a Matías manualmente, ingresar a la **opción 3**, seleccionar el **pedido #2** y confirmar su entrega.

Sofía quedará disponible y el reporte mostrará **dos pedidos completados y ningún pendiente**.

## Ejecutar la demostración automática

Desde la carpeta del proyecto, ejecutar:

```bash
./ejecutar.sh < scripts/ejemplo-entrada.txt
```

La demostración automática muestra el reporte y cierra la sesión con el **pedido #2 En camino**.

**Los datos se conservan solo durante cada ejecución.**
