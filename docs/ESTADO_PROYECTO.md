# Estado del Proyecto — Sistema de Inventario de Vehículos
## PDM115 · Grupo 02 · Universidad de El Salvador
**Fecha:** 2026-05-08

---

## 1. Arquitectura general

El proyecto sigue una arquitectura en capas separadas por responsabilidad:

```
UI (Activities)
     │
     ▼
SessionManager / Utils
     │
     ▼
DAOs (GenericDAO / DAOs específicos)
     │
     ▼
DatabaseManager (singleton)
     │
     ▼
DatabaseHelper (SQLiteOpenHelper)
     │
     ▼
SQLite — 26 tablas + 7 triggers
```

**Reglas que nunca se violan:**
- Java puro — sin Kotlin, sin Room, sin Retrofit, sin dependencias externas
- SQLite directo con patrón DAO + GenericDAO por reflexión
- `DatabaseManager` es singleton — siempre `getInstance(context)`
- Cada método DAO abre y cierra su propia conexión con `try-finally`
- Sin `PRAGMA foreign_keys` — integridad validada programáticamente en cada DAO

---

## 2. Estructura de carpetas

```
app/src/main/java/com/example/sistema_pdm1/
├── data/
│   ├── database/
│   │   ├── DatabaseContract.java   ← nombres de tablas y columnas (constantes)
│   │   ├── DatabaseHelper.java     ← crea tablas y triggers al instalar
│   │   └── DatabaseManager.java    ← singleton que abre/cierra la BD
│   └── dao/
│       ├── IBaseDAO.java           ← interfaz con insertar/actualizar/eliminar/obtener
│       └── GenericDAO.java         ← implementación genérica por reflexión Java
├── models/
│   └── [26 POJOs]                  ← un archivo por tabla, campos = columnas BD
├── utils/
│   ├── SessionManager.java         ← maneja sesión activa y control de accesos
│   ├── Constants.java              ← IDs de opciones, estados, mensajes, formatos
│   └── LlenarBDGpo02.java          ← inserta datos iniciales (solo si tablas vacías)
└── activities/
    ├── LoginActivity.java          ← pantalla de login
    ├── MainActivity.java           ← menú principal con botones dinámicos por rol
    ├── importador/                 ← módulo de Gaby
    ├── vehiculo/                   ← módulo de Yami
    ├── movimiento/                 ← módulo de Eleazar
    ├── reparacion/                 ← módulo de Ricardo
    ├── venta/                      ← módulo de Javier
    └── catalogos/                  ← catálogos compartidos
```

---

## 3. Base de datos

**Archivo:** `inventario_pdm1.db` (se crea automáticamente al instalar)

### 26 tablas en orden de dependencias

| # | Tabla | Depende de |
|---|-------|-----------|
| 1 | OPCIONCRUD | — |
| 2 | USUARIO | — |
| 3 | ACCESOUSUARIO | USUARIO, OPCIONCRUD |
| 4 | PAIS | — |
| 5 | DEPARTAMENTO | PAIS |
| 6 | MUNICIPIO | DEPARTAMENTO |
| 7 | DISTRITO | MUNICIPIO |
| 8 | BODEGA | DISTRITO |
| 9 | IMPORTADOR | DISTRITO |
| 10 | IMPORTACION | IMPORTADOR |
| 11 | MARCA | — |
| 12 | MODELO | MARCA |
| 13 | TIPO_VEHICULO | — |
| 14 | SECCION | BODEGA |
| 15 | VEHICULO | IMPORTACION, MODELO, TIPO_VEHICULO, SECCION |
| 16 | TIPO_DESPERFECTO | — |
| 17 | DETALLE_DESPERFECTO | VEHICULO, TIPO_DESPERFECTO |
| 18 | FOTO_DESPERFECTO | DETALLE_DESPERFECTO |
| 19 | TIPO_TRANSPORTE | — |
| 20 | TRANSPORTE | TIPO_TRANSPORTE |
| 21 | PERSONAL_INTERNO | — |
| 22 | MOVIMIENTO | VEHICULO, PERSONAL_INTERNO, TRANSPORTE, BODEGA |
| 23 | TALLER | — |
| 24 | REPARACION | VEHICULO, TALLER |
| 25 | TELEFONO_IMPORTADOR | IMPORTADOR |
| 26 | VENTA | VEHICULO, IMPORTADOR |

### 7 triggers

| Trigger | Tabla | Acción |
|---------|-------|--------|
| TRG_ANIO_VEHICULO | VEHICULO | Bloquea inserción si AÑO ≤ 2020 |
| TRG_CAPACIDAD_SECCION | VEHICULO | Bloquea si sección llena |
| TRG_CAPACIDAD_TRANSPORTE | MOVIMIENTO | Bloquea si transporte llegó al límite diario |
| TRG_SECCION_INCREMENTAR | VEHICULO | Suma +1 a CAPACIDAD_ACTUAL al insertar vehículo |
| TRG_SECCION_MOVER | VEHICULO | Ajusta capacidades al cambiar vehículo de sección |
| TRG_ESTADO_VENDIDO | VENTA | Cambia estado vehículo a `'vendido'` |
| TRG_ESTADO_REPARACION | REPARACION | Cambia estado a `'listo'` o `'en reparacion'` |

---

## 4. Capa de datos — cómo funciona GenericDAO

`GenericDAO<T>` opera por reflexión Java: lee los campos del modelo y los convierte a nombres de columna usando `camelCase → SCREAMING_SNAKE_CASE`.

**Ejemplo:** campo `nomUsuario` → columna `NOM_USUARIO`

### Uso básico

```java
// Crear un DAO
GenericDAO<Marca> marcaDAO = new GenericDAO<>(context, Marca.class, "marca");

// Insertar (id=0 → SQLite asigna el ID automáticamente)
marcaDAO.insertar(new Marca(0, "Toyota"));

// Obtener todos
List<Marca> marcas = marcaDAO.obtenerTodos();

// Obtener por ID
Marca m = marcaDAO.obtenerPorId(1);

// Actualizar
m.setNombreMarca("Toyota Actualizado");
marcaDAO.actualizar(m);

// Eliminar
marcaDAO.eliminar(1);

// Contar registros
long total = marcaDAO.contar();

// Buscar por columna
List<Marca> resultado = marcaDAO.obtenerPor("NOMBRE_MARCA", "Honda");
```

### Reglas del modelo para que GenericDAO funcione

1. Constructor vacío obligatorio: `public Marca() {}`
2. Campos Java con el mismo nombre (camelCase) que las columnas BD
3. Para IDs enteros auto-incremento: pasar `0` al insertar
4. `toString()` retorna el campo más descriptivo (se usa en Spinners)

### Detección automática de PK

`GenericDAO` detecta automáticamente el nombre real de la columna PK usando
`PRAGMA table_info(tabla)` en el constructor. Esto resuelve que cada tabla
tiene su propio nombre de PK (`ID_USUARIO`, `ID_MARCA`, `ID_OPCION`, etc.)
en lugar de un genérico `ID`.

---

## 5. Flujo de login y sistema de roles

### Flujo completo

```
1. LoginActivity.onCreate()
   ├─ SessionManager.inicializar(context)
   ├─ LlenarBDGpo02.llenarDatosIniciales()   ← inserta solo si tabla vacía
   └─ Si ya hay sesión activa → saltar a MainActivity

2. Usuario ingresa credenciales → btnLogin
   └─ SessionManager.login(usuario, clave)
         ├─ Busca en tabla USUARIO (obtenerTodos + comparación)
         ├─ Si OK → carga List<Integer> con todas las ID_OPCION del usuario
         └─ Retorna true/false

3. MainActivity.configurarMenu()
   └─ Para cada botón: session.tieneAcceso(Constants.OPCION_MENU_X)
         ├─ true  → botón VISIBLE, listener configurado
         └─ false → botón GONE (no aparece)
```

### Usuarios y permisos

| Usuario | Clave | Botones visibles |
|---------|-------|-----------------|
| `admin` | `adm01` | Importador, Vehículo, Movimiento, Reparación, Venta, Catálogos |
| `importador` | `imp01` | Importador, Catálogos |
| `personal` | `per01` | Movimiento, Catálogos |

### Control de acceso por operación (en cada Activity)

Dentro de cada módulo, usar `Constants` para verificar la operación específica:

```java
SessionManager session = SessionManager.getInstance();

if (!session.tieneAcceso(Constants.OPCION_INSERTAR_IMPORTADOR)) {
    Toast.makeText(this, Constants.MSG_SIN_PERMISO, Toast.LENGTH_SHORT).show();
    finish();
    return;
}
```

### SessionManager — métodos disponibles

```java
SessionManager.getInstance().inicializar(context);   // llamar al inicio
SessionManager.getInstance().login(usuario, clave);  // retorna boolean
SessionManager.getInstance().logout();               // limpia sesión
SessionManager.getInstance().isLoggedIn();           // boolean
SessionManager.getInstance().getNombreUsuario();     // String
SessionManager.getInstance().tieneAcceso(int id);   // boolean
SessionManager.getInstance().getUsuarioActual();     // Usuario
```

---

## 6. Datos iniciales — LlenarBDGpo02

Se llama en `LoginActivity.onCreate()` y `MainActivity.onCreate()`.
**Solo inserta si la tabla está vacía** (`contar() == 0`), por lo que es seguro
llamarla múltiples veces sin duplicar datos.

### Datos cargados automáticamente

- **Usuarios:** admin, importador, personal
- **30 opciones CRUD** (módulos 100–604)
- **Matriz de accesos** (qué usuario tiene qué opción)
- **Geografía:** 1 país, 14 departamentos, 14 municipios, 14 distritos
- **Catálogos:** 9 marcas, 18 modelos, 5 tipos vehículo, 3 tipos transporte, 4 tipos desperfecto
- **Operativos:** 3 talleres, 3 personal interno

---

## 7. Estado por fases

| Fase | Descripción | Estado |
|------|-------------|--------|
| **FASE 1** | Base de datos (DatabaseContract, Helper, Manager, IBaseDAO, GenericDAO) | ✅ Completa |
| **FASE 2** | 26 modelos POJO + SessionManager + Constants + LlenarBDGpo02 | ✅ Completa |
| **FASE 3** | LoginActivity + MainActivity con menú dinámico por roles | ✅ Completa |
| **FASE 4** | Template CRUD completo de Marca (4 Activities + 4 layouts) | ⬜ Pendiente |
| **FASE 5** | DAOs específicos + CRUDs por módulo (equipo en paralelo) | ⬜ Pendiente |

---

## 8. Asignación de módulos — Fase 5

El sistema de login, roles y menú principal está terminado.
**Nadie debe modificar:** `SessionManager`, `Constants`, `LlenarBDGpo02`,
`DatabaseHelper`, `DatabaseManager`, `GenericDAO`, `LoginActivity`, `MainActivity`.

Cada integrante trabaja solo en su carpeta y su DAO:

| Integrante | Carpeta | DAOs a crear | Tabla principal |
|-----------|---------|-------------|-----------------|
| **Gaby** | `activities/importador/` | `ImportadorDAO`, `ImportacionDAO` | IMPORTADOR, IMPORTACION |
| **Yami** | `activities/vehiculo/` | `VehiculoDAO`, `DetalleDesperfectoDAO` | VEHICULO, DETALLE_DESPERFECTO |
| **Eleazar** | `activities/movimiento/` | `MovimientoDAO` | MOVIMIENTO |
| **Ricardo** | `activities/reparacion/` | `ReparacionDAO` | REPARACION |
| **Javier** | `activities/venta/` | `VentaDAO`, `SeccionDAO` | VENTA, SECCION |

Los catálogos (Marca, Modelo, TipoVehiculo, etc.) usan `GenericDAO` directo,
sin DAO específico.

### Patrón de 4 Activities por módulo (basado en template Marca de Fase 4)

```
[Modulo]InsertarActivity   → formulario + GenericDAO/DAO.insertar()
[Modulo]ConsultarActivity  → lista + GenericDAO/DAO.obtenerTodos()
[Modulo]ActualizarActivity → buscar por ID + formulario + DAO.actualizar()
[Modulo]EliminarActivity   → buscar por ID + confirmación + DAO.eliminar()
```

---

## 9. Constantes disponibles en Constants.java

| Categoría | Ejemplos |
|-----------|---------|
| IDs de opciones | `OPCION_MENU_IMPORTADOR = 100`, `OPCION_INSERTAR_IMPORTADOR = 101` … |
| Estados vehículo | `ESTADO_VEHICULO_ALMACENADO`, `LISTO`, `EN_REPARACION`, `VENDIDO` |
| Estados taller | `TALLER_AUTORIZADO = 1`, `TALLER_NO_AUTORIZADO = 0` |
| Formatos fecha | `FORMATO_FECHA = "yyyy-MM-dd"` |
| Límites | `ANIO_MINIMO_VEHICULO = 2021`, `LONGITUD_VIN = 17`, `LONGITUD_NUI = 14` |
| Mensajes | `MSG_SIN_PERMISO`, `MSG_OPERACION_EXITOSA`, `MSG_CAMPO_REQUERIDO` … |
| Intent extras | `EXTRA_ID`, `EXTRA_OBJETO`, `EXTRA_MODULO`, `EXTRA_OPERACION` |

---

## 10. Corrección aplicada en esta sesión

**Problema detectado:** `GenericDAO.convertToContentValues()` mapeaba el campo
Java `id` a la columna `ID`, pero cada tabla tiene su propio nombre de PK
(`ID_USUARIO`, `ID_OPCION`, `ID_MARCA`, etc.). Esto causaba que todos los
inserts con PK de tipo String (`Usuario`, `OpcionCrud`) fallaran silenciosamente,
dejando la base de datos vacía y haciendo que el login siempre rechazara credenciales.

**Corrección:** Se agregó el método `detectPkColumn()` al constructor de
`GenericDAO`. Usa `PRAGMA table_info(tabla)` para leer el nombre real de la
primera columna (la PK) y lo utiliza en `INSERT`, `UPDATE`, `DELETE` y `SELECT BY ID`.

**Acción requerida:** Todos los integrantes deben hacer `git pull` antes de
continuar con sus módulos.