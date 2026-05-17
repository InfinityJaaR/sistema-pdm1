# Sistema de Gestión de Inventario de Vehículos

Aplicación Android para la administración de importaciones, bodegaje y venta de vehículos. Desarrollada como proyecto académico para la asignatura PDM115 — Universidad de El Salvador, 2026.

## Tecnologías

- **Android** — Java, minSdk 24, targetSdk 36
- **Base de datos** — SQLite con acceso via GenericDAO (reflexión)
- **UI** — Material Design 3, ConstraintLayout, AlertDialog fragments
- **Internacionalización** — Español (base), Inglés, Portugués

## Funcionalidades

| Módulo | Descripción |
|---|---|
| Importador | CRUD de importadores con teléfonos múltiples |
| Importación | Registro de importaciones por importador |
| Vehículo | Gestión de vehículos con VIN, estado y fotos de desperfectos |
| Bodega / Sección | Organización física del inventario por niveles |
| Movimiento | Registro de entradas/salidas con transporte asignado |
| Transporte | CRUD de unidades de transporte con tipo y capacidad |
| Reparación | Seguimiento de reparaciones en talleres |
| Venta | Registro de ventas con cambio automático de estado |
| Personal Interno | Empleados y supervisores del sistema |
| Catálogos | Marcas, modelos, tipos de vehículo, talleres, desperfectos |

Los estados del vehículo (`en bodega` → `en reparacion` → `listo para venta` → `vendido`) son gestionados automáticamente mediante triggers SQLite.

## Roles de usuario

| Usuario | Contraseña | Acceso |
|---|---|---|
| `admin` | `adm01` | Todos los módulos |
| `importador` | `imp01` | Importador, Vehículo, Importación, Desperfecto |
| `personal` | `per01` | Bodega, Sección, Movimiento, Transporte, Personal |

## Instalación

**Requisitos:** Android Studio Hedgehog o superior, JDK 11.

1. Clonar el repositorio:
   ```bash
   git clone <url-del-repositorio>
   ```
2. Abrir el proyecto en Android Studio (`File → Open`).
3. Sincronizar Gradle y ejecutar en un emulador o dispositivo físico (Android 7.0+).

Los datos iniciales se cargan automáticamente al primer inicio de la aplicación.
