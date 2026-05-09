hazme# Contexto del Proyecto - Sistema de Inventario de Vehículos
## PDM115 - Grupo 02 - Universidad de El Salvador

---

## Datos técnicos
- Lenguaje: Java (NO Kotlin bajo ninguna circunstancia)
- Package: com.example.sistema_pdm1
- Mínimo SDK: API 24 (Android 7.0)
- Base de datos: SQLite puro, sin Room, sin Retrofit, sin librerías externas
- Arquitectura: DAO Pattern + GenericDAO por reflexión Java

---

## Estructura de carpetas

```
app/src/main/java/com/example/sistema_pdm1/
├── data/
│   ├── database/
│   │   ├── DatabaseManager.java
│   │   ├── DatabaseHelper.java
│   │   └── DatabaseContract.java
│   └── dao/
│       ├── IBaseDAO.java
│       ├── GenericDAO.java
│       ├── ImportadorDAO.java
│       ├── ImportacionDAO.java
│       ├── VehiculoDAO.java
│       ├── DetalleDesperfectoDAO.java
│       ├── MovimientoDAO.java
│       ├── ReparacionDAO.java
│       ├── SeccionDAO.java
│       └── VentaDAO.java
├── models/
│   ├── Pais.java
│   ├── Departamento.java
│   ├── Municipio.java
│   ├── Distrito.java
│   ├── Marca.java
│   ├── Modelo.java
│   ├── TipoVehiculo.java
│   ├── TipoTransporte.java
│   ├── TipoDesperfecto.java
│   ├── Taller.java
│   ├── PersonalInterno.java
│   ├── Usuario.java
│   ├── OpcionCrud.java
│   ├── AccesoUsuario.java
│   ├── Bodega.java
│   ├── Seccion.java
│   ├── Transporte.java
│   ├── Importador.java
│   ├── TelefonoImportador.java
│   ├── Importacion.java
│   ├── Vehiculo.java
│   ├── DetalleDesperfecto.java
│   ├── FotoDesperfecto.java
│   ├── Movimiento.java
│   ├── Reparacion.java
│   └── Venta.java
├── utils/
│   ├── SessionManager.java
│   └── Constants.java
└── activities/
    ├── LoginActivity.java
    ├── MainActivity.java
    ├── importador/
    ├── vehiculo/
    ├── movimiento/
    ├── reparacion/
    ├── venta/
    ├── importacion/
    └── catalogos/
```

---

## Base de datos

- 26 tablas definidas en docs/sql/01_crear_tablas.txt
- 7 triggers en docs/sql/02_triggers_y_datos.txt
- Corrida de prueba en docs/sql/03_corrida_prueba.txt

### Tablas del sistema
1. PAIS
2. DEPARTAMENTO → PAIS
3. MUNICIPIO → DEPARTAMENTO
4. DISTRITO → MUNICIPIO
5. MARCA
6. MODELO → MARCA
7. TIPO_VEHICULO
8. TIPO_TRANSPORTE
9. TIPO_DESPERFECTO
10. TALLER
11. PERSONAL_INTERNO
12. USUARIO
13. OPCIONCRUD
14. ACCESOUSUARIO → USUARIO, OPCIONCRUD
15. BODEGA → DISTRITO
16. SECCION → BODEGA
17. TRANSPORTE → TIPO_TRANSPORTE
18. IMPORTADOR → DISTRITO
19. TELEFONO_IMPORTADOR → IMPORTADOR
20. IMPORTACION → IMPORTADOR
21. VEHICULO → IMPORTACION, MODELO, TIPO_VEHICULO, SECCION
22. DETALLE_DESPERFECTO → VEHICULO, TIPO_DESPERFECTO
23. FOTO_DESPERFECTO → DETALLE_DESPERFECTO
24. MOVIMIENTO → VEHICULO, PERSONAL_INTERNO, TRANSPORTE, BODEGA
25. REPARACION → VEHICULO, TALLER
26. VENTA → VEHICULO, IMPORTADOR

### Triggers implementados
- TRG_ANIO_VEHICULO: bloquea inserción de vehículos año <= 2020
- TRG_CAPACIDAD_SECCION: bloquea inserción si sección llena
- TRG_CAPACIDAD_TRANSPORTE: bloquea si transporte llega a su límite diario
- TRG_SECCION_INCREMENTAR: actualiza capacidad_actual al asignar vehículo
- TRG_SECCION_MOVER: ajusta capacidades al cambiar vehículo de sección
- TRG_ESTADO_VENDIDO: cambia estado vehículo a 'vendido' al registrar venta
- TRG_ESTADO_REPARACION: cambia estado según resultado de reparación

---

## Usuarios y roles

| Usuario | Clave | Rol |
|---------|-------|-----|
| admin | adm01 | Acceso total |
| importador | imp01 | Solo su módulo + consultas |
| personal | per01 | Movimientos y consultas |

El control de acceso se maneja con SessionManager + tabla ACCESOUSUARIO.
Los menús se construyen dinámicamente según las opciones que tiene el usuario.

---

## Reglas críticas — NUNCA violar

1. **Java puro**, nunca Kotlin
2. **Sin Room**, sin Retrofit, sin Dagger, sin ninguna dependencia externa
3. Todos los layouts van en **res/layout/** con nombre `activity_[tabla]_[operacion].xml`
4. Los nombres de campos Java deben ser **IDÉNTICOS** a las columnas en BD (para GenericDAO por reflexión)
5. Todo modelo debe tener **constructor vacío** obligatorio
6. **toString()** de cada modelo retorna el campo más descriptivo (se usa en Spinners)
7. **Sin eliminación en cascada** — validación manual en cada DAO antes de eliminar
8. **Sin PRAGMA foreign_keys** — integridad validada programáticamente en DAOs
9. **DatabaseManager es singleton** — siempre usar getInstance(context)
10. Cada método DAO **abre y cierra su propia conexión** con try-finally

---

## DAOs específicos (lógica de negocio especial)

### ImportadorDAO
- Validar NUI único antes de insertar
- Verificar que no tiene importaciones antes de eliminar
- Búsqueda por NUI

### ImportacionDAO
- Verificar que el importador existe antes de insertar
- Bloquear eliminación si tiene vehículos asociados

### VehiculoDAO
- Validar VIN único
- Búsquedas por estado, modelo, año
- Bloquear eliminación siempre (núcleo del inventario)
- Método obtenerListosParaVenta()

### DetalleDesperfectoDAO
- Verificar vehículo existe antes de insertar
- Verificar tipo desperfecto existe
- Eliminar fotos antes de eliminar detalle
- Obtener todos los detalles de un vehículo

### MovimientoDAO
- Validar que transporte no supera capacidad diaria
- Verificar vehículo, personal y transporte existen
- Obtener movimientos por bodega, por fecha

### ReparacionDAO
- Validar que taller tiene AUTORIZADO = 1
- Validar que vehículo no está ya vendido
- Obtener reparaciones activas (sin fecha_fin)

### SeccionDAO
- Verificar espacio disponible antes de asignar
- Método obtenerConEspacio() para Spinners de selección
- Bloquear eliminación si tiene vehículos

### VentaDAO
- Validar que ESTADO_VEHICULO = 'listo' antes de insertar
- Bloquear eliminación siempre
- Obtener ventas por importador

---

## Tablas con GenericDAO directo

Sin lógica especial, CRUD básico suficiente:
Pais, Departamento, Municipio, Distrito, Marca, Modelo, TipoVehiculo,
TipoTransporte, TipoDesperfecto, Taller, PersonalInterno, Transporte,
TelefonoImportador, FotoDesperfecto, Usuario, OpcionCrud, AccesoUsuario

---

## SessionManager

Singleton que maneja la sesión activa:
- login(usuario, clave, context): boolean
  → Consulta tabla USUARIO
  → Carga todas las ID_OPCION de ACCESOUSUARIO para ese usuario
  → Retorna true si credenciales correctas
- logout(): limpia sesión
- tieneAcceso(idOpcion): boolean → revisa lista cargada
- isLoggedIn(): boolean
- getNombreUsuario(): String

---

## Estructura de OPCIONCRUD

```
Módulo 1 - Importador
  100 = Menu Importador       (num_crud=0)
  101 = Agregar Importador    (num_crud=1)
  102 = Modificar Importador  (num_crud=2)
  103 = Eliminar Importador   (num_crud=3)
  104 = Consultar Importador  (num_crud=4)

Módulo 2 - Vehículo
  200 = Menu Vehiculo         (num_crud=0)
  201 = Agregar Vehiculo      (num_crud=1)
  202 = Modificar Vehiculo    (num_crud=2)
  203 = Eliminar Vehiculo     (num_crud=3)
  204 = Consultar Vehiculo    (num_crud=4)

Módulo 3 - Movimiento
  300..304

Módulo 4 - Reparacion
  400..404

Módulo 5 - Venta
  500..504

Módulo 6 - Catalogos
  600..604
```

---

## Plan de trabajo por fases

### FASE 1 — Base de datos (ejecutar en orden, verificar compilación entre cada uno)
- Paso 1: DatabaseContract.java
- Paso 2: DatabaseHelper.java
- Paso 3: DatabaseManager.java
- Paso 4: IBaseDAO.java
- Paso 5: GenericDAO.java

### FASE 2 — Modelos y utilidades
- Paso 6: Los 26 modelos POJO (todos en una sola generación)
- Paso 7: SessionManager.java
- Paso 8: Constants.java

### FASE 3 — Login y menú principal
- Paso 9: LoginActivity.java + activity_login.xml
- Paso 10: LlenarBDGpo02.java (datos iniciales en Java)
- Paso 11: MainActivity.java + activity_main.xml

### FASE 4 — Template CRUD completo
- Paso 12: Marca CRUD (template que el equipo replica)
    * MarcaInsertarActivity + layout
    * MarcaConsultarActivity + layout
    * MarcaActualizarActivity + layout
    * MarcaEliminarActivity + layout

### FASE 5 — DAOs específicos y CRUDs principales (equipo en paralelo)
- Gaby: ImportadorDAO, ImportacionDAO + sus Activities
- Yami: VehiculoDAO, DetalleDesperfectoDAO + sus Activities
- Eleazar: MovimientoDAO + sus Activities
- Ricardo: ReparacionDAO + sus Activities
- Javier: VentaDAO, SeccionDAO + sus Activities

---

## Notas para la defensa

- Los 15 CRUDs se demuestran con el usuario admin
- El usuario importador demuestra restricción de acceso
- El usuario personal demuestra restricción de acceso
- Los triggers se demuestran intentando insertar vehículo año 2019
- La integridad referencial se demuestra intentando eliminar una marca con modelos