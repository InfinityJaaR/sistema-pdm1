# Clasificación Final de las 26 Tablas SQLite
## PDM115 · Sistema de Inventario de Vehículos · Grupo 02 · UES
**Fecha:** Mayo 2026

---

## Sección 1: Resumen Ejecutivo

El sistema cuenta con **26 tablas SQLite** divididas en dos categorías:

| Categoría | Cantidad | Propósito |
|-----------|----------|-----------|
| **CRUD completo en UI** | 15 | El usuario puede Insertar, Consultar, Actualizar y Eliminar registros desde la app |
| **Apoyo (solo lectura en UI)** | 11 | Se precargan con datos fijos y alimentan Spinners o controlan accesos; el usuario no las gestiona directamente |
| **Total** | **26** | |

**Regla de oro:**
- Las **15 CRUD** tienen su propia pantalla (Activity + Dialogs) o viven dentro del formulario de otra.
- Las **11 de apoyo** solo aparecen en Spinners o están ocultas para el usuario.
- **Todas las 26** se precargan en `LlenarBDGpo02.java` con datos demo antes del primer uso.

---

## Sección 2: Las 15 CRUDs Completas

| # | Tabla | Integrante | Ubicación del CRUD | Tipo | Observaciones |
|---|-------|------------|--------------------|------|---------------|
| 1 | **MARCA** | Template (base) | `MarcaActivity` + 3 Dialogs | Principal | Ya implementada. Sirve de template para todos los demás módulos |
| 2 | **MODELO** | Ricardo | `ModeloActivity` + 3 Dialogs | Dependiente de MARCA | Spinner de Marca en el formulario |
| 3 | **IMPORTADOR** | Gaby | `ImportadorActivity` + 3 Dialogs | Principal | Incluye gestión de teléfonos dentro del FormDialog |
| 4 | **TELEFONO_IMPORTADOR** | Gaby | Dentro de `ImportadorFormDialog` | Dependiente de IMPORTADOR | No tiene Activity propia; se gestiona como lista editable dentro del formulario de Importador |
| 5 | **IMPORTACION** | Gaby | `ImportacionActivity` + 3 Dialogs | Dependiente de IMPORTADOR | Spinner de Importador en el formulario |
| 6 | **BODEGA** | Javier | `BodegaActivity` + 3 Dialogs | Dependiente de DISTRITO | Spinner de Distrito en el formulario |
| 7 | **SECCION** | Javier | `SeccionActivity` + 3 Dialogs | Dependiente de BODEGA | Spinner de Bodega; muestra capacidad actual vs máxima |
| 8 | **VEHICULO** | Yami | `VehiculoActivity` + 3 Dialogs | Principal (tabla central) | Spinners: Importacion, Modelo, TipoVehiculo, Seccion. Trigger valida AÑO > 2020 |
| 9 | **DETALLE_DESPERFECTO** | Yami | `DetalleDesperfectoActivity` + 3 Dialogs | Dependiente de VEHICULO | Incluye funcionalidad de fotos (FOTO_DESPERFECTO) dentro de la misma Activity |
| 10 | **TRANSPORTE** | Eleazar | `TransporteActivity` + 3 Dialogs | Dependiente de TIPO_TRANSPORTE | Spinner de TipoTransporte en el formulario |
| 11 | **PERSONAL_INTERNO** | Eleazar | `PersonalInternoActivity` + 3 Dialogs | Principal | Sin FKs externas; formulario simple |
| 12 | **MOVIMIENTO** | Eleazar | `MovimientoActivity` + 3 Dialogs | Dependiente de varios | Spinners: Vehiculo, PersonalInterno, Transporte, Bodega. Trigger valida capacidad diaria |
| 13 | **TALLER** | Ricardo | `TallerActivity` + 3 Dialogs | Principal | Campo autorizado (1/0); sin FKs externas |
| 14 | **REPARACION** | Ricardo | `ReparacionActivity` + 3 Dialogs | Dependiente de VEHICULO y TALLER | Spinners: Vehiculo, Taller. Trigger cambia estado del vehículo |
| 15 | **VENTA** | Javier | `VentaActivity` + 3 Dialogs | Dependiente de VEHICULO e IMPORTADOR | Spinners: Vehiculo, Importador. Trigger cambia estado a 'vendido' |

### Patrón estándar de cada CRUD (replicar desde MarcaActivity)

```
[Tabla]Activity        → lista de registros + búsqueda/filtro + botón agregar
[Tabla]OptionsDialog   → tap sobre ítem: opciones Ver / Editar / Eliminar
[Tabla]FormDialog      → formulario para Insertar y Actualizar (mismo dialog, doble uso)
[Tabla]ViewDialog      → detalle de solo lectura
```

---

## Sección 3: Las 11 Tablas de Apoyo (Sin CRUD en UI)

### Categoría A — Configuración (NUNCA se modifican desde la app)

| Tabla | Descripción | Cómo se usa |
|-------|-------------|-------------|
| OPCIONCRUD | Define los 30 permisos disponibles del sistema | Solo lectura interna en `SessionManager` |
| USUARIO | Usuarios del sistema (admin, importador, personal) | Login en `LoginActivity` |
| ACCESOUSUARIO | Matriz que vincula usuarios con permisos | Consultada por `SessionManager.tieneAcceso()` |

> Estas tres tablas se precargan en `LlenarBDGpo02` y **no deben modificarse**.
> Cambiarlas rompe el sistema de roles.

---

### Categoría B — Geográficas (Precargadas, solo SELECT en Spinners)

| Tabla | Depende de | Usada en Spinner de |
|-------|-----------|---------------------|
| PAIS | — | — (dato base, no aparece en UI) |
| DEPARTAMENTO | PAIS | Spinners de dirección (Importador, etc.) |
| MUNICIPIO | DEPARTAMENTO | Spinners de dirección |
| DISTRITO | MUNICIPIO | Spinner en BodegaFormDialog e ImportadorFormDialog |

> El usuario **no crea ni edita** departamentos, municipios ni distritos.
> Solo los selecciona en Spinners en cascada cuando registra un Importador o una Bodega.

---

### Categoría C — Catálogos de Tipos (Precargadas, solo SELECT en Spinners)

| Tabla | Usada en Spinner de |
|-------|---------------------|
| TIPO_VEHICULO | VehiculoFormDialog |
| TIPO_TRANSPORTE | TransporteFormDialog |
| TIPO_DESPERFECTO | DetalleDesperfectoFormDialog |

> **No tienen Activity propia ni entrada en el menú.**
> Se cargan con `GenericDAO.obtenerTodos()` directamente en los Spinners
> de los formularios que las necesitan.

---

### Categoría D — Especial

| Tabla | Categoría | Descripción |
|-------|-----------|-------------|
| FOTO_DESPERFECTO | Especial | Funcionalidad limitada dentro de `DetalleDesperfectoActivity`. Permite agregar, ver y eliminar fotos. **Sin editar.** La foto se guarda en Storage y la ruta se registra en esta tabla. |

---

## Sección 4: Diagrama Visual de Dependencias

```
════════════════════════════════════════════════════════════════════
        TABLAS DE APOYO (alimentan Spinners / controlan accesos)
════════════════════════════════════════════════════════════════════

  [CONFIGURACIÓN]              [GEOGRÁFICAS]           [TIPOS]
  OPCIONCRUD ──┐              PAIS                    TIPO_VEHICULO
  USUARIO ─────┤              └─ DEPARTAMENTO         TIPO_TRANSPORTE
  ACCESOUSUARIO┘                 └─ MUNICIPIO         TIPO_DESPERFECTO
       │                            └─ DISTRITO
       │ tieneAcceso()                   │ Spinner
       ▼                                 ▼
  SessionManager            ImportadorForm / BodegaForm

════════════════════════════════════════════════════════════════════
              15 CRUDs COMPLETAS (corazón del sistema)
════════════════════════════════════════════════════════════════════

  MARCA ──────────────────────────────────────────────► (template)
    └─ MODELO ──────────────────────────────────────────┐
                                                        │
  IMPORTADOR ──────────────────────────────────────────►│
    ├─ TELEFONO_IMPORTADOR (dentro de ImportadorForm)   │
    └─ IMPORTACION ────────────────────────────────────►│
                                                        │ Spinners
  BODEGA ─────────────────────────────────────────────►│
    └─ SECCION ─────────────────────────────────────────┤
                                                        │
  TALLER ──────────────────────────────────────────────►│
                                                        ▼
  PERSONAL_INTERNO ──────────────────────────────► VEHICULO (tabla central)
  TRANSPORTE ───────────────────────────────────────────│
                                                        │
                                          ┌─────────────┼──────────────┐
                                          ▼             ▼              ▼
                                      MOVIMIENTO   REPARACION       VENTA
                                                        │
                                                        ▼
                                             DETALLE_DESPERFECTO
                                                        │
                                                        ▼
                                               FOTO_DESPERFECTO
                                              (funcionalidad limitada)

════════════════════════════════════════════════════════════════════
  Spinners de tipos:
  TIPO_VEHICULO ──────────────────────► VehiculoForm
  TIPO_TRANSPORTE ────────────────────► TransporteForm
  TIPO_DESPERFECTO ───────────────────► DetalleDesperfectoForm
════════════════════════════════════════════════════════════════════
```

### Responsables por zona

```
  ┌─────────────────────────────────────────────────────────┐
  │  GABY:  IMPORTADOR ─── TELEFONO_IMPORTADOR              │
  │              └──────── IMPORTACION                      │
  ├─────────────────────────────────────────────────────────┤
  │  YAMI:  VEHICULO ────── DETALLE_DESPERFECTO             │
  │                              └──── FOTO_DESPERFECTO     │
  ├─────────────────────────────────────────────────────────┤
  │  ELEAZAR: TRANSPORTE ── PERSONAL_INTERNO ── MOVIMIENTO  │
  ├─────────────────────────────────────────────────────────┤
  │  RICARDO: MODELO ────── TALLER ──────────── REPARACION  │
  ├─────────────────────────────────────────────────────────┤
  │  JAVIER:  BODEGA ────── SECCION ──────────── VENTA      │
  ├─────────────────────────────────────────────────────────┤
  │  BASE:    MARCA (template ya implementado)              │
  └─────────────────────────────────────────────────────────┘
```

---

## Sección 5: Regla Crítica

```
══════════════════════════════════════════════════════════════
  TODAS LAS 26 TABLAS SE PRECARGAN EN LlenarBDGpo02.java
══════════════════════════════════════════════════════════════

  Las 15 CRUDs     → datos demo iniciales (el usuario los puede modificar)
  Las 11 de apoyo  → datos de catálogo/configuración (fijos para siempre)

  En UI:
  ✅  Las 15 → CRUD completo (Insertar / Consultar / Actualizar / Eliminar)
  🔒  Las 11 → SELECT only (solo aparecen en Spinners o controlan accesos)
  ⚠️  FOTO_DESPERFECTO → caso especial: agregar + ver + eliminar (sin editar)

══════════════════════════════════════════════════════════════
  NO modificar LlenarBDGpo02.java.
  Los datos de catálogo/configuración deben estar presentes
  desde la primera corrida (requisito del PDF del proyecto).
══════════════════════════════════════════════════════════════
```

---

## Referencia rápida: ¿qué carga cada Spinner?

| Spinner | Tabla fuente | DAO a usar | En qué formulario |
|---------|-------------|-----------|-------------------|
| Marca | MARCA | `GenericDAO<Marca>` | ModeloFormDialog |
| Tipo de Vehículo | TIPO_VEHICULO | `GenericDAO<TipoVehiculo>` | VehiculoFormDialog |
| Modelo | MODELO | `GenericDAO<Modelo>` | VehiculoFormDialog |
| Importación | IMPORTACION | `GenericDAO<Importacion>` | VehiculoFormDialog |
| Sección | SECCION | `GenericDAO<Seccion>` | VehiculoFormDialog |
| Tipo de Transporte | TIPO_TRANSPORTE | `GenericDAO<TipoTransporte>` | TransporteFormDialog |
| Distrito | DISTRITO | `GenericDAO<Distrito>` | ImportadorFormDialog, BodegaFormDialog |
| Bodega | BODEGA | `GenericDAO<Bodega>` | SeccionFormDialog, MovimientoFormDialog |
| Vehículo | VEHICULO | `GenericDAO<Vehiculo>` | MovimientoFormDialog, ReparacionFormDialog, VentaFormDialog |
| Personal Interno | PERSONAL_INTERNO | `GenericDAO<PersonalInterno>` | MovimientoFormDialog |
| Transporte | TRANSPORTE | `GenericDAO<Transporte>` | MovimientoFormDialog |
| Taller | TALLER | `GenericDAO<Taller>` | ReparacionFormDialog |
| Importador | IMPORTADOR | `GenericDAO<Importador>` | VentaFormDialog |
| Tipo Desperfecto | TIPO_DESPERFECTO | `GenericDAO<TipoDesperfecto>` | DetalleDesperfectoFormDialog |