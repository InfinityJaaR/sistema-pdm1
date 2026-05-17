# Arquitectura PDM115 — Resumen Ejecutivo
## Sistema de Inventario de Vehículos · Grupo 02 · Universidad de El Salvador
**Fecha:** Mayo 2026

---

## Sección 1: Visión General

### Qué es el proyecto

Aplicación Android nativa para gestionar el inventario de vehículos importados de una empresa.
Cubre el ciclo completo: importación → almacenamiento en bodegas → movimientos → reparaciones → venta.

### Tecnología

| Aspecto | Decisión |
|---------|----------|
| Lenguaje | Java 11 puro — sin Kotlin |
| Base de datos | SQLite nativo (sin Room) |
| Red | Sin Retrofit ni llamadas HTTP |
| Dependencias externas | Ninguna — solo AndroidX estándar |
| `minSdk` | 24 (Android 7.0 Nougat) |
| `targetSdk` | 36 (Android 16) |
| Paquete | `com.ues.sistema_pdm1` |
| applicationId | `com.example.sistema_pdm1` |

### Equipo

| Integrante | Módulos asignados |
|-----------|------------------|
| Gaby | Importador, Importación, Teléfono Importador |
| Yami | Vehículo, Detalle Desperfecto, Foto Desperfecto |
| Eleazar | Transporte, Personal Interno, Movimiento |
| Ricardo | Taller, Reparación |
| Javier | Bodega, Sección, Venta |

### Estado actual

**Fases 1–4 completas. Fase 5 en progreso.**

---

## Sección 2: Fases del Proyecto

| Fase | Descripción | Estado |
|------|-------------|--------|
| **FASE 1** | Base de datos: 26 tablas + 7 triggers + DatabaseContract/Helper/Manager | ✅ Completa |
| **FASE 2** | 26 modelos POJO + SessionManager + Constants + LlenarBDGpo02 | ✅ Completa |
| **FASE 3** | LoginActivity + MainActivity con menú de tarjetas en cuadrícula, control de acceso por rol | ✅ Completa |
| **FASE 4** | Template CRUD de Marca: MarcaActivity + 3 Dialogs + 4 layouts | ✅ Completa |
| **FASE 5** | CRUDs por módulo, siguiendo el patrón de Marca, uno por integrante | 🔄 En progreso |

### Detalle de cada fase

**FASE 1 — Base de datos**
- `DatabaseContract.java` — constantes de nombres de tablas y columnas
- `DatabaseHelper.java` — extiende `SQLiteOpenHelper`; crea las 26 tablas y 7 triggers en `onCreate()`
- `DatabaseManager.java` — singleton; siempre usar `DatabaseManager.getInstance(context)`

**FASE 2 — Modelos y utilidades**
- 26 POJOs en `models/` — uno por tabla, campos en camelCase que `GenericDAO` mapea a SCREAMING_SNAKE_CASE
- `SessionManager.java` — gestión de sesión activa, login/logout, verificación de permisos por opción
- `Constants.java` — IDs de menú, estados de vehículo, formatos de fecha, límites, mensajes, extras de Intent
- `LlenarBDGpo02.java` — precarga las 26 tablas con datos demo al arrancar la app por primera vez

**FASE 3 — Autenticación y menú**
- `LoginActivity.java` — formulario de login, llama `SessionManager.login()`, redirige a `MainActivity`
- `MainActivity.java` — menú de tarjetas en cuadrícula (2 columnas, 7 filas) dentro de un `ScrollView`; visibilidad de cada tarjeta controlada por `tieneAcceso()`; filas completas se ocultan si ninguna de sus tarjetas tiene acceso; click en el bloque ícono+nombre del header dispara logout con `AlertDialog` de confirmación

**FASE 4 — Template de Marca**
- `MarcaActivity.java` — lista con búsqueda en tiempo real, botón agregar
- `MarcaFormDialog.java` — formulario de insertar y actualizar (mismo dialog, doble uso)
- `MarcaOptionsDialog.java` — dialog al tocar ítem: Ver / Editar / Eliminar
- `MarcaViewDialog.java` — detalle de solo lectura

---

## Sección 3: Arquitectura de Capas

```
┌──────────────────────────────────────────────────────┐
│                   CAPA DE UI                          │
│  LoginActivity         → autenticación               │
│  MainActivity          → cuadrícula de tarjetas      │
│                           2 col × 7 filas, ScrollView│
│                           visibilidad por rol         │
│                           logout en header           │
│  [Modulo]Activity      → lista + búsqueda            │
│  [Modulo]FormDialog    → insertar / actualizar       │
│  [Modulo]OptionsDialog → Ver / Editar / Eliminar     │
│  [Modulo]ViewDialog    → solo lectura                │
└───────────────────────────┬──────────────────────────┘
                             │
┌───────────────────────────▼──────────────────────────┐
│               CAPA DE LÓGICA DE NEGOCIO               │
│  SessionManager    → sesión, login, tieneAcceso()    │
│  Constants         → IDs, estados, mensajes, límites │
│  LlenarBDGpo02     → datos demo iniciales            │
└───────────────────────────┬──────────────────────────┘
                             │
┌───────────────────────────▼──────────────────────────┐
│              CAPA DE ACCESO A DATOS                   │
│  IBaseDAO<T>       → interfaz: insertar/actualizar/  │
│                      eliminar/obtener                │
│  GenericDAO<T>     → implementación por reflexión    │
│                      Java; mapeo automático          │
│                      camelCase ↔ SCREAMING_SNAKE     │
└───────────────────────────┬──────────────────────────┘
                             │
┌───────────────────────────▼──────────────────────────┐
│              CAPA DE BASE DE DATOS                    │
│  DatabaseManager   → singleton, abre/cierra BD       │
│  DatabaseHelper    → SQLiteOpenHelper, crea tablas   │
│  DatabaseContract  → nombres de tablas y columnas    │
└───────────────────────────┬──────────────────────────┘
                             │
┌───────────────────────────▼──────────────────────────┐
│                     SQLite                            │
│  inventario_pdm1.db — 26 tablas + 32 triggers         │
└──────────────────────────────────────────────────────┘
```

**Reglas que nunca se violan:**
- `DatabaseManager` siempre como singleton: `DatabaseManager.getInstance(context)`
- Cada método DAO abre y cierra su conexión con `try-finally`
- Sin `PRAGMA foreign_keys = ON` — integridad referencial validada en código
- Constructor vacío obligatorio en todos los POJOs
- `toString()` en cada POJO retorna el campo más descriptivo (para Spinners)

---

## Sección 4: Base de Datos — 26 Tablas

### Orden de dependencias (FKs)

| # | Tabla | Depende de |
|---|-------|-----------|
| 1 | OPCIONCRUD | — |
| 2 | USUARIO | — |
| 3 | ACCESOUSUARIO | USUARIO, OPCIONCRUD |
| 4 | PAIS | — |
| 5 | DEPARTAMENTO | PAIS |
| 6 | MUNICIPIO | DEPARTAMENTO |
| 7 | DISTRITO | MUNICIPIO |
| 8 | TIPO_VEHICULO | — |
| 9 | TIPO_TRANSPORTE | — |
| 10 | TIPO_DESPERFECTO | — |
| 11 | MARCA | — |
| 12 | MODELO | MARCA |
| 13 | TALLER | — |
| 14 | PERSONAL_INTERNO | — |
| 15 | BODEGA | DISTRITO |
| 16 | SECCION | BODEGA |
| 17 | IMPORTADOR | DISTRITO |
| 18 | TELEFONO_IMPORTADOR | IMPORTADOR |
| 19 | IMPORTACION | IMPORTADOR |
| 20 | TRANSPORTE | TIPO_TRANSPORTE |
| 21 | VEHICULO | IMPORTACION, MODELO, TIPO_VEHICULO, SECCION |
| 22 | DETALLE_DESPERFECTO | VEHICULO, TIPO_DESPERFECTO |
| 23 | FOTO_DESPERFECTO | DETALLE_DESPERFECTO |
| 24 | MOVIMIENTO | VEHICULO, PERSONAL_INTERNO, TRANSPORTE, BODEGA |
| 25 | REPARACION | VEHICULO, TALLER |
| 26 | VENTA | VEHICULO, IMPORTADOR |

### 32 Triggers

| Trigger | Tabla | Cuándo dispara | Efecto |
|---------|-------|---------------|--------|
| TRG_ANIO_VEHICULO | VEHICULO | BEFORE INSERT | Rechaza si antigüedad > 5 años (`strftime('%Y','now') - 5`) |
| TRG_CAPACIDAD_SECCION | VEHICULO | BEFORE INSERT | Rechaza si sección está llena al insertar |
| TRG_CAPACIDAD_SECCION_UPDATE | VEHICULO | BEFORE UPDATE de ID_SECCION | Rechaza si sección destino está llena al mover |
| TRG_CAPACIDAD_TRANSPORTE | MOVIMIENTO | BEFORE INSERT | Rechaza si transporte alcanzó límite diario |
| TRG_SECCION_INCREMENTAR | VEHICULO | AFTER INSERT | Suma +1 a `CAPACIDAD_ACTUAL` de la sección |
| TRG_SECCION_MOVER | VEHICULO | AFTER UPDATE de ID_SECCION | Resta -1 sección origen, suma +1 sección destino |
| TRG_SECCION_DECREMENTAR | VEHICULO | AFTER DELETE | Resta -1 a `CAPACIDAD_ACTUAL` de la sección |
| TRG_ESTADO_VENDIDO | VENTA | AFTER INSERT | Cambia vehículo a `'vendido'` automáticamente |
| TRG_ESTADO_VENTA_DELETE | VENTA | AFTER DELETE | Revierte vehículo a `'listo para venta'` al eliminar venta |
| TRG_ESTADO_VENTA_UPDATE | VENTA | AFTER UPDATE de ID_VEHICULO | Revierte vehículo anterior y marca el nuevo como `'vendido'` |
| TRG_ESTADO_REPARACION_INSERT | REPARACION | AFTER INSERT | Cambia vehículo a `'en reparacion'` siempre |
| TRG_ESTADO_REPARACION | REPARACION | AFTER UPDATE de APTO_PARA_VENTA | Cambia vehículo a `'listo para venta'` o `'en reparacion'` |

> **Flujo de estados de vehículo:**
> `'en bodega'` → *(crear reparación)* → `'en reparacion'` → *(editar, aptoParaVenta=1)* → `'listo para venta'` → *(registrar venta)* → `'vendido'`
>
> `TRG_ESTADO_REPARACION_INSERT` dispara en INSERT y siempre pone `'en reparacion'`.
> `TRG_ESTADO_REPARACION` dispara en UPDATE de `APTO_PARA_VENTA` y transiciona a `'listo para venta'`.

---

## Sección 5: Clasificación de las 26 Tablas

### Las 15 CRUDs completas

| # | Tabla | Integrante | Ubicación CRUD | FKs externas | Datos precargados |
|---|-------|-----------|----------------|-------------|------------------|
| 1 | MARCA | Base (template) | `MarcaActivity` + 3 Dialogs | — | ✅ 9 marcas |
| 2 | MODELO | Sin asignar | Activity propia | MARCA | ✅ 18 modelos |
| 3 | TALLER | Ricardo | Activity propia | — | ✅ 3 talleres |
| 4 | PERSONAL_INTERNO | Eleazar | Activity propia | — | ✅ 3 personas |
| 5 | BODEGA | Javier | Activity propia | DISTRITO | ✅ 6 bodegas |
| 6 | SECCION | Javier | Activity propia | BODEGA | ✅ 18 secciones |
| 7 | IMPORTADOR | Gaby | Activity propia | DISTRITO | ✅ 3 importadores |
| 8 | TELEFONO_IMPORTADOR | Gaby | Dentro de `ImportadorFormDialog` | IMPORTADOR | ✅ 5 teléfonos |
| 9 | IMPORTACION | Gaby | Activity propia | IMPORTADOR | ✅ 4 importaciones |
| 10 | TRANSPORTE | Eleazar | Activity propia | TIPO_TRANSPORTE | ✅ 3 transportes |
| 11 | VEHICULO | Yami | Activity propia | IMPORTACION, MODELO, TIPO_VEHICULO, SECCION | ✅ 5 vehículos |
| 12 | DETALLE_DESPERFECTO | Yami | Activity propia | VEHICULO, TIPO_DESPERFECTO | ✅ 4 detalles |
| 13 | REPARACION | Ricardo | Activity propia | VEHICULO, TALLER | ✅ 3 reparaciones |
| 14 | MOVIMIENTO | Eleazar | Activity propia | VEHICULO, PERSONAL_INTERNO, TRANSPORTE, BODEGA | ✅ 4 movimientos |
| 15 | VENTA | Javier | Activity propia | VEHICULO, IMPORTADOR | ✅ 2 ventas |

### Las 11 tablas de apoyo

| Tabla | Categoría | CRUD en UI | Uso en UI |
|-------|-----------|-----------|-----------|
| OPCIONCRUD | Configuración | ❌ | Interno en SessionManager |
| USUARIO | Configuración | ❌ | Login en LoginActivity |
| ACCESOUSUARIO | Configuración | ❌ | `tieneAcceso()` en SessionManager |
| PAIS | Geográfica | ❌ | Referencia base |
| DEPARTAMENTO | Geográfica | ❌ | Spinner en ImportadorForm, BodegaForm |
| MUNICIPIO | Geográfica | ❌ | Spinner en ImportadorForm, BodegaForm |
| DISTRITO | Geográfica | ❌ | Spinner en ImportadorForm, BodegaForm |
| TIPO_VEHICULO | Catálogo de tipos | ❌ | Spinner en VehiculoForm |
| TIPO_TRANSPORTE | Catálogo de tipos | ❌ | Spinner en TransporteForm |
| TIPO_DESPERFECTO | Catálogo de tipos | ❌ | Spinner en DetalleDesperfectoForm |
| FOTO_DESPERFECTO | Especial | ⚠️ Agregar/Ver/Eliminar | Dentro de DetalleDesperfectoFormDialog |

---

## Sección 6: Sistema de Roles y Permisos

### Usuarios precargados

| Usuario | Clave | Rol |
|---------|-------|-----|
| `admin` | `adm01` | Acceso a todos los 15 módulos del menú |
| `importador` | `imp01` | Importador, Importación, Marca, Tipo Transporte, Tipo Vehículo |
| `personal` | `per01` | Movimiento, Transporte, Marca, Tipo Transporte, Tipo Vehículo |

### IDs de menú en Constants.java (Sección 13)

Las tarjetas del menú están **todas ya definidas en `activity_main.xml`** y la visibilidad está **ya cableada en `configurarTarjetas()`** de `MainActivity`. Lo único que cada integrante debe agregar es el `case` en `abrirModulo()` y registrar su Activity en el Manifest.

| Constante | Valor | Módulo | Tarjeta en layout | Case en `abrirModulo()` |
|-----------|-------|--------|-------------------|------------------------|
| `MENU_IMPORTADOR` | 100 | Importador | ✅ `cardImportadores` | ✅ existe (stub) |
| `MENU_VEHICULO` | 200 | Vehículo | ✅ `cardVehiculos` | ✅ existe (stub) |
| `MENU_IMPORTACION` | 210 | Importación | ✅ `cardImportaciones` | ❌ agregar |
| `MENU_BODEGA` | 300 | Bodega | ✅ `cardBodegas` | ❌ agregar |
| `MENU_SECCION` | 310 | Sección | ✅ `cardSeccion` | ❌ agregar |
| `MENU_MOVIMIENTO` | 400 | Movimiento | ✅ `cardMovimientos` | ✅ existe (stub) |
| `MENU_TRANSPORTE` | 410 | Transporte | ✅ `cardTransporte` | ❌ agregar |
| `MENU_REPARACION` | 500 | Reparación | ✅ `cardReparaciones` | ✅ existe (stub) |
| `MENU_TALLER` | 510 | Taller | ✅ `cardTalleres` | ❌ agregar |
| `MENU_VENTA` | 600 | Venta | ✅ `cardVentas` | ✅ existe (stub) |
| `MENU_DESPERFECTO` | 610 | Detalle Desperfecto | ✅ `cardDesperfectos` | ❌ agregar |
| `MENU_PERSONAL` | 620 | Personal Interno | ✅ `cardPersonal` | ❌ agregar |
| `MENU_MARCA` | 630 | Marca (temporal) | ✅ `cardMarca` | ✅ existe (completo) |
| `MENU_TIPO_TRANSPORTE` | 640 | Tipo Transporte | ❌ no en UI | — sin tarjeta — |
| `MENU_TIPO_VEHICULO` | 650 | Tipo Vehículo | ❌ no en UI | — sin tarjeta — |

### Flujo de verificación de acceso

```
LoginActivity
    └─ SessionManager.login("admin", "adm01")
           └─ Busca USUARIO por nomUsuario
           └─ Valida clave
           └─ Carga List<Integer> con los IDs de OPCIONCRUD del usuario
           └─ Retorna true si OK

MainActivity.configurarTarjetas()
    └─ Para cada tarjeta (cardImportadores, cardVehiculos, …):
           if (session.tieneAcceso(idModulo))
               card.setOnClickListener(…)   ← tarjeta activa y clickeable
           else
               card.setVisibility(GONE)     ← tarjeta oculta

    └─ Por cada fila (row1…row7):
           if ninguna de sus dos tarjetas tiene acceso
               fila.setVisibility(GONE)     ← fila completa oculta

Dentro de cualquier Activity o Dialog:
    if (!SessionManager.getInstance().isLoggedIn()) {
        // redirigir a LoginActivity
    }
```

---

## Sección 7: Flujo Completo de Usuario

```
INSTALACIÓN
  └─ MainActivity.onCreate()
       └─ LlenarBDGpo02.llenarDatosIniciales()
            └─ 16 bloques independientes (cada uno con contar() == 0)
            └─ Precarga todas las 26 tablas con datos demo

LOGIN
  └─ Usuario ingresa credenciales
  └─ SessionManager.login() → valida en USUARIO → carga accesos
  └─ Éxito → MainActivity

MENÚ
  └─ Cuadrícula 2 col × 7 filas de tarjetas en ScrollView
  └─ configurarTarjetas() oculta tarjetas/filas sin acceso
  └─ Tap en tarjeta → abrirModulo(id) → lanza Activity

DENTRO DEL MÓDULO (patrón Marca)
  └─ Activity: ListView + búsqueda en tiempo real
  └─ Tap ítem → OptionsDialog (Ver / Editar / Eliminar)
  └─ Botón "+" → FormDialog vacío → insertar
  └─ Editar → FormDialog precargado → actualizar
  └─ Eliminar → AlertDialog de confirmación → eliminar
  └─ Cada operación → recargar ListView

LOGOUT
  └─ Click en bloque ícono+nombre en el header → AlertDialog
  └─ Confirmar → SessionManager.logout()
  └─ Vuelve a LoginActivity con flags NEW_TASK | CLEAR_TASK
```

---

## Sección 8: GenericDAO — Cómo funciona

`GenericDAO<T>` implementa `IBaseDAO<T>` usando reflexión Java.
No hay SQL escrito a mano en los módulos de negocio.

### Mecanismo de mapeo

```
Campo Java (camelCase)    →    Columna BD (SCREAMING_SNAKE_CASE)
─────────────────────────────────────────────────────────────────
id                        →    ID_[TABLA]  (detectado con PRAGMA table_info)
nombreMarca               →    NOMBRE_MARCA
idImportador              →    ID_IMPORTADOR
fechaImportacion          →    FECHA_IMPORTACION
aptoParaVenta             →    APTO_PARA_VENTA
estadoVehiculo            →    ESTADO_VEHICULO
```

### API completa disponible

```java
GenericDAO<Marca> dao = new GenericDAO<>(context, Marca.class, "marca");

dao.insertar(new Marca(0, "Toyota"));        // id=0 → SQLite asigna auto
dao.obtenerTodos();                          // List<Marca>
dao.obtenerPorId(1);                         // Marca o null
dao.actualizar(marca);                       // void
dao.eliminar(1);                             // void
dao.contar();                                // long
dao.obtenerPor("NOMBRE_MARCA", "Toyota");   // List<Marca>
```

### Requisitos del POJO para que funcione

1. Constructor vacío: `public Marca() {}`
2. Campos en camelCase con getters/setters estándar
3. Pasar `id = 0` al insertar para que SQLite asigne el ID automático
4. `toString()` retorna el campo más descriptivo

---

## Sección 9: LlenarBDGpo02 — Estado Final

16 secciones de carga, cada una con guarda independiente (`contar() == 0`):

| Sección | Tablas | Registros |
|---------|--------|-----------|
| 1 | USUARIO | 3 |
| 2 | OPCIONCRUD | 15 |
| 3 | ACCESOUSUARIO | 25 |
| 4 | PAIS + DEPARTAMENTO + MUNICIPIO + DISTRITO + MARCA + MODELO + TIPO_VEHICULO + TIPO_TRANSPORTE + TIPO_DESPERFECTO + TALLER + PERSONAL_INTERNO | 1+14+14+14+9+18+5+3+4+3+3 = 88 |
| 5 | BODEGA | 6 |
| 6 | SECCION | 18 |
| 7 | IMPORTADOR | 3 |
| 8 | TELEFONO_IMPORTADOR | 5 |
| 9 | IMPORTACION | 4 |
| 10 | TRANSPORTE | 3 |
| 11 | VEHICULO | 5 |
| 12 | MOVIMIENTO | 4 |
| 13 | DETALLE_DESPERFECTO | 4 |
| 14 | FOTO_DESPERFECTO | 4 |
| 15 | REPARACION | 3 |
| 16 | VENTA | 2 |

**Todas las 26 tablas tienen datos al arrancar la app por primera vez.**

Estado de vehículos tras la carga inicial:
- Vehículos 1, 2, 3 → `en bodega` (tienen reparaciones pero el trigger solo actúa en UPDATE)
- Vehículos 4, 5 → `vendido` (trigger `TRG_ESTADO_VENDIDO` actúa en INSERT de VENTA)

---

## Sección 10: Estructura de Carpetas

```
app/src/main/
├── java/com/ues/sistema_pdm1/
│   ├── data/
│   │   ├── database/
│   │   │   ├── DatabaseContract.java
│   │   │   ├── DatabaseHelper.java
│   │   │   └── DatabaseManager.java
│   │   └── dao/
│   │       ├── IBaseDAO.java
│   │       └── GenericDAO.java
│   │
│   ├── models/                          ← 26 POJOs
│   │   ├── AccesoUsuario.java
│   │   ├── Bodega.java
│   │   ├── Departamento.java
│   │   ├── DetalleDesperfecto.java
│   │   ├── Distrito.java
│   │   ├── FotoDesperfecto.java
│   │   ├── Importacion.java
│   │   ├── Importador.java
│   │   ├── Marca.java
│   │   ├── Modelo.java
│   │   ├── Movimiento.java
│   │   ├── Municipio.java
│   │   ├── OpcionCrud.java
│   │   ├── Pais.java
│   │   ├── PersonalInterno.java
│   │   ├── Reparacion.java
│   │   ├── Seccion.java
│   │   ├── Taller.java
│   │   ├── TelefonoImportador.java
│   │   ├── TipoDesperfecto.java
│   │   ├── TipoTransporte.java
│   │   ├── TipoVehiculo.java
│   │   ├── Transporte.java
│   │   ├── Usuario.java
│   │   ├── Vehiculo.java
│   │   └── Venta.java
│   │
│   ├── utils/
│   │   ├── SessionManager.java
│   │   ├── Constants.java
│   │   └── LlenarBDGpo02.java
│   │
│   └── activities/
│       ├── LoginActivity.java
│       ├── MainActivity.java
│       ├── marca/                       ← TEMPLATE COMPLETO
│       │   ├── MarcaActivity.java
│       │   ├── MarcaFormDialog.java
│       │   ├── MarcaOptionsDialog.java
│       │   └── MarcaViewDialog.java
│       ├── importador/                  ← GABY
│       ├── importacion/                 ← GABY
│       ├── vehiculo/                    ← YAMI
│       ├── detalleDesperfecto/          ← YAMI
│       ├── transporte/                  ← ELEAZAR
│       ├── personalInterno/             ← ELEAZAR
│       ├── movimiento/                  ← ELEAZAR
│       ├── taller/                      ← RICARDO
│       ├── reparacion/                  ← RICARDO
│       ├── bodega/                      ← JAVIER
│       ├── seccion/                     ← JAVIER
│       └── venta/                       ← JAVIER
│
└── res/layout/
    ├── activity_login.xml
    ├── activity_main.xml
    ├── activity_marca.xml               ← TEMPLATE
    ├── item_marca.xml
    ├── dialog_marca_form.xml
    ├── dialog_marca_view.xml
    ├── activity_vehiculo.xml            ← stub
    ├── dialog_vehiculo.xml              ← stub
    ├── item_vehiculo.xml
    ├── dialog_options.xml               ← genérico reutilizable
    └── dialog_confirmacion.xml          ← genérico reutilizable
```

---

## Sección 11: Archivos críticos — Nunca modificar

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
    activities/MainActivity.java  ← solo agregar case en abrirModulo(), no tocar el resto
    models/*.java                 ← todos los 26 POJOs
    res/layout/activity_main.xml  ← menú ya completo, no tocar
    res/values/colors.xml         ← paleta cerrada, no agregar ni cambiar colores

⚠️ SOLO AGREGAR (no cambiar lógica existente):
    activities/MainActivity.java
        → Agregar case en switch de abrirModulo() para tu Activity
    AndroidManifest.xml
        → Registrar <activity> de las clases nuevas
    res/values/strings.xml
        → Agregar strings para la UI de cada módulo
```

---

## Sección 12: Checklist Final de Entrega

### Infraestructura base (ya verificada)

```
✅ DatabaseHelper crea 26 tablas y 32 triggers
✅ GenericDAO mapea campos automáticamente
✅ SessionManager valida credenciales y permisos
✅ LlenarBDGpo02 precarga las 26 tablas
✅ LoginActivity autentica y redirige
✅ MainActivity — menú de tarjetas 2×7 con control de acceso por rol
✅ MarcaActivity CRUD completo (template para los demás módulos)
```

### Por cada integrante (Fase 5)

```
COMPILACIÓN Y REGISTRO
□ Compila sin errores
□ Activity registrada en AndroidManifest.xml
□ case agregado en MainActivity.abrirModulo() si faltaba
□ Strings agregados a strings.xml

MENÚ E INTEGRACIÓN
□ La Activity aparece en el menú con el usuario correcto
□ No aparece con usuario sin permiso

CRUD COMPLETO
□ INSERTAR: nuevo registro guarda y aparece en lista
□ CONSULTAR: ListView muestra todos los registros
□ ACTUALIZAR: cambios se reflejan correctamente
□ ELIMINAR: pide confirmación y elimina

CALIDAD
□ Búsqueda filtra en tiempo real (case-insensitive)
□ Spinners se cargan correctamente antes de mostrar
□ Formulario rechaza campos obligatorios vacíos
□ Excepciones de triggers capturadas con mensaje claro

SESIÓN
□ Redirige a LoginActivity si no hay sesión activa
□ No se tocaron archivos prohibidos
```

### Módulo especial: Foto Desperfecto (Yami)

```
□ Botón "Agregar Foto" abre selector cámara/galería
□ Permisos solicitados en runtime (CAMERA, READ_MEDIA_IMAGES)
□ Foto se guarda en getFilesDir()/fotos/
□ Ruta absoluta guardada en BD (campo rutaImagen)
□ ListView muestra lista de fotos del detalle
□ Tap en foto abre previsualización grande
□ Botón Eliminar borra archivo físico + registro BD
□ Fotos temporales descartadas si se cancela el formulario
```

---

## Sección 13: Reglas del Proyecto (Nunca Violar)

| # | Regla | Razón |
|---|-------|-------|
| 1 | Java puro — sin Kotlin | Requisito del curso |
| 2 | Sin Room, Retrofit, Dagger ni otras librerías | Requisito del curso |
| 3 | `DatabaseManager.getInstance(context)` siempre | Es singleton; instanciar directo rompe la BD |
| 4 | Constructor vacío en todos los POJOs | GenericDAO lo usa para instanciar por reflexión |
| 5 | Campo Java = columna BD en camelCase | GenericDAO mapea por nombre; si difieren, no guarda |
| 6 | `id = 0` al insertar | SQLite asigna el ID auto; pasar otro puede duplicar |
| 7 | `try-finally` para abrir/cerrar conexiones en DAO | Evita que la BD quede bloqueada |
| 8 | Validar FKs en código antes de insertar | Sin `PRAGMA foreign_keys = ON`, SQLite no las valida |
| 9 | `SessionManager.tieneAcceso()` en cada Activity | Sin esto, el control de acceso es solo cosmético |
| 10 | `toString()` retorna campo descriptivo en POJOs | Es lo que aparece en los Spinners del formulario |

---

## Sección 14: Documentación Disponible en /docs

| Archivo | Contenido |
|---------|-----------|
| `CLASIFICACION_FINAL_26_TABLAS.md` | Las 26 tablas clasificadas, diagrama de dependencias |
| `LLENARBDGPO2_ESTRUCTURA_ACTUAL.md` | Análisis de la precarga, qué tiene cada tabla |
| `GUIA_FASE_5_PARA_INTEGRANTES.md` | Guía práctica por integrante con campos reales |
| `FOTO_DESPERFECTO_IMPLEMENTACION_ANDROID.md` | Guía técnica de cámara/galería para Yami |
| `ARQUITECTURA_PDM115_RESUMEN_EJECUTIVO.md` | Este documento |
| `01_crear_tablas.txt` | Script SQL de creación de tablas |
| `02_triggers_y_datos.txt` | Script SQL de triggers y datos de referencia |
| `03_corrida_prueba.txt` | Script SQL de datos de prueba por integrante |