# LlenarBDGpo02 — Análisis y Estructura Actual
## PDM115 · Sistema de Inventario de Vehículos · Grupo 02 · UES
**Fecha:** Mayo 2026

---

## Sección 1: Estado de Precarga

Todas las 26 tablas están precargadas con datos demo desde la primera ejecución.

| Tabla | Precargada | Cantidad | Datos de ejemplo |
|-------|-----------|----------|-----------------|
| USUARIO | ✅ | 3 | admin/adm01 (id='1'), importador/imp01 (id='2'), personal/per01 (id='3') |
| OPCIONCRUD | ✅ | 13 | 100 (Importador), 200 (Vehículo), 210 (Importación), 300 (Bodega)… |
| ACCESOUSUARIO | ✅ | 22 | admin→13 módulos, importador→4, personal→5 |
| PAIS | ✅ | 1 | El Salvador |
| DEPARTAMENTO | ✅ | 14 | Ahuachapán(1), Santa Ana(2)… Chalatenango(13), San Miguel(14) |
| MUNICIPIO | ✅ | 14 | 1 municipio por departamento |
| DISTRITO | ✅ | 14 | 1 distrito por municipio |
| MARCA | ✅ | 9 | Toyota(1), Honda(2), Ford(3), Chevrolet(4), Nissan(5), Hyundai(6), KIA(7), BMW(8), Mercedes Benz(9) |
| MODELO | ✅ | 18 | Corolla(1), Camry(2), Hilux(3), Civic(4), Accord(5)… Serie 3(17), Clase C(18) |
| TIPO_VEHICULO | ✅ | 5 | Sedán(1), SUV(2), Pickup(3), Minivan(4), Hatchback(5) |
| TIPO_TRANSPORTE | ✅ | 3 | Camión Plataforma(cap 8), Camión Cerrado(cap 6), Remolque(cap 10) |
| TIPO_DESPERFECTO | ✅ | 4 | Rayón(1), Golpe(2), Cristal roto(3), Mecánico(4) |
| TALLER | ✅ | 3 | Taller Autorizado A (autorizado), Taller B (no autorizado), Taller C (autorizado) |
| PERSONAL_INTERNO | ✅ | 3 | Juan Pérez (Almacenero), María González (Supervisor), Carlos López (Operario) |
| BODEGA | ✅ | 6 | Bodega Central(D5), Occidente(D2), Oriente(D14), Sur(D7), Paracentral(D8), Costera(D3) |
| SECCION | ✅ | 18 | 3 secciones (niveles 1–3) por cada una de las 6 bodegas, cap. 50 c/u |
| IMPORTADOR | ✅ | 3 | Carlos Mendoza(D5), María González(D2), Roberto Hernández(D14) |
| TELEFONO_IMPORTADOR | ✅ | 5 | 2 teléfonos para Carlos, 2 para María, 1 para Roberto |
| IMPORTACION | ✅ | 4 | 2 de Carlos, 1 de María, 1 de Roberto |
| TRANSPORTE | ✅ | 3 | Camión P-123-456, Camión cerrado P-789-012, Remolque P-345-678 |
| VEHICULO | ✅ | 5 | VINs: TOY(Corolla), HON(Civic), NIS(Frontier), HYU(Tucson), BMW(Serie 3) |
| MOVIMIENTO | ✅ | 4 | Entradas a bodega de vehículos 1–4 |
| DETALLE_DESPERFECTO | ✅ | 4 | Golpe en V1, Rayón en V2, Cristal roto en V3, Mecánico en V4 |
| FOTO_DESPERFECTO | ✅ | 4 | 1 foto por cada detalle de desperfecto |
| REPARACION | ✅ | 3 | Vehículos 1, 2 y 3 en talleres 1, 2 y 3 |
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
| 2 | `opcionDAO.contar() == 0` | 13 opciones de menú | 13 |
| 3 | `accesoDAO.contar() == 0` | Matriz de accesos | 22 |
| 4 | `paisDAO.contar() == 0` | País + geo + catálogos + talleres + personal | 86 |
| 5 | `bodegaDAO.contar() == 0` | 6 bodegas | 6 |
| 6 | `seccionDAO.contar() == 0` | 18 secciones (3 por bodega) | 18 |
| 7 | `importadorDAO.contar() == 0` | 3 importadores | 3 |
| 8 | `telefonoDAO.contar() == 0` | 5 teléfonos de importador | 5 |
| 9 | `importacionDAO.contar() == 0` | 4 importaciones | 4 |
| 10 | `transporteDAO.contar() == 0` | 3 transportes | 3 |
| 11 | `vehiculoDAO.contar() == 0` | 5 vehículos | 5 |
| 12 | `movimientoDAO.contar() == 0` | 4 movimientos | 4 |
| 13 | `detalleDAO.contar() == 0` | 4 detalles de desperfecto | 4 |
| 14 | `fotoDAO.contar() == 0` | 4 fotos de desperfecto | 4 |
| 15 | `reparacionDAO.contar() == 0` | 3 reparaciones | 3 |
| 16 | `ventaDAO.contar() == 0` | 2 ventas | 2 |

> ⚠️ **Nota Sección 4 — guarda compartida:**
> País, departamentos, municipios, distritos, marcas, modelos, tipos de vehículo/transporte/desperfecto,
> talleres y personal están bajo una sola guarda (`paisDAO.contar() == 0`). Si el país ya existe
> pero alguna subtabla quedó a medias por un crash, no se reintentaría. Para el contexto de
> instalación limpia de desarrollo esto no es un problema práctico.

---

## Sección 3: Estado de vehículos tras la carga inicial

Los triggers actúan durante la misma inserción de datos, cambiando el estado según la secuencia:

| Vehículo | VIN | Estado tras carga | Razón |
|----------|-----|------------------|-------|
| 1 — Corolla 2022 | VIN2024TOY001 | `en reparacion` | TRG_ESTADO_REPARACION_INSERT actúa al insertar reparación 1 |
| 2 — Civic 2023 | VIN2024HON001 | `en reparacion` | TRG_ESTADO_REPARACION_INSERT actúa al insertar reparación 2 |
| 3 — Frontier 2021 | VIN2024NIS001 | `en reparacion` | TRG_ESTADO_REPARACION_INSERT actúa al insertar reparación 3 |
| 4 — Tucson 2023 | VIN2024HYU001 | `vendido` | TRG_ESTADO_VENDIDO actúa al insertar venta 1 |
| 5 — Serie 3 2024 | VIN2024BMW001 | `vendido` | TRG_ESTADO_VENDIDO actúa al insertar venta 2 |

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
Toyota=1, Honda=2, Ford=3, Chevrolet=4, Nissan=5, Hyundai=6, KIA=7, BMW=8, Mercedes Benz=9.

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
| Vehiculo.idImportacion = 1–3 | VEHICULO | IMPORTACION | ✅ |
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
  ✅ TRG_ESTADO_REPARACION_INSERT actúa en INSERT de REPARACION
     → vehículos 1, 2 y 3 quedan en 'en reparacion'
  ✅ TRG_ESTADO_VENDIDO actúa en INSERT de VENTA
     → vehículos 4 y 5 quedan en 'vendido'
  ✅ TRG_SECCION_INCREMENTAR actualiza CAPACIDAD_ACTUAL
     en cada sección al insertar vehículos

  Los integrantes de Fase 5 encuentran datos reales en todas
  las tablas desde la primera ejecución. Los Spinners de todos
  los formularios estarán poblados sin necesidad de insertar
  datos manualmente.
══════════════════════════════════════════════════════════════
```
