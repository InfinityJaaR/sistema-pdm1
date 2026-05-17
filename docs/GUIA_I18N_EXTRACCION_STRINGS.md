# Guía i18n — Extracción de strings a resources

**Responsable de coordinar:** Eleazar (el3azar)  
**Objetivo:** Cada integrante extrae sus textos hardcodeados al archivo `res/values/strings.xml`.  
Eleazar agrega las traducciones al inglés en `res/values-en/strings.xml` cuando todos terminen.

---

## Regla de oro

> **Solo tocar `res/values/strings.xml` (español).  
> NO modificar `res/values-en/strings.xml`.**

---

## Keys comunes ya disponibles — reutilizar, NO duplicar

Antes de agregar un string nuevo, verificar si ya existe en `strings.xml`:

| Key | Valor |
|---|---|
| `btn_guardar` | Guardar |
| `btn_cancelar` | Cancelar |
| `btn_actualizar` | Actualizar |
| `btn_cerrar` | Cerrar |
| `btn_limpiar` | Limpiar |
| `btn_delete` | Eliminar |
| `btn_save` | Guardar |
| `btn_cancel` | Cancelar |
| `error_cargar_datos` | Error al cargar datos |
| `msg_accion_irreversible` | Esta acción no se puede deshacer. |
| `yes` | Sí |
| `no` | No |
| `title_options` | Opciones |
| `view` | Ver |
| `update` | Editar |
| `delete` | Eliminar |
| `placeholder_empty` | --- |
| `desc_agregar_nuevo` | Agregar nuevo |
| `desc_boton_buscar` | Buscar |
| `modal_titulo_confirmacion` | Confirmación |
| `msg_confirmacion_pregunta` | ¿Está seguro de realizar esta acción? |

---

## Convención de nombres

```
title_nuevo_xxx       → título de pantalla nueva
title_editar_xxx      → título de pantalla edición
title_detalle_xxx     → título de pantalla detalle
title_eliminar_xxx    → título de pantalla eliminar
subtitle_xxx          → subtítulo de cabecera
hint_buscar_xxx       → placeholder del buscador
field_xxx             → label de campo en formulario (ej: "Nombre *")
hint_xxx              → placeholder dentro de campo
label_xxx             → etiqueta de solo lectura en vista detalle
error_cargar_xxx      → mensaje de error al cargar lista
error_xxx             → validación de campo
msg_xxx               → mensaje informativo
btn_xxx               → texto de botón
```

---

## Cómo reemplazar en XML layouts

**Antes:**
```xml
android:text="Nuevo Taller"
android:hint="Buscar taller..."
```

**Después:**
```xml
android:text="@string/title_nuevo_taller"
android:hint="@string/hint_buscar_taller"
```

## Cómo reemplazar en Java / Activities

**Antes:**
```java
Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
formTitle.setText("Nuevo Taller");
```

**Después:**
```java
Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
formTitle.setText(getString(R.string.title_nuevo_taller));
```

---

---

## Por integrante

---

### Gabriela (gabymcanales) — Importador e Importaciones

**Archivos Java a modificar:**
```
activities/importador/ImportadorActivity.java
activities/importador/ImportadorFormActivity.java
activities/importador/ImportadorViewActivity.java
activities/importador/ImportadorDeleteActivity.java
activities/importacion/ImportacionActivity.java
activities/importacion/ImportacionFormActivity.java
activities/importacion/ImportacionViewActivity.java
activities/importacion/ImportacionDeleteActivity.java
```

**Layouts XML a modificar:**
```
res/layout/activity_importer_form.xml
res/layout/activity_importer_view.xml
res/layout/activity_importer_delete.xml
res/layout/activity_importacion_list.xml
res/layout/activity_importacion_form.xml
res/layout/activity_importacion_view.xml
res/layout/activity_importacion_delete.xml
```

**Archivo de strings a editar:**
```
res/values/strings.xml   ← agregar sus keys nuevos al final de la sección Importadores
```

> Nota: ya existen en strings.xml las keys del módulo Importador (`importers_title`, `form_title_add`, `field_name`, `field_lastname`, etc.). Revisar si ya cubren lo que se necesita antes de agregar nuevas.

---

### Yami (YamiLinares) — Vehículos y Desperfectos

**Archivos Java a modificar:**
```
activities/vehiculo/VehiculoActivity.java
activities/vehiculo/VehiculoFormDialog.java
activities/vehiculo/VehiculoViewDialog.java
activities/vehiculo/VehiculoOptionsDialog.java
activities/detalleDesperfecto/DetalleDesperfectoActivity.java
activities/detalleDesperfecto/DetalleDesperfectoFormDialog.java
activities/detalleDesperfecto/DetalleDesperfectoOptionsDialog.java
activities/fotoDesperfecto/FotoDesperfecto.java
```

**Layouts XML a modificar:**
```
res/layout/activity_vehiculo.xml
res/layout/activity_detalle_desperfecto.xml
```

**Archivo de strings a editar:**
```
res/values/strings.xml   ← agregar sus keys al final de la sección Vehículos / Desperfectos
```

> Nota: ya existen algunas keys base en strings.xml para Vehículos (`title_vehiculos`, `hint_buscar`, `label_vin`, etc.) y Desperfectos (`title_desperfectos`, `hint_buscar_desperfecto`, etc.). Revisar primero.

---

### Javier (InfinityJaaR) — Bodega, Sección y Ventas

**Archivos Java a modificar:**
```
activities/bodega/BodegaActivity.java
activities/bodega/BodegaFormDialog.java
activities/bodega/BodegaOptionsDialog.java
activities/bodega/BodegaViewDialog.java
activities/seccion/SeccionActivity.java
activities/seccion/SeccionFormDialog.java
activities/seccion/SeccionOptionsDialog.java
activities/seccion/SeccionViewDialog.java
activities/venta/VentaActivity.java
activities/venta/VentaFormDialog.java
activities/venta/VentaOptionsDialog.java
activities/venta/VentaViewDialog.java
```

**Layouts XML a modificar:**
```
res/layout/activity_bodega.xml
res/layout/activity_seccion.xml
res/layout/activity_venta.xml
```

**Archivo de strings a editar:**
```
res/values/strings.xml   ← agregar sus keys al final de las secciones Bodega / Sección / Ventas
```

> Nota: ya existen keys base para Bodega (`title_bodegas`, `hint_buscar_bodega`, `label_distrito`, etc.) y Sección (`title_secciones`, `hint_buscar_seccion`, `label_bodega`, etc.). Revisar primero.

---

### Ricardo (RicardoO4) — Talleres, Reparaciones y Modelos

**Archivos Java a modificar:**
```
activities/taller/TallerActivity.java
activities/taller/TallerFormDialog.java
activities/taller/TallerOptionsDialog.java
activities/taller/TallerViewDialog.java
activities/reparacion/ReparacionActivity.java
activities/reparacion/ReparacionFormDialog.java
activities/reparacion/ReparacionOptionsDialog.java
activities/reparacion/ReparacionViewDialog.java
activities/modelo/ModeloActivity.java
activities/modelo/ModeloFormDialog.java
activities/modelo/ModeloOptionsDialog.java
activities/modelo/ModeloViewDialog.java
```

**Layouts XML a modificar:**
```
res/layout/activity_taller.xml
res/layout/activity_reparacion.xml
res/layout/activity_modelo.xml
```

**Archivo de strings a editar:**
```
res/values/strings.xml   ← agregar sus keys nuevos al final (crear sección <!-- Taller -->, <!-- Reparación -->, <!-- Modelo -->)
```

---

### Eleazar (el3azar) — Transporte, Personal Interno, Movimiento y Marca

**Estado:** ✅ Extracción de strings completada.  
Pendiente: agregar traducción inglés de Marca en `res/values-en/strings.xml` y revisar `MarcaActivity.java` / `activity_marca.xml`.

---

## Checklist por módulo

Cada integrante puede marcar su avance:

| Módulo | Layouts XML | Java/Activities | Keys en strings.xml |
|---|---|---|---|
| Importador | ☐ | ☐ | ☐ |
| Importaciones | ☐ | ☐ | ☐ |
| Vehículos | ☐ | ☐ | ☐ |
| Desperfectos | ☐ | ☐ | ☐ |
| Bodega | ☐ | ☐ | ☐ |
| Sección | ☐ | ☐ | ☐ |
| Ventas | ☐ | ☐ | ☐ |
| Talleres | ☐ | ☐ | ☐ |
| Reparaciones | ☐ | ☐ | ☐ |
| Modelos | ☐ | ☐ | ☐ |
| Transporte | ✅ | ✅ | ✅ |
| Personal Interno | ✅ | ✅ | ✅ |
| Movimiento | ✅ | ✅ | ✅ |
| Marca | ☐ | ☐ | ✅ |
