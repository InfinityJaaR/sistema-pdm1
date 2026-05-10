# Guía Fase 5 — Implementación de Módulos CRUD
## PDM115 · Sistema de Inventario de Vehículos · Grupo 02 · UES
**Fecha:** Mayo 2026

---

## Sección 1: Introducción

### ¿Qué es la Fase 5?

La Fase 5 es la etapa final del proyecto. Toda la infraestructura ya está lista:
la base de datos, los modelos, el sistema de login, el menú dinámico y el template
de Marca. Tu trabajo ahora es **implementar los módulos CRUD que te asignaron**,
replicando exactamente el mismo patrón que ya existe en `MarcaActivity`.

### ¿Cuál es tu rol?

Cada integrante trabaja de forma independiente **solo en su carpeta**. No debes
tocar los archivos de otro integrante ni los archivos base del sistema. Si necesitas
usar datos de otra tabla (por ejemplo un Spinner de Marcas), los lees con
`GenericDAO` directamente — no le pides al otro que te haga nada.

### ¿Cuántos módulos hace cada uno?

| Integrante | Módulos | Activities |
|-----------|---------|-----------|
| Gaby | Importador, Importación, Teléfono Importador | 2 Activities (Teléfono va dentro de Importador) |
| Yami | Vehículo, Detalle Desperfecto, Foto Desperfecto | 2 Activities (Foto va dentro de DetalleDesperfecto) |
| Eleazar | Transporte, Personal Interno, Movimiento | 3 Activities |
| Ricardo | Taller, Reparación | 2 Activities |
| Javier | Bodega, Sección, Venta | 3 Activities |

---

## Sección 2: Patrón a Replicar — Basado en Marca

Abre y estudia estos archivos antes de empezar. Son tu referencia:

```
app/src/main/java/com/ues/sistema_pdm1/activities/marca/
    MarcaActivity.java
    MarcaFormDialog.java
    MarcaOptionsDialog.java
    MarcaViewDialog.java

app/src/main/res/layout/
    activity_marca.xml
    item_marca.xml
    dialog_marca_form.xml
    dialog_marca_view.xml
```

### 2.1 Estructura de archivos que debes crear

Para cada módulo (excepto los especiales), crear exactamente esto:

```
JAVA (4 clases):
  [Modulo]Activity.java       → Lista de registros + búsqueda + botón agregar
  [Modulo]FormDialog.java     → Formulario para insertar Y actualizar (mismo dialog)
  [Modulo]OptionsDialog.java  → Dialog con 3 botones al tocar un ítem: Ver/Editar/Eliminar
  [Modulo]ViewDialog.java     → Vista de solo lectura de un registro

XML (4 layouts):
  activity_[modulo].xml       → Pantalla principal con ListView y barra de búsqueda
  item_[modulo].xml           → Fila individual del ListView
  dialog_[modulo]_form.xml    → Campos del formulario
  dialog_[modulo]_view.xml    → Datos en modo lectura
```

### 2.2 Flujo de operaciones

```
Al abrir la Activity:
├─ Se cargan todos los registros con GenericDAO.obtenerTodos()
├─ Se muestran en el ListView
└─ La barra de búsqueda filtra en tiempo real

Botón "+" (agregar):
└─ Abre [Modulo]FormDialog con campos vacíos
   └─ Al guardar → GenericDAO.insertar() → recargar lista

Tap en un ítem de la lista:
└─ Abre [Modulo]OptionsDialog con 3 opciones:
   ├─ VER      → [Modulo]ViewDialog (solo lectura, sin botones de acción)
   ├─ EDITAR   → [Modulo]FormDialog con datos precargados
   │              └─ Al guardar → GenericDAO.actualizar() → recargar lista
   └─ ELIMINAR → AlertDialog de confirmación
                  └─ Confirmar → GenericDAO.eliminar(id) → recargar lista
```

### 2.3 La regla de oro: camelCase → SCREAMING_SNAKE_CASE

`GenericDAO` mapea campos Java a columnas BD automáticamente por reflexión.
El campo Java **debe llamarse igual** que la columna BD, pero en camelCase:

```
Campo Java          Columna BD
-----------         ----------
id               → ID_[TABLA]        (PK, detectada automáticamente)
nombreMarca      → NOMBRE_MARCA
idImportador     → ID_IMPORTADOR
fechaImportacion → FECHA_IMPORTACION
aptoParaVenta    → APTO_PARA_VENTA
```

Los modelos POJO ya están creados con los campos correctos. No los modifiques.

### 2.4 Usar GenericDAO en tu Activity

```java
// En tu Activity, declarar el DAO como campo:
private GenericDAO<Importador> importadorDAO;

// En onCreate():
importadorDAO = new GenericDAO<>(this, Importador.class, "importador");

// Operaciones disponibles:
List<Importador> lista = importadorDAO.obtenerTodos();
importadorDAO.insertar(new Importador(0, ...));   // id=0 → SQLite asigna auto
importadorDAO.actualizar(importador);
importadorDAO.eliminar(importador.getId());
long total = importadorDAO.contar();
List<Importador> result = importadorDAO.obtenerPor("NOMBRE_IMPORTADOR", "Carlos");
```

### 2.5 Spinners — cargar datos de otra tabla

Si tu formulario necesita un Spinner (por ejemplo, seleccionar el Importador
al registrar una Importación), cargarlo así en tu FormDialog:

```java
// En tu FormDialog.onCreateDialog() o onViewCreated():
GenericDAO<Importador> dao = new GenericDAO<>(getContext(), Importador.class, "importador");
List<Importador> importadores = dao.obtenerTodos();

ArrayAdapter<Importador> adapter = new ArrayAdapter<>(
    getContext(), android.R.layout.simple_spinner_item, importadores);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinnerImportador.setAdapter(adapter);

// Al recuperar el seleccionado:
Importador seleccionado = (Importador) spinnerImportador.getSelectedItem();
int idImportador = seleccionado.getId();
```

> El método `toString()` de cada modelo devuelve el campo descriptivo
> (nombre, placa, VIN, etc.) y es lo que aparece en el Spinner.

---

## Sección 3: Asignación por Integrante

---

### 3.1 GABY — Importador, Importación, Teléfono Importador

**Carpeta de trabajo:** `activities/importador/` y `activities/importacion/`

---

#### IMPORTADOR

**Clase:** `ImportadorActivity`, `ImportadorFormDialog`, `ImportadorOptionsDialog`, `ImportadorViewDialog`

**Campos reales del modelo `Importador.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_IMPORTADOR | int | PK auto |
| `idDistrito` | ID_DISTRITO | int | FK → DISTRITO (Spinner) |
| `nui` | NUI | String | Número único importador |
| `nombreImportador` | NOMBRE_IMPORTADOR | String | |
| `apellidoImportador` | APELLIDO_IMPORTADOR | String | |
| `direccionImportador` | DIRECCION_IMPORTADOR | String | |
| `emailImportador` | EMAIL_IMPORTADOR | String | |
| `estado` | ESTADO | String | "activo" / "inactivo" |
| `capacidadBodegaTotal` | CAPACIDAD_BODEGA_TOTAL | int | |
| `capacidadBodegaActual` | CAPACIDAD_BODEGA_ACTUAL | int | |

**Spinner necesario:** DISTRITO → `GenericDAO<Distrito>(ctx, Distrito.class, "distrito")`

**Menú:** `Constants.MENU_IMPORTADOR = 100` (ya cableado en `MainActivity`)

---

#### IMPORTACION

**Clase:** `ImportacionActivity`, `ImportacionFormDialog`, `ImportacionOptionsDialog`, `ImportacionViewDialog`

**Campos reales del modelo `Importacion.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_IMPORTACION | int | PK auto |
| `idImportador` | ID_IMPORTADOR | int | FK → IMPORTADOR (Spinner) |
| `fechaImportacion` | FECHA_IMPORTACION | String | Formato: `yyyy-MM-dd` |

**Spinner necesario:** IMPORTADOR → `GenericDAO<Importador>(ctx, Importador.class, "importador")`

**Menú:** `Constants.MENU_IMPORTACION = 210` → agregar `case` en `MainActivity.abrirModulo()`

---

#### TELÉFONO IMPORTADOR (especial — va dentro de ImportadorFormDialog)

**No tiene Activity propia.** Se gestiona como una sub-lista dentro del formulario
de Importador.

**Campos reales del modelo `TelefonoImportador.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_TELEFONO_IMPORTADOR | int | PK auto |
| `idImportador` | ID_IMPORTADOR | int | FK → IMPORTADOR |
| `numero` | NUMERO | String | Número de teléfono |
| `tipo` | TIPO | String | Usar `Constants.TIPO_TELEFONO_CELULAR` o `TIPO_TELEFONO_FIJO` |

**Estructura sugerida dentro de `ImportadorFormDialog`:**

```
ImportadorFormDialog
├─ Campos de importador (nombre, apellido, nui, email, estado, etc.)
├─ Spinner de Distrito
├─ ─── Sección Teléfonos ───
│   ├─ ListView pequeño con teléfonos del importador
│   ├─ Botón "Agregar teléfono" → TelefonoDialog (insertar)
│   └─ Tap en teléfono → TelefonoDialog (editar) / long-tap → confirmar eliminar
└─ Botón Guardar importador
```

**DAO:** `GenericDAO<TelefonoImportador>(ctx, TelefonoImportador.class, "telefono_importador")`

Para cargar los teléfonos de un importador específico:
```java
List<TelefonoImportador> tels = telefonoDAO.obtenerPor("ID_IMPORTADOR",
    String.valueOf(importador.getId()));
```

---

### 3.2 YAMI — Vehículo, Detalle Desperfecto, Foto Desperfecto

**Carpeta de trabajo:** `activities/vehiculo/` y `activities/detalleDesperfecto/`

---

#### VEHÍCULO

**Clase:** `VehiculoActivity`, `VehiculoFormDialog`, `VehiculoOptionsDialog`, `VehiculoViewDialog`

**Campos reales del modelo `Vehiculo.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_VEHICULO | int | PK auto |
| `idImportacion` | ID_IMPORTACION | int | FK → IMPORTACION (Spinner) |
| `idModelo` | ID_MODELO | int | FK → MODELO (Spinner) |
| `idTipoVehiculo` | ID_TIPO_VEHICULO | int | FK → TIPO_VEHICULO (Spinner) |
| `idSeccion` | ID_SECCION | int | FK → SECCION (Spinner) |
| `vin` | VIN | String | Exactamente 17 caracteres (`Constants.LONGITUD_VIN`) |
| `anio` | ANIO | int | Mínimo 2021 (`Constants.ANIO_MINIMO_VEHICULO`) |
| `estadoVehiculo` | ESTADO_VEHICULO | String | "en bodega", "en reparacion", "vendido" |
| `fechaIngreso` | FECHA_INGRESO | String | Formato: `yyyy-MM-dd` |

> ⚠️ El campo `color` **NO existe** en el modelo Java actual. El modelo tiene
> `fechaIngreso` en su lugar. No lo confundas con el script SQL de referencia.

**Spinners necesarios:** IMPORTACION, MODELO, TIPO_VEHICULO, SECCION

**Triggers activos — tenerlos en cuenta:**
- `TRG_ANIO_VEHICULO`: rechaza si `anio <= 2020` → validar en FormDialog antes de insertar
- `TRG_CAPACIDAD_SECCION`: rechaza si la sección está llena → manejar la excepción
- `TRG_SECCION_INCREMENTAR`: automático, incrementa `capacidadActual` de la sección

**Menú:** `Constants.MENU_VEHICULO = 200` (ya cableado en `MainActivity`)

---

#### DETALLE DESPERFECTO

**Clase:** `DetalleDesperfectoActivity`, `DetalleDesperfectoFormDialog`, `DetalleDesperfectoOptionsDialog`, `DetalleDesperfectoViewDialog`

**Campos reales del modelo `DetalleDesperfecto.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_DETALLE_DESPERFECTO | int | PK auto |
| `idVehiculo` | ID_VEHICULO | int | FK → VEHICULO (Spinner) |
| `idTipoDesperfecto` | ID_TIPO_DESPERFECTO | int | FK → TIPO_DESPERFECTO (Spinner) |
| `descripcionDetalle` | DESCRIPCION_DETALLE | String | Texto libre |
| `estado` | ESTADO | String | "pendiente", "en reparacion", "resuelto" |

> ⚠️ El campo `fechaToma` **NO existe** en este modelo. Es `estado` en su lugar.

**Spinners necesarios:** VEHICULO, TIPO_DESPERFECTO

**Menú:** `Constants.MENU_DESPERFECTO = 610` → agregar `case` en `MainActivity.abrirModulo()`

---

#### FOTO DESPERFECTO (especial — va dentro de DetalleDesperfectoFormDialog)

**No tiene Activity propia.** Se gestiona dentro del formulario de DetalleDesperfecto.

**Campos reales del modelo `FotoDesperfecto.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_FOTO_DESPERFECTO | int | PK auto |
| `idDetalleDesperfecto` | ID_DETALLE_DESPERFECTO | int | FK → DETALLE_DESPERFECTO |
| `rutaImagen` | RUTA_IMAGEN | String | Ruta del archivo en el dispositivo |
| `fechaToma` | FECHA_TOMA | String | Formato: `yyyy-MM-dd` |

**Implementación de foto:**
- Guardar archivo en: `context.getFilesDir() + "/fotos/detalle_[id]_[timestamp].jpg"`
- Guardar ruta en BD como String
- Al mostrar: `BitmapFactory.decodeFile(rutaImagen)`
- Al eliminar: borrar archivo físico primero, luego `fotoDAO.eliminar(id)`

---

### 3.3 ELEAZAR — Transporte, Personal Interno, Movimiento

**Carpeta de trabajo:** `activities/transporte/`, `activities/personalInterno/`, `activities/movimiento/`

---

#### TRANSPORTE

**Clase:** `TransporteActivity`, `TransporteFormDialog`, `TransporteOptionsDialog`, `TransporteViewDialog`

**Campos reales del modelo `Transporte.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_TRANSPORTE | int | PK auto |
| `idTipoTransporte` | ID_TIPO_TRANSPORTE | int | FK → TIPO_TRANSPORTE (Spinner) |
| `placa` | PLACA | String | Placa del vehículo de transporte |
| `descripcion` | DESCRIPCION | String | Descripción libre |
| `capacidadDiaria` | CAPACIDAD_DIARIA | int | Cuántos vehículos mueve por día |
| `capacidadActual` | CAPACIDAD_ACTUAL | int | Cuántos lleva hoy (inicia en 0) |

**Spinner necesario:** TIPO_TRANSPORTE → `GenericDAO<TipoTransporte>(ctx, TipoTransporte.class, "tipo_transporte")`

**Menú:** `Constants.MENU_TRANSPORTE = 410` → agregar `case` en `MainActivity.abrirModulo()`

---

#### PERSONAL INTERNO

**Clase:** `PersonalInternoActivity`, `PersonalInternoFormDialog`, `PersonalInternoOptionsDialog`, `PersonalInternoViewDialog`

**Campos reales del modelo `PersonalInterno.java`:**

> Verificar en `models/PersonalInterno.java` los campos exactos antes de implementar.
> No tiene FK externa — es el módulo más simple del sistema.

**Menú:** `Constants.MENU_PERSONAL = 620` → agregar `case` en `MainActivity.abrirModulo()`

---

#### MOVIMIENTO

**Clase:** `MovimientoActivity`, `MovimientoFormDialog`, `MovimientoOptionsDialog`, `MovimientoViewDialog`

**Campos reales del modelo `Movimiento.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_MOVIMIENTO | int | PK auto |
| `idVehiculo` | ID_VEHICULO | int | FK → VEHICULO (Spinner) |
| `idPersonal` | ID_PERSONAL | int | FK → PERSONAL_INTERNO (Spinner) |
| `idTransporte` | ID_TRANSPORTE | int | FK → TRANSPORTE (Spinner) |
| `idBodegaDestino` | ID_BODEGA_DESTINO | int | FK → BODEGA (Spinner) |
| `fechaMovimiento` | FECHA_MOVIMIENTO | String | Formato: `yyyy-MM-dd` |
| `motivo` | MOTIVO | String | Motivo del movimiento |
| `observacion` | OBSERVACION | String | Texto libre |

> ⚠️ El modelo **NO tiene** `idBodegaOrigen`. Solo tiene `idBodegaDestino`.

**Spinners necesarios:** VEHICULO, PERSONAL_INTERNO, TRANSPORTE, BODEGA

**Trigger activo:**
- `TRG_CAPACIDAD_TRANSPORTE`: bloquea si el transporte ya alcanzó su límite diario
  para la misma fecha → capturar la excepción y mostrar mensaje al usuario

**Menú:** `Constants.MENU_MOVIMIENTO = 400` (ya cableado en `MainActivity`)

---

### 3.4 RICARDO — Taller, Reparación

**Carpeta de trabajo:** `activities/taller/`, `activities/reparacion/`

---

#### TALLER

**Clase:** `TallerActivity`, `TallerFormDialog`, `TallerOptionsDialog`, `TallerViewDialog`

**Campos reales del modelo `Taller.java`:**

> Verificar en `models/Taller.java` los campos exactos.
> No tiene FK externa — módulo simple similar a Marca.
> Campo `autorizado`: usar `Constants.TALLER_AUTORIZADO = 1` / `Constants.TALLER_NO_AUTORIZADO = 0`
> En el formulario mostrar como CheckBox o Spinner Sí/No.

**Menú:** `Constants.MENU_TALLER = 510` → agregar `case` en `MainActivity.abrirModulo()`

---

#### REPARACIÓN

**Clase:** `ReparacionActivity`, `ReparacionFormDialog`, `ReparacionOptionsDialog`, `ReparacionViewDialog`

**Campos reales del modelo `Reparacion.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_REPARACION | int | PK auto |
| `idVehiculo` | ID_VEHICULO | int | FK → VEHICULO (Spinner) |
| `idTaller` | ID_TALLER | int | FK → TALLER (Spinner) |
| `fechaInicio` | FECHA_INICIO | String | Formato: `yyyy-MM-dd` |
| `fechaFin` | FECHA_FIN | String | Puede ser vacío si aún no terminó |
| `aptoParaVenta` | APTO_PARA_VENTA | int | Usar `Constants.REPARACION_APTO = 1` / `Constants.REPARACION_NO_APTO = 0` |
| `costo` | COSTO | double | Costo de la reparación |
| `observacion` | OBSERVACION | String | Texto libre |

**Spinners necesarios:** VEHICULO, TALLER

**Trigger activo — muy importante:**
- `TRG_ESTADO_REPARACION`: dispara en **UPDATE** de `APTO_PARA_VENTA`, NO en INSERT
- Cuando actualices una reparación y cambies `aptoParaVenta` a 1 → el vehículo
  cambia automáticamente a `"listo"`
- Cuando cambies a `requiere_otra_reparacion = 1` → vehículo cambia a `"en reparacion"`
- El trigger **no dispara al insertar**, solo al actualizar ese campo específico

**Menú:** `Constants.MENU_REPARACION = 500` (ya cableado en `MainActivity`)

---

### 3.5 JAVIER — Bodega, Sección, Venta

**Carpeta de trabajo:** `activities/bodega/`, `activities/seccion/`, `activities/venta/`

---

#### BODEGA

**Clase:** `BodegaActivity`, `BodegaFormDialog`, `BodegaOptionsDialog`, `BodegaViewDialog`

**Campos reales del modelo `Bodega.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_BODEGA | int | PK auto |
| `idDistrito` | ID_DISTRITO | int | FK → DISTRITO (Spinner) |
| `nombreBodega` | NOMBRE_BODEGA | String | |
| `direccionBodega` | DIRECCION_BODEGA | String | |
| `capacidadTotal` | CAPACIDAD_TOTAL | int | Capacidad máxima total |
| `capacidadActual` | CAPACIDAD_ACTUAL | int | Cuántos vehículos hay ahora |

**Spinner necesario:** DISTRITO

**Menú:** `Constants.MENU_BODEGA = 300` → agregar `case` en `MainActivity.abrirModulo()`

---

#### SECCIÓN

**Clase:** `SeccionActivity`, `SeccionFormDialog`, `SeccionOptionsDialog`, `SeccionViewDialog`

**Campos reales del modelo `Seccion.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_SECCION | int | PK auto |
| `idBodega` | ID_BODEGA | int | FK → BODEGA (Spinner) |
| `nivel` | NIVEL | int | Número de nivel (1, 2, 3…) |
| `capacidadMaxima` | CAPACIDAD_MAXIMA | int | Máximo de vehículos |
| `capacidadActual` | CAPACIDAD_ACTUAL | int | Actuales (lo actualiza trigger) |

**Spinner necesario:** BODEGA

**Menú:** `Constants.MENU_SECCION = 310` → agregar `case` en `MainActivity.abrirModulo()`

---

#### VENTA

**Clase:** `VentaActivity`, `VentaFormDialog`, `VentaOptionsDialog`, `VentaViewDialog`

**Campos reales del modelo `Venta.java`:**

| Campo Java | Columna BD | Tipo | Descripción |
|-----------|-----------|------|-------------|
| `id` | ID_VENTA | int | PK auto |
| `idImportador` | ID_IMPORTADOR | int | FK → IMPORTADOR (Spinner) |
| `idVehiculo` | ID_VEHICULO | int | FK → VEHICULO (Spinner) |
| `fechaVenta` | FECHA_VENTA | String | Formato: `yyyy-MM-dd` |
| `precio` | PRECIO | double | Precio de venta |

**Spinners necesarios:** IMPORTADOR, VEHICULO

**Trigger activo — muy importante:**
- `TRG_ESTADO_VENDIDO`: dispara en **INSERT** de VENTA
- Al insertar una venta → el vehículo cambia automáticamente a `"vendido"`
- Solo se puede vender un vehículo que esté en estado `"listo"` o `"en bodega"` → validar

**Menú:** `Constants.MENU_VENTA = 600` (ya cableado en `MainActivity`)

---

## Sección 4: Archivos que NUNCA deben tocar

```
❌ PROHIBIDO MODIFICAR:
    utils/SessionManager.java
    utils/Constants.java
    utils/LlenarBDGpo02.java
    data/database/DatabaseManager.java
    data/database/DatabaseHelper.java
    data/database/DatabaseContract.java
    data/dao/GenericDAO.java
    data/dao/IBaseDAO.java
    activities/LoginActivity.java
    adapters/MenuItemAdapter.java
    models/MenuItem.java
    models/*.java          ← todos los 26 POJOs

⚠️ SOLO AGREGAR (no modificar lo existente):
    activities/MainActivity.java
        → Agregar case en el switch de abrirModulo()
    AndroidManifest.xml
        → Registrar <activity> de tus clases nuevas
```

---

## Sección 5: Cómo integrar tu módulo al menú

El menú ya funciona automáticamente para los módulos que tienen acceso definido.
Tu única tarea es agregar el `case` que abre tu Activity.

### Paso 1 — Verificar que el acceso ya existe

En `LlenarBDGpo02.java` ya están las opciones de menú y la matriz de accesos.
Busca el ID de tu módulo en `Constants.java` (sección 13 — OPCIONES DE MENÚ PRINCIPAL).

### Paso 2 — Agregar el case en MainActivity

Abre `MainActivity.java`, busca el método `abrirModulo(int idOpcion)` y agrega
tu(s) `case` al final del switch, **antes del `default`**:

```java
// ─── GABY ───
case Constants.MENU_IMPORTACION:
    startActivity(new Intent(this, ImportacionActivity.class));
    break;

// ─── YAMI ───
case Constants.MENU_DESPERFECTO:
    startActivity(new Intent(this, DetalleDesperfectoActivity.class));
    break;

// ─── ELEAZAR ───
case Constants.MENU_TRANSPORTE:
    startActivity(new Intent(this, TransporteActivity.class));
    break;
case Constants.MENU_PERSONAL:
    startActivity(new Intent(this, PersonalInternoActivity.class));
    break;

// ─── RICARDO ───
case Constants.MENU_TALLER:
    startActivity(new Intent(this, TallerActivity.class));
    break;

// ─── JAVIER ───
case Constants.MENU_BODEGA:
    startActivity(new Intent(this, BodegaActivity.class));
    break;
case Constants.MENU_SECCION:
    startActivity(new Intent(this, SeccionActivity.class));
    break;
```

> Los módulos IMPORTADOR, VEHÍCULO, MOVIMIENTO, REPARACIÓN y VENTA ya tienen
> su `case` en `MainActivity` apuntando a stubs. Solo tienes que implementar
> el contenido de esas Activities — el case ya existe.

### Paso 3 — Registrar en AndroidManifest.xml

Dentro de la etiqueta `<application>`, agregar una línea por Activity nueva:

```xml
<activity android:name=".activities.importacion.ImportacionActivity" />
<activity android:name=".activities.detalleDesperfecto.DetalleDesperfectoActivity" />
<activity android:name=".activities.transporte.TransporteActivity" />
<activity android:name=".activities.personalInterno.PersonalInternoActivity" />
<activity android:name=".activities.taller.TallerActivity" />
<activity android:name=".activities.bodega.BodegaActivity" />
<activity android:name=".activities.seccion.SeccionActivity" />
```

---

## Sección 6: Estructura de carpetas

```
app/src/main/java/com/ues/sistema_pdm1/activities/
│
├── importador/          ← GABY
│   ├── ImportadorActivity.java
│   ├── ImportadorFormDialog.java
│   ├── ImportadorOptionsDialog.java
│   ├── ImportadorViewDialog.java
│   └── TelefonoDialog.java    ← sub-dialog para teléfonos
│
├── importacion/         ← GABY
│   ├── ImportacionActivity.java
│   ├── ImportacionFormDialog.java
│   ├── ImportacionOptionsDialog.java
│   └── ImportacionViewDialog.java
│
├── vehiculo/            ← YAMI
│   ├── VehiculoActivity.java
│   ├── VehiculoFormDialog.java
│   ├── VehiculoOptionsDialog.java
│   └── VehiculoViewDialog.java
│
├── detalleDesperfecto/  ← YAMI
│   ├── DetalleDesperfectoActivity.java
│   ├── DetalleDesperfectoFormDialog.java  ← incluye gestión de fotos
│   ├── DetalleDesperfectoOptionsDialog.java
│   └── DetalleDesperfectoViewDialog.java
│
├── transporte/          ← ELEAZAR
│   └── [4 clases]
│
├── personalInterno/     ← ELEAZAR
│   └── [4 clases]
│
├── movimiento/          ← ELEAZAR
│   └── [4 clases]
│
├── taller/              ← RICARDO
│   └── [4 clases]
│
├── reparacion/          ← RICARDO
│   └── [4 clases]
│
├── bodega/              ← JAVIER
│   └── [4 clases]
│
├── seccion/             ← JAVIER
│   └── [4 clases]
│
└── venta/               ← JAVIER
    └── [4 clases]

app/src/main/res/layout/
    activity_importador.xml, item_importador.xml, dialog_importador_form.xml, dialog_importador_view.xml
    activity_importacion.xml, item_importacion.xml, dialog_importacion_form.xml, dialog_importacion_view.xml
    activity_vehiculo.xml, item_vehiculo.xml, dialog_vehiculo_form.xml, dialog_vehiculo_view.xml
    ... (mismo patrón para cada módulo)
```

---

## Sección 7: Validaciones obligatorias

### Campos obligatorios

Antes de llamar a `insertar()` o `actualizar()`, verificar en el FormDialog:

```
- Ningún campo obligatorio vacío → mostrar Constants.MSG_CAMPO_REQUERIDO
- VIN: exactamente 17 caracteres (Constants.LONGITUD_VIN)
- NUI: 14 caracteres (Constants.LONGITUD_NUI)
- Año de vehículo: >= Constants.ANIO_MINIMO_VEHICULO (2021)
- Email: contiene "@" y "."
- Precio/Costo: mayor que 0
```

### Manejo de excepciones de triggers

Los triggers de SQLite lanzan una excepción cuando se viola una restricción.
Capturarla así en tu Activity:

```java
try {
    vehiculoDAO.insertar(vehiculo);
    cargarLista();
} catch (Exception e) {
    String mensaje = e.getMessage();
    if (mensaje != null && mensaje.contains("anio")) {
        Toast.makeText(this, "El año debe ser mayor a 2020", Toast.LENGTH_SHORT).show();
    } else if (mensaje != null && mensaje.contains("capacidad")) {
        Toast.makeText(this, "La sección está llena", Toast.LENGTH_SHORT).show();
    } else {
        Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
    }
}
```

### Verificar sesión activa

Al inicio de cada Activity, copiar el mismo bloque que tiene `MarcaActivity`:

```java
if (!SessionManager.getInstance().isLoggedIn()) {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
    return;
}
```

---

## Sección 8: Checklist antes de entregar

Verificar cada punto antes de hacer commit:

```
COMPILACIÓN
□ Compila sin errores ni warnings rojos

MENÚ E INTEGRACIÓN
□ La Activity aparece en el menú al iniciar sesión con el usuario correcto
□ case agregado en MainActivity.abrirModulo()
□ Activity registrada en AndroidManifest.xml

CRUD COMPLETO
□ INSERTAR: guarda el nuevo registro y aparece en la lista
□ CONSULTAR: muestra todos los registros en el ListView
□ ACTUALIZAR: edita el registro y el cambio se refleja en la lista
□ ELIMINAR: pide confirmación y borra el registro

CALIDAD
□ Búsqueda filtra en tiempo real (mayúsculas/minúsculas ignoradas)
□ Spinners cargan datos correctamente (no están vacíos)
□ Validaciones rechazan datos inválidos con mensaje claro
□ Triggers manejados con catch y mensaje al usuario

SESIÓN Y PERMISOS
□ Si no hay sesión activa, redirige a LoginActivity
□ No se modificó SessionManager, Constants ni LlenarBDGpo02

FOREIGN KEYS
□ No se puede insertar un registro con FK que no existe
□ Spinner muestra solo registros existentes en la tabla relacionada
```

---

## Referencia rápida — IDs de menú en Constants.java

| Constante | Valor | Integrante | Case ya en MainActivity |
|-----------|-------|-----------|------------------------|
| `MENU_IMPORTADOR` | 100 | Gaby | ✅ sí (stub) |
| `MENU_VEHICULO` | 200 | Yami | ✅ sí (stub) |
| `MENU_IMPORTACION` | 210 | Gaby | ❌ agregar |
| `MENU_BODEGA` | 300 | Javier | ❌ agregar |
| `MENU_SECCION` | 310 | Javier | ❌ agregar |
| `MENU_MOVIMIENTO` | 400 | Eleazar | ✅ sí (stub) |
| `MENU_TRANSPORTE` | 410 | Eleazar | ❌ agregar |
| `MENU_REPARACION` | 500 | Ricardo | ✅ sí (stub) |
| `MENU_TALLER` | 510 | Ricardo | ❌ agregar |
| `MENU_VENTA` | 600 | Javier | ✅ sí (stub) |
| `MENU_DESPERFECTO` | 610 | Yami | ❌ agregar |
| `MENU_PERSONAL` | 620 | Eleazar | ❌ agregar |
| `MENU_MARCA` | 630 | Base | ✅ sí (completo) |
| `MENU_TIPO_TRANSPORTE` | 640 | Sin asignar | ❌ agregar |
| `MENU_TIPO_VEHICULO` | 650 | Sin asignar | ❌ agregar |