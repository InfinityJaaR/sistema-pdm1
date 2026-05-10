# LlenarBDGpo02 — Análisis y Estructura Actual
## PDM115 · Sistema de Inventario de Vehículos · Grupo 02 · UES
**Fecha:** Mayo 2026

---

## Sección 1: Estado de Precarga

Todas las 26 tablas están precargadas con datos demo desde la primera ejecución.

| Tabla | Precargada | Cantidad | Datos de ejemplo |
|-------|-----------|----------|-----------------|
| USUARIO | ✅ | 3 | admin/adm01, importador/imp01, personal/per01 |
| OPCIONCRUD | ✅ | 15 | 100 (Importador), 200 (Vehículo), 630 (Marca)… |
| ACCESOUSUARIO | ✅ | 25 | admin→15 opciones, importador→5, personal→5 |
| PAIS | ✅ | 1 | El Salvador |
| DEPARTAMENTO | ✅ | 14 | Ahuachapán, Santa Ana, Sonsonate… |
| MUNICIPIO | ✅ | 14 | Ahuachapán, Santa Ana, Santa Tecla… |
| DISTRITO | ✅ | 14 | Ahuachapán Centro, Santa Ana Centro… |
| MARCA | ✅ | 9 | Toyota, Honda, Ford, Chevrolet, Nissan… |
| MODELO | ✅ | 18 | Corolla, Camry, Hilux, Civic, Accord… |
| TIPO_VEHICULO | ✅ | 5 | Sedán, SUV, Pickup, Minivan, Hatchback |
| TIPO_TRANSPORTE | ✅ | 3 | Camión Plataforma (cap. 8), Camión Cerrado (cap. 6), Remolque (cap. 10) |
| TIPO_DESPERFECTO | ✅ | 4 | Rayón, Golpe, Cristal roto, Mecánico |
| TALLER | ✅ | 3 | Taller Autorizado A, Taller B, Taller C |
| PERSONAL_INTERNO | ✅ | 3 | Juan Pérez (Almacenero), María González (Supervisor), Carlos López (Operario) |
| BODEGA | ✅ | 6 | Bodega Central, Occidente, Oriente, Sur, Paracentral, Costera |
| SECCION | ✅ | 18 | 3 secciones (niveles 1–3) por cada una de las 6 bodegas |
| IMPORTADOR | ✅ | 3 | Carlos Mendoza, María González, Roberto Hernández |
| TELEFONO_IMPORTADOR | ✅ | 5 | 2 teléfonos para Carlos, 2 para María, 1 para Roberto |
| IMPORTACION | ✅ | 4 | 2 de Carlos, 1 de María, 1 de Roberto |
| TRANSPORTE | ✅ | 3 | Placa P-123-456, P-789-012, P-345-678 |
| VEHICULO | ✅ | 5 | VINs: TOY, HON, NIS, HYU, BMW — años 2021–2024 |
| MOVIMIENTO | ✅ | 4 | Ingresos a bodega de vehículos 1–4 |
| DETALLE_DESPERFECTO | ✅ | 4 | Golpe, Rayón, Cristal roto, Falla mecánica |
| FOTO_DESPERFECTO | ✅ | 4 | Rutas de referencia por cada detalle |
| REPARACION | ✅ | 3 | Vehículos 1–3, en talleres distintos |
| VENTA | ✅ | 2 | Vehículos 4 y 5 → trigger los cambia a `'vendido'` |

**Resumen: 26 de 26 tablas precargadas ✅**

---

## Sección 2: Estructura del Código

### DAOs instanciados

Los 26 DAOs se declaran al inicio del método, todos con `GenericDAO<T>`:

```
usuarioDAO, opcionDAO, accesoDAO, paisDAO, departamentoDAO, municipioDAO,
distritoDAO, marcaDAO, modeloDAO, tipoVehiculoDAO, tipoTransporteDAO,
tipoDesperfectoDAO, tallerDAO, personalDAO,
bodegaDAO, seccionDAO, importadorDAO, telefonoDAO, importacionDAO,
transporteDAO, vehiculoDAO, movimientoDAO, detalleDAO, fotoDAO,
reparacionDAO, ventaDAO
```

### 16 secciones con guardas independientes

Cada sección tiene su propio `contar() == 0`, lo que permite que si una tabla
falla, las demás se intentan en la siguiente ejecución.

| Sección | Guarda | Qué inserta | Registros |
|---------|--------|------------|-----------|
| 1 | `usuarioDAO.contar() == 0` | 3 usuarios | 3 |
| 2 | `opcionDAO.contar() == 0` | 15 opciones de menú | 15 |
| 3 | `accesoDAO.contar() == 0` | Matriz de accesos | 25 |
| 4 | `paisDAO.contar() == 0` | País + geo + catálogos + talleres + personal | 88 |
| 5 | `bodegaDAO.contar() == 0` | 6 bodegas | 6 |
| 6 | `seccionDAO.contar() == 0` | 18 secciones (3 por bodega) | 18 |
| 7 | `importadorDAO.contar() == 0` | 3 importadores | 3 |
| 8 | `telefonoDAO.contar() == 0` | 5 teléfonos de importador | 5 |
| 9 | `importacionDAO.contar() == 0` | 4 importaciones | 4 |
| 10 | `transporteDAO.contar() == 0` | 3 transportes | 3 |
| 11 | `vehiculoDAO.contar() == 0` | 5 vehículos (años 2021–2024) | 5 |
| 12 | `movimientoDAO.contar() == 0` | 4 movimientos | 4 |
| 13 | `detalleDAO.contar() == 0` | 4 detalles de desperfecto | 4 |
| 14 | `fotoDAO.contar() == 0` | 4 fotos de desperfecto (rutas de referencia) | 4 |
| 15 | `reparacionDAO.contar() == 0` | 3 reparaciones | 3 |
| 16 | `ventaDAO.contar() == 0` | 2 ventas | 2 |

> ⚠️ **Nota Sección 4 — guarda compartida:**
> País, departamentos, municipios, distritos, marcas, modelos, tipos de vehículo/transporte/desperfecto,
> talleres y personal están bajo una sola guarda (`paisDAO.contar() == 0`). Si el país ya existe
> pero alguna subtabla quedó a medias por un crash, no se reintentaría. Para el contexto de
> instalación limpia de desarrollo esto no es un problema práctico.

---

## Sección 3: Estado de vehículos tras la carga inicial

| Vehículo | VIN | Estado tras carga | Razón |
|----------|-----|------------------|-------|
| 1 — Corolla 2022 | VIN2024TOY001 | `en bodega` | Tiene reparación, pero trigger solo actúa en UPDATE |
| 2 — Civic 2023 | VIN2024HON001 | `en bodega` | Tiene reparación, mismo motivo |
| 3 — Frontier 2021 | VIN2024NIS001 | `en bodega` | Tiene reparación, mismo motivo |
| 4 — Tucson 2023 | VIN2024HYU001 | `vendido` | Trigger `TRG_ESTADO_VENDIDO` actúa en INSERT de VENTA |
| 5 — Serie 3 2024 | VIN2024BMW001 | `vendido` | Trigger `TRG_ESTADO_VENDIDO` actúa en INSERT de VENTA |

---

## Sección 4: Uso de IDs

| Tabla | Tipo de ID | Cómo se inserta |
|-------|-----------|-----------------|
| USUARIO | String (`"1"`, `"2"`, `"3"`) | ID manual explícito |
| OPCIONCRUD | String (`"100"`, `"200"`…) | ID manual explícito |
| ACCESOUSUARIO | String (combinado) | ID manual implícito |
| Resto de tablas | int auto-incremento | Se pasa `0`; SQLite asigna el ID |

Los modelos cargados con `id = 0` obtienen su ID real de SQLite por orden de inserción.
Los FK de secciones posteriores (p.ej. `idMarca` en Modelo) asumen ese orden:
Toyota = 1, Honda = 2, Ford = 3, etc.

---

## Sección 5: Verificación de FKs

| FK | Origen | Destino | Estado |
|----|--------|---------|--------|
| Departamento.idPais = 1 | DEPARTAMENTO | PAIS (ID=1) | ✅ |
| Municipio.idDepto = 1–14 | MUNICIPIO | DEPARTAMENTO | ✅ |
| Distrito.idMunicipio = 1–14 | DISTRITO | MUNICIPIO | ✅ |
| Modelo.idMarca = 1–9 | MODELO | MARCA | ✅ |
| Bodega.idDistrito = 5,2,14,7,8,3 | BODEGA | DISTRITO | ✅ |
| Seccion.idBodega = 1–6 | SECCION | BODEGA | ✅ |
| Importador.idDistrito = 5,2,14 | IMPORTADOR | DISTRITO | ✅ |
| TelefonoImportador.idImportador = 1–3 | TELEFONO | IMPORTADOR | ✅ |
| Importacion.idImportador = 1–3 | IMPORTACION | IMPORTADOR | ✅ |
| Transporte.idTipoTransporte = 1–3 | TRANSPORTE | TIPO_TRANSPORTE | ✅ |
| Vehiculo.idImportacion = 1–4 | VEHICULO | IMPORTACION | ✅ |
| Vehiculo.idModelo = 1,4,12,13,17 | VEHICULO | MODELO | ✅ |
| Vehiculo.idSeccion = 1,2,4,7,10 | VEHICULO | SECCION | ✅ |
| Movimiento.idVehiculo = 1–4 | MOVIMIENTO | VEHICULO | ✅ |
| DetalleDesperfecto.idVehiculo = 1–4 | DETALLE | VEHICULO | ✅ |
| FotoDesperfecto.idDetalle = 1–4 | FOTO | DETALLE_DESPERFECTO | ✅ |
| Reparacion.idVehiculo = 1–3 | REPARACION | VEHICULO | ✅ |
| Venta.idVehiculo = 4,5 | VENTA | VEHICULO | ✅ |

---

## Sección 6: Conclusión

```
══════════════════════════════════════════════════════════════
  LlenarBDGpo02 está COMPLETO Y FUNCIONAL.

  ✅ 26 de 26 tablas precargadas con datos coherentes
  ✅ 16 guardas independientes por sección
  ✅ Todas las FKs apuntan a registros existentes
  ✅ Triggers TRG_ESTADO_VENDIDO y TRG_SECCION_INCREMENTAR
     ya actúan sobre los datos de demo al insertar

  Los integrantes de Fase 5 encuentran datos reales en todas
  las tablas desde la primera ejecución. Los Spinners de todos
  los formularios estarán poblados sin necesidad de insertar
  datos manualmente.
══════════════════════════════════════════════════════════════
```