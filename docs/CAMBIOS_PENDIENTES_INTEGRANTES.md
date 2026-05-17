# Cambios Pendientes por Integrante
**PDM115 — Grupo 02 — Sistema de Inventario de Vehículos**

> Este documento detalla los ajustes menores que cada integrante debe aplicar
> en su propio código. Los triggers ya están implementados en `DatabaseHelper.java`
> por el encargado — ustedes solo adaptan sus Activities y Dialogs.

---

## Constantes disponibles en `Constants.java`

Antes de ver los cambios, usa estas constantes en lugar de strings hardcodeados.
Son más seguras y si el valor cambia algún día, solo se cambia en un lugar:

```java
// Estados de vehículo
Constants.ESTADO_VEHICULO_ALMACENADO    // "en bodega"
Constants.ESTADO_VEHICULO_EN_REPARACION // "en reparacion"
Constants.ESTADO_VEHICULO_LISTO         // "listo para venta"
Constants.ESTADO_VEHICULO_VENDIDO       // "vendido"

// Reparación
Constants.REPARACION_APTO               // 1
Constants.REPARACION_NO_APTO            // 0

// Mensajes genéricos
Constants.MSG_CAMPO_REQUERIDO
Constants.MSG_OPERACION_EXITOSA
```

---

## Regla general para TODOS — Mensaje al eliminar

Los triggers de la BD ahora bloquean eliminaciones cuando un registro tiene
datos relacionados. Para que el usuario vea el motivo real, **cambia UNA línea**
en cada `confirmarEliminar` de tus Activities:

```java
// ANTES (mensaje genérico — el usuario no sabe por qué falló):
} catch (Exception e) {
    Toast.makeText(this, "Error al eliminar X", Toast.LENGTH_SHORT).show();
}

// DESPUÉS (muestra el mensaje exacto del trigger):
} catch (Exception e) {
    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
}
```

**No necesitas validar en Java si hay hijos antes de eliminar.**
El trigger lo verifica en la BD automáticamente. Java solo intenta eliminar
y si falla muestra el motivo. Sin duplicar lógica.

---

## RICARDO — Taller, Reparación

### 1. `ReparacionFormDialog` — Filtro de vehículos al crear

El spinner de vehículos actualmente solo muestra `"en reparacion"`.
Debe mostrar también `"en bodega"` porque un vehículo recién llegado
puede enviarse directo a reparación sin cambiar su estado manualmente.

```java
// ANTES:
if ("en reparacion".equals(v.getEstadoVehiculo())) {
    vehiculos.add(v);
}

// DESPUÉS:
String est = v.getEstadoVehiculo();
if (Constants.ESTADO_VEHICULO_ALMACENADO.equals(est) ||
    Constants.ESTADO_VEHICULO_EN_REPARACION.equals(est)) {
    vehiculos.add(v);
}
```

### 2. `ReparacionFormDialog` — Checkboxes solo al editar

`cbAptoParaVenta` y `cbRequiereOtraReparacion` no tienen sentido al crear
una reparación nueva (el trabajo aún no se ha hecho). Deben estar
deshabilitados al crear y habilitados solo al editar.

```java
// Agregar esto donde detectas si es edición o creación:
cbAptoParaVenta.setEnabled(esEdicion);
cbRequiereOtraReparacion.setEnabled(esEdicion);

// Al crear también conviene fijarlos en false:
if (!esEdicion) {
    cbAptoParaVenta.setChecked(false);
    cbRequiereOtraReparacion.setChecked(false);
}
```

### 3. `TallerActivity` y `ReparacionActivity` — Mensaje al eliminar

Aplicar la regla general descrita arriba en ambos Activities.

---

## JAVIER — Bodega, Sección, Venta

### 1. `SeccionFormDialog` — Campo capacidad actual de solo lectura al editar

`CAPACIDAD_ACTUAL` es mantenida automáticamente por triggers de la BD.
Si el usuario la edita manualmente rompe el conteo. Debe ser de solo lectura:

```java
// Agregar junto al bloque if (esEdicion):
etCapacidadActual.setEnabled(false);

// Si quieres también al crear (empieza siempre en 0):
if (!esEdicion) {
    etCapacidadActual.setText("0");
    etCapacidadActual.setEnabled(false);
}
```

### 2. `BodegaActivity`, `SeccionActivity`, `VentaActivity` — Mensaje al eliminar

Aplicar la regla general descrita arriba en los tres Activities.

### 3. Uso de constantes en `VentaFormDialog` (opcional pero recomendado)

```java
// ANTES (líneas 129-131, condición duplicada):
estado.equalsIgnoreCase("en bodega") ||
estado.equalsIgnoreCase("listo para venta") ||
estado.equalsIgnoreCase(Constants.ESTADO_VEHICULO_ALMACENADO)

// DESPUÉS (limpio, sin duplicado):
Constants.ESTADO_VEHICULO_ALMACENADO.equalsIgnoreCase(estado) ||
Constants.ESTADO_VEHICULO_LISTO.equalsIgnoreCase(estado)
```

---

## YAMI — Vehículo, Detalle Desperfecto, Foto Desperfecto

### 1. `VehiculoFormDialog` — Spinner de años dinámico

El spinner actualmente tiene años hardcodeados (`2021` a `2026`).
El trigger de la BD ya rechaza vehículos con más de 5 años de antigüedad
de forma dinámica. El spinner debe coincidir:

```java
// ANTES:
for (int i = 2021; i <= 2026; i++) anios.add(i);

// DESPUÉS:
int anioActual = Calendar.getInstance().get(Calendar.YEAR);
int anioMinimo = anioActual - 5;
for (int i = anioMinimo; i <= anioActual; i++) anios.add(i);
```

Asegúrate de tener importado: `import java.util.Calendar;`

### 2. `VehiculoActivity`, `DetalleDesperfectoActivity`, `FotoDesperfectoActivity` — Mensaje al eliminar

Aplicar la regla general descrita arriba en los tres Activities.

---

## GABY — Importador, Teléfono Importador, Importación

### 1. `ImportadorActivity`, `ImportacionActivity`, `TelefonoImportadorActivity` — Mensaje al eliminar

Aplicar la regla general descrita arriba en los tres Activities.

> **Nota:** el trigger de importador verifica en orden: teléfonos → importaciones → ventas.
> Si el importador tiene cualquiera de esos registros, la eliminación se bloquea
> y el usuario verá el motivo exacto.

---

## ELEAZAR — Personal Interno, Transporte, Movimiento

### 1. `PersonalInternoActivity`, `TransporteActivity` — Mensaje al eliminar

Aplicar la regla general descrita arriba en ambos Activities.

> **Nota:** Movimiento es tabla hoja (no tiene hijos), no necesita cambio de mensaje.
> El trigger bloquea borrar Personal o Transporte que tenga movimientos registrados.

---

## Resumen rápido de cambios por integrante

| Integrante | Archivos a modificar | Tipo de cambio |
|---|---|---|
| Ricardo | `ReparacionFormDialog` | Filtro vehículos + deshabilitar checkboxes |
| Ricardo | `TallerActivity`, `ReparacionActivity` | 1 línea en catch |
| Javier | `SeccionFormDialog` | Deshabilitar campo capacidad actual |
| Javier | `BodegaActivity`, `SeccionActivity`, `VentaActivity` | 1 línea en catch |
| Yami | `VehiculoFormDialog` | Spinner años dinámico (3 líneas) |
| Yami | `VehiculoActivity`, `DetalleDesperfectoActivity`, `FotoDesperfectoActivity` | 1 línea en catch |
| Gaby | `ImportadorActivity`, `ImportacionActivity`, `TelefonoImportadorActivity` | 1 línea en catch |
| Eleazar | `PersonalInternoActivity`, `TransporteActivity` | 1 línea en catch |

---

## Lo que NO deben hacer

- **No validar en Java si un registro tiene hijos antes de eliminar** — el trigger lo hace.
- **No activar `PRAGMA foreign_keys`** — no está habilitado en este proyecto.
- **No modificar** `DatabaseHelper.java`, `Constants.java`, `SessionManager.java`,
  `LlenarBDGpo02.java`, `GenericDAO.java` ni ningún POJO en `models/`.
- **No cambiar los valores de estado** — deben coincidir exactamente con las constantes:
  `"en bodega"`, `"en reparacion"`, `"listo para venta"`, `"vendido"`.
