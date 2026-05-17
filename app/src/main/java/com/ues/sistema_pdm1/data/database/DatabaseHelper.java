package com.ues.sistema_pdm1.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "inventario_pdm1.db";
    private static final int    DB_VERSION = 11;
    private static final String TAG        = "DatabaseHelper";

    // ----------------------------------------------------------------
    // Triggers (tomados de docs/necesitosql/02_triggers_y_datos.txt)
    // ----------------------------------------------------------------

    private static final String TRG_ANIO_VEHICULO =
        "CREATE TRIGGER IF NOT EXISTS TRG_ANIO_VEHICULO " +
        "BEFORE INSERT ON VEHICULO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN NEW.ANIO < CAST(strftime('%Y', 'now') AS INTEGER) - 5 " +
        "        THEN RAISE(ABORT, 'No se permiten vehiculos con mas de 5 anios de antiguedad') " +
        "    END; " +
        "END";

    private static final String TRG_CAPACIDAD_SECCION =
        "CREATE TRIGGER IF NOT EXISTS TRG_CAPACIDAD_SECCION " +
        "BEFORE INSERT ON VEHICULO " +
        "WHEN NEW.ID_SECCION IS NOT NULL " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN ( " +
        "            SELECT CAPACIDAD_ACTUAL FROM SECCION " +
        "            WHERE ID_SECCION = NEW.ID_SECCION " +
        "        ) >= ( " +
        "            SELECT CAPACIDAD_MAXIMA FROM SECCION " +
        "            WHERE ID_SECCION = NEW.ID_SECCION " +
        "        ) " +
        "        THEN RAISE(ABORT, 'La seccion ha alcanzado su capacidad maxima') " +
        "    END; " +
        "END";

    private static final String TRG_CAPACIDAD_TRANSPORTE =
        "CREATE TRIGGER IF NOT EXISTS TRG_CAPACIDAD_TRANSPORTE " +
        "BEFORE INSERT ON MOVIMIENTO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN ( " +
        "            SELECT COUNT(*) FROM MOVIMIENTO " +
        "            WHERE ID_TRANSPORTE = NEW.ID_TRANSPORTE " +
        "            AND DATE(FECHA_MOVIMIENTO) = DATE(NEW.FECHA_MOVIMIENTO) " +
        "        ) >= ( " +
        "            SELECT TT.CAPACIDAD_MAX_VEHICULOS " +
        "            FROM TIPO_TRANSPORTE TT " +
        "            JOIN TRANSPORTE T ON T.ID_TIPO_TRANSPORTE = TT.ID_TIPO_TRANSPORTE " +
        "            WHERE T.ID_TRANSPORTE = NEW.ID_TRANSPORTE " +
        "        ) " +
        "        THEN RAISE(ABORT, 'El transporte ha alcanzado su capacidad maxima para este dia') " +
        "    END; " +
        "END";

    private static final String TRG_SECCION_INCREMENTAR =
        "CREATE TRIGGER IF NOT EXISTS TRG_SECCION_INCREMENTAR " +
        "AFTER INSERT ON VEHICULO " +
        "WHEN NEW.ID_SECCION IS NOT NULL " +
        "BEGIN " +
        "    UPDATE SECCION " +
        "    SET CAPACIDAD_ACTUAL = CAPACIDAD_ACTUAL + 1 " +
        "    WHERE ID_SECCION = NEW.ID_SECCION; " +
        "END";

    private static final String TRG_SECCION_MOVER =
        "CREATE TRIGGER IF NOT EXISTS TRG_SECCION_MOVER " +
        "AFTER UPDATE OF ID_SECCION ON VEHICULO " +
        "WHEN OLD.ID_SECCION IS NOT NULL OR NEW.ID_SECCION IS NOT NULL " +
        "BEGIN " +
        "    UPDATE SECCION " +
        "    SET CAPACIDAD_ACTUAL = CAPACIDAD_ACTUAL - 1 " +
        "    WHERE ID_SECCION = OLD.ID_SECCION AND OLD.ID_SECCION IS NOT NULL; " +
        "    UPDATE SECCION " +
        "    SET CAPACIDAD_ACTUAL = CAPACIDAD_ACTUAL + 1 " +
        "    WHERE ID_SECCION = NEW.ID_SECCION AND NEW.ID_SECCION IS NOT NULL; " +
        "END";

    private static final String TRG_ESTADO_VENDIDO =
        "CREATE TRIGGER IF NOT EXISTS TRG_ESTADO_VENDIDO " +
        "AFTER INSERT ON VENTA " +
        "BEGIN " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = 'vendido' " +
        "    WHERE ID_VEHICULO = NEW.ID_VEHICULO; " +
        "END";

    private static final String TRG_ESTADO_VENTA_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_ESTADO_VENTA_DELETE " +
        "AFTER DELETE ON VENTA " +
        "BEGIN " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = 'listo para venta' " +
        "    WHERE ID_VEHICULO = OLD.ID_VEHICULO; " +
        "END";

    private static final String TRG_ESTADO_VENTA_UPDATE =
        "CREATE TRIGGER IF NOT EXISTS TRG_ESTADO_VENTA_UPDATE " +
        "AFTER UPDATE OF ID_VEHICULO ON VENTA " +
        "WHEN OLD.ID_VEHICULO != NEW.ID_VEHICULO " +
        "BEGIN " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = 'listo para venta' " +
        "    WHERE ID_VEHICULO = OLD.ID_VEHICULO; " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = 'vendido' " +
        "    WHERE ID_VEHICULO = NEW.ID_VEHICULO; " +
        "END";

    private static final String TRG_ESTADO_REPARACION =
        "CREATE TRIGGER IF NOT EXISTS TRG_ESTADO_REPARACION " +
        "AFTER UPDATE OF APTO_PARA_VENTA ON REPARACION " +
        "BEGIN " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = CASE " +
        "        WHEN NEW.APTO_PARA_VENTA = 1 THEN 'listo para venta' " +
        "        WHEN NEW.REQUIERE_OTRA_REPARACION = 1 THEN 'en reparacion' " +
        "        ELSE ESTADO_VEHICULO " +
        "    END " +
        "    WHERE ID_VEHICULO = NEW.ID_VEHICULO; " +
        "END";

    private static final String TRG_ESTADO_REPARACION_INSERT =
        "CREATE TRIGGER IF NOT EXISTS TRG_ESTADO_REPARACION_INSERT " +
        "AFTER INSERT ON REPARACION " +
        "BEGIN " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = 'en reparacion' " +
        "    WHERE ID_VEHICULO = NEW.ID_VEHICULO; " +
        "END";

    private static final String TRG_SECCION_DECREMENTAR =
        "CREATE TRIGGER IF NOT EXISTS TRG_SECCION_DECREMENTAR " +
        "AFTER DELETE ON VEHICULO " +
        "WHEN OLD.ID_SECCION IS NOT NULL " +
        "BEGIN " +
        "    UPDATE SECCION " +
        "    SET CAPACIDAD_ACTUAL = CAPACIDAD_ACTUAL - 1 " +
        "    WHERE ID_SECCION = OLD.ID_SECCION; " +
        "END";

    private static final String TRG_CAPACIDAD_SECCION_UPDATE =
        "CREATE TRIGGER IF NOT EXISTS TRG_CAPACIDAD_SECCION_UPDATE " +
        "BEFORE UPDATE OF ID_SECCION ON VEHICULO " +
        "WHEN NEW.ID_SECCION IS NOT NULL AND NEW.ID_SECCION != OLD.ID_SECCION " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN ( " +
        "            SELECT CAPACIDAD_ACTUAL FROM SECCION " +
        "            WHERE ID_SECCION = NEW.ID_SECCION " +
        "        ) >= ( " +
        "            SELECT CAPACIDAD_MAXIMA FROM SECCION " +
        "            WHERE ID_SECCION = NEW.ID_SECCION " +
        "        ) " +
        "        THEN RAISE(ABORT, 'La seccion ha alcanzado su capacidad maxima') " +
        "    END; " +
        "END";

    // ----------------------------------------------------------------
    // Triggers RESTRICT — ON DELETE RESTRICT simulado (sin PRAGMA foreign_keys)
    // Bloquean eliminación de registros padre que tienen hijos referenciándolos
    // ----------------------------------------------------------------

    // Jerarquía geográfica
    private static final String TRG_RESTRICT_PAIS_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_PAIS_DELETE " +
        "BEFORE DELETE ON PAIS " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM DEPARTAMENTO WHERE ID_PAIS = OLD.ID_PAIS) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el pais tiene departamentos asignados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_DEPARTAMENTO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_DEPARTAMENTO_DELETE " +
        "BEFORE DELETE ON DEPARTAMENTO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM MUNICIPIO WHERE ID_DEPARTAMENTO = OLD.ID_DEPARTAMENTO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el departamento tiene municipios asignados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_MUNICIPIO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_MUNICIPIO_DELETE " +
        "BEFORE DELETE ON MUNICIPIO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM DISTRITO WHERE ID_MUNICIPIO = OLD.ID_MUNICIPIO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el municipio tiene distritos asignados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_DISTRITO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_DISTRITO_DELETE " +
        "BEFORE DELETE ON DISTRITO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM BODEGA WHERE ID_DISTRITO = OLD.ID_DISTRITO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el distrito tiene bodegas asignadas') " +
        "        WHEN (SELECT COUNT(*) FROM IMPORTADOR WHERE ID_DISTRITO = OLD.ID_DISTRITO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el distrito tiene importadores asignados') " +
        "    END; " +
        "END";

    // Control de acceso
    private static final String TRG_RESTRICT_OPCION_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_OPCION_DELETE " +
        "BEFORE DELETE ON OPCIONCRUD " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM ACCESOUSUARIO WHERE ID_OPCION = OLD.ID_OPCION) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: la opcion esta asignada a usuarios') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_USUARIO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_USUARIO_DELETE " +
        "BEFORE DELETE ON USUARIO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM ACCESOUSUARIO WHERE ID_USUARIO = OLD.ID_USUARIO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el usuario tiene accesos asignados') " +
        "    END; " +
        "END";

    // Catálogos
    private static final String TRG_RESTRICT_MARCA_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_MARCA_DELETE " +
        "BEFORE DELETE ON MARCA " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM MODELO WHERE ID_MARCA = OLD.ID_MARCA) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: la marca tiene modelos registrados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_MODELO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_MODELO_DELETE " +
        "BEFORE DELETE ON MODELO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM VEHICULO WHERE ID_MODELO = OLD.ID_MODELO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el modelo tiene vehiculos registrados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_TIPO_VEHICULO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_TIPO_VEHICULO_DELETE " +
        "BEFORE DELETE ON TIPO_VEHICULO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM VEHICULO WHERE ID_TIPO_VEHICULO = OLD.ID_TIPO_VEHICULO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el tipo de vehiculo esta en uso') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_TIPO_TRANSPORTE_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_TIPO_TRANSPORTE_DELETE " +
        "BEFORE DELETE ON TIPO_TRANSPORTE " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM TRANSPORTE WHERE ID_TIPO_TRANSPORTE = OLD.ID_TIPO_TRANSPORTE) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el tipo de transporte tiene unidades asignadas') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_TIPO_DESPERFECTO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_TIPO_DESPERFECTO_DELETE " +
        "BEFORE DELETE ON TIPO_DESPERFECTO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM DETALLE_DESPERFECTO WHERE ID_TIPO_DESPERFECTO = OLD.ID_TIPO_DESPERFECTO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el tipo de desperfecto esta en uso') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_TALLER_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_TALLER_DELETE " +
        "BEFORE DELETE ON TALLER " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM REPARACION WHERE ID_TALLER = OLD.ID_TALLER) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el taller tiene reparaciones registradas') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_PERSONAL_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_PERSONAL_DELETE " +
        "BEFORE DELETE ON PERSONAL_INTERNO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM MOVIMIENTO WHERE ID_PERSONAL = OLD.ID_PERSONAL) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el personal tiene movimientos registrados') " +
        "    END; " +
        "END";

    // Transaccionales
    private static final String TRG_RESTRICT_IMPORTADOR_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_IMPORTADOR_DELETE " +
        "BEFORE DELETE ON IMPORTADOR " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM TELEFONO_IMPORTADOR WHERE ID_IMPORTADOR = OLD.ID_IMPORTADOR) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el importador tiene telefonos registrados') " +
        "        WHEN (SELECT COUNT(*) FROM IMPORTACION WHERE ID_IMPORTADOR = OLD.ID_IMPORTADOR) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el importador tiene importaciones registradas') " +
        "        WHEN (SELECT COUNT(*) FROM VENTA WHERE ID_IMPORTADOR = OLD.ID_IMPORTADOR) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el importador tiene ventas registradas') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_IMPORTACION_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_IMPORTACION_DELETE " +
        "BEFORE DELETE ON IMPORTACION " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM VEHICULO WHERE ID_IMPORTACION = OLD.ID_IMPORTACION) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: la importacion tiene vehiculos registrados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_BODEGA_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_BODEGA_DELETE " +
        "BEFORE DELETE ON BODEGA " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM SECCION WHERE ID_BODEGA = OLD.ID_BODEGA) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: la bodega tiene secciones asignadas') " +
        "        WHEN (SELECT COUNT(*) FROM MOVIMIENTO WHERE ID_BODEGA = OLD.ID_BODEGA) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: la bodega tiene movimientos registrados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_SECCION_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_SECCION_DELETE " +
        "BEFORE DELETE ON SECCION " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM VEHICULO WHERE ID_SECCION = OLD.ID_SECCION) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: la seccion tiene vehiculos asignados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_TRANSPORTE_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_TRANSPORTE_DELETE " +
        "BEFORE DELETE ON TRANSPORTE " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM MOVIMIENTO WHERE ID_TRANSPORTE = OLD.ID_TRANSPORTE) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el transporte tiene movimientos registrados') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_VEHICULO_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_VEHICULO_DELETE " +
        "BEFORE DELETE ON VEHICULO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM DETALLE_DESPERFECTO WHERE ID_VEHICULO = OLD.ID_VEHICULO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el vehiculo tiene detalles de desperfecto') " +
        "        WHEN (SELECT COUNT(*) FROM MOVIMIENTO WHERE ID_VEHICULO = OLD.ID_VEHICULO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el vehiculo tiene movimientos registrados') " +
        "        WHEN (SELECT COUNT(*) FROM REPARACION WHERE ID_VEHICULO = OLD.ID_VEHICULO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el vehiculo tiene reparaciones registradas') " +
        "        WHEN (SELECT COUNT(*) FROM VENTA WHERE ID_VEHICULO = OLD.ID_VEHICULO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el vehiculo tiene ventas registradas') " +
        "    END; " +
        "END";

    private static final String TRG_RESTRICT_DETALLE_DELETE =
        "CREATE TRIGGER IF NOT EXISTS TRG_RESTRICT_DETALLE_DELETE " +
        "BEFORE DELETE ON DETALLE_DESPERFECTO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN (SELECT COUNT(*) FROM FOTO_DESPERFECTO WHERE ID_DETALLE_DESPERFECTO = OLD.ID_DETALLE_DESPERFECTO) > 0 " +
        "        THEN RAISE(ABORT, 'No se puede eliminar: el detalle tiene fotos registradas') " +
        "    END; " +
        "END";

    // ----------------------------------------------------------------

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tablas — orden respeta dependencias de FK
        execSafe(db, DatabaseContract.OpcionCrud.CREATE_TABLE,        "OPCIONCRUD");
        execSafe(db, DatabaseContract.Usuario.CREATE_TABLE,           "USUARIO");
        execSafe(db, DatabaseContract.AccesoUsuario.CREATE_TABLE,     "ACCESOUSUARIO");
        execSafe(db, DatabaseContract.Pais.CREATE_TABLE,              "PAIS");
        execSafe(db, DatabaseContract.Departamento.CREATE_TABLE,      "DEPARTAMENTO");
        execSafe(db, DatabaseContract.Municipio.CREATE_TABLE,         "MUNICIPIO");
        execSafe(db, DatabaseContract.Distrito.CREATE_TABLE,          "DISTRITO");
        execSafe(db, DatabaseContract.Bodega.CREATE_TABLE,            "BODEGA");
        execSafe(db, DatabaseContract.Importador.CREATE_TABLE,        "IMPORTADOR");
        execSafe(db, DatabaseContract.Importacion.CREATE_TABLE,       "IMPORTACION");
        execSafe(db, DatabaseContract.Marca.CREATE_TABLE,             "MARCA");
        execSafe(db, DatabaseContract.Modelo.CREATE_TABLE,            "MODELO");
        execSafe(db, DatabaseContract.TipoVehiculo.CREATE_TABLE,      "TIPO_VEHICULO");
        execSafe(db, DatabaseContract.Seccion.CREATE_TABLE,           "SECCION");
        execSafe(db, DatabaseContract.Vehiculo.CREATE_TABLE,          "VEHICULO");
        execSafe(db, DatabaseContract.TipoDesperfecto.CREATE_TABLE,   "TIPO_DESPERFECTO");
        execSafe(db, DatabaseContract.DetalleDesperfecto.CREATE_TABLE,"DETALLE_DESPERFECTO");
        execSafe(db, DatabaseContract.FotoDesperfecto.CREATE_TABLE,   "FOTO_DESPERFECTO");
        execSafe(db, DatabaseContract.TipoTransporte.CREATE_TABLE,    "TIPO_TRANSPORTE");
        execSafe(db, DatabaseContract.Transporte.CREATE_TABLE,        "TRANSPORTE");
        execSafe(db, DatabaseContract.PersonalInterno.CREATE_TABLE,   "PERSONAL_INTERNO");
        execSafe(db, DatabaseContract.Movimiento.CREATE_TABLE,        "MOVIMIENTO");
        execSafe(db, DatabaseContract.Taller.CREATE_TABLE,            "TALLER");
        execSafe(db, DatabaseContract.Reparacion.CREATE_TABLE,        "REPARACION");
        execSafe(db, DatabaseContract.TelefonoImportador.CREATE_TABLE,"TELEFONO_IMPORTADOR");
        execSafe(db, DatabaseContract.Venta.CREATE_TABLE,             "VENTA");

        // Triggers
        execSafe(db, TRG_ANIO_VEHICULO,        "TRG_ANIO_VEHICULO");
        execSafe(db, TRG_CAPACIDAD_SECCION,    "TRG_CAPACIDAD_SECCION");
        execSafe(db, TRG_CAPACIDAD_TRANSPORTE, "TRG_CAPACIDAD_TRANSPORTE");
        execSafe(db, TRG_SECCION_INCREMENTAR,  "TRG_SECCION_INCREMENTAR");
        execSafe(db, TRG_SECCION_MOVER,        "TRG_SECCION_MOVER");
        execSafe(db, TRG_ESTADO_VENDIDO,              "TRG_ESTADO_VENDIDO");
        execSafe(db, TRG_ESTADO_VENTA_DELETE,         "TRG_ESTADO_VENTA_DELETE");
        execSafe(db, TRG_ESTADO_VENTA_UPDATE,         "TRG_ESTADO_VENTA_UPDATE");
        execSafe(db, TRG_ESTADO_REPARACION,           "TRG_ESTADO_REPARACION");
        execSafe(db, TRG_ESTADO_REPARACION_INSERT,    "TRG_ESTADO_REPARACION_INSERT");
        execSafe(db, TRG_SECCION_DECREMENTAR,         "TRG_SECCION_DECREMENTAR");
        execSafe(db, TRG_CAPACIDAD_SECCION_UPDATE,    "TRG_CAPACIDAD_SECCION_UPDATE");

        // Triggers RESTRICT — protección de integridad referencial
        execSafe(db, TRG_RESTRICT_PAIS_DELETE,              "TRG_RESTRICT_PAIS_DELETE");
        execSafe(db, TRG_RESTRICT_DEPARTAMENTO_DELETE,      "TRG_RESTRICT_DEPARTAMENTO_DELETE");
        execSafe(db, TRG_RESTRICT_MUNICIPIO_DELETE,         "TRG_RESTRICT_MUNICIPIO_DELETE");
        execSafe(db, TRG_RESTRICT_DISTRITO_DELETE,          "TRG_RESTRICT_DISTRITO_DELETE");
        execSafe(db, TRG_RESTRICT_OPCION_DELETE,            "TRG_RESTRICT_OPCION_DELETE");
        execSafe(db, TRG_RESTRICT_USUARIO_DELETE,           "TRG_RESTRICT_USUARIO_DELETE");
        execSafe(db, TRG_RESTRICT_MARCA_DELETE,             "TRG_RESTRICT_MARCA_DELETE");
        execSafe(db, TRG_RESTRICT_MODELO_DELETE,            "TRG_RESTRICT_MODELO_DELETE");
        execSafe(db, TRG_RESTRICT_TIPO_VEHICULO_DELETE,     "TRG_RESTRICT_TIPO_VEHICULO_DELETE");
        execSafe(db, TRG_RESTRICT_TIPO_TRANSPORTE_DELETE,   "TRG_RESTRICT_TIPO_TRANSPORTE_DELETE");
        execSafe(db, TRG_RESTRICT_TIPO_DESPERFECTO_DELETE,  "TRG_RESTRICT_TIPO_DESPERFECTO_DELETE");
        execSafe(db, TRG_RESTRICT_TALLER_DELETE,            "TRG_RESTRICT_TALLER_DELETE");
        execSafe(db, TRG_RESTRICT_PERSONAL_DELETE,          "TRG_RESTRICT_PERSONAL_DELETE");
        execSafe(db, TRG_RESTRICT_IMPORTADOR_DELETE,        "TRG_RESTRICT_IMPORTADOR_DELETE");
        execSafe(db, TRG_RESTRICT_IMPORTACION_DELETE,       "TRG_RESTRICT_IMPORTACION_DELETE");
        execSafe(db, TRG_RESTRICT_BODEGA_DELETE,            "TRG_RESTRICT_BODEGA_DELETE");
        execSafe(db, TRG_RESTRICT_SECCION_DELETE,           "TRG_RESTRICT_SECCION_DELETE");
        execSafe(db, TRG_RESTRICT_TRANSPORTE_DELETE,        "TRG_RESTRICT_TRANSPORTE_DELETE");
        execSafe(db, TRG_RESTRICT_VEHICULO_DELETE,          "TRG_RESTRICT_VEHICULO_DELETE");
        execSafe(db, TRG_RESTRICT_DETALLE_DELETE,           "TRG_RESTRICT_DETALLE_DELETE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Triggers primero para evitar conflictos al borrar tablas
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_DETALLE_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_VEHICULO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_TRANSPORTE_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_SECCION_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_BODEGA_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_IMPORTACION_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_IMPORTADOR_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_PERSONAL_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_TALLER_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_TIPO_DESPERFECTO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_TIPO_TRANSPORTE_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_TIPO_VEHICULO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_MODELO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_MARCA_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_USUARIO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_OPCION_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_DISTRITO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_MUNICIPIO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_DEPARTAMENTO_DELETE");
        dropSafe(db, "TRIGGER", "TRG_RESTRICT_PAIS_DELETE");
        dropSafe(db, "TRIGGER", "TRG_CAPACIDAD_SECCION_UPDATE");
        dropSafe(db, "TRIGGER", "TRG_SECCION_DECREMENTAR");
        dropSafe(db, "TRIGGER", "TRG_ESTADO_REPARACION_INSERT");
        dropSafe(db, "TRIGGER", "TRG_ESTADO_REPARACION");
        dropSafe(db, "TRIGGER", "TRG_ESTADO_VENTA_UPDATE");
        dropSafe(db, "TRIGGER", "TRG_ESTADO_VENTA_DELETE");
        dropSafe(db, "TRIGGER", "TRG_ESTADO_VENDIDO");
        dropSafe(db, "TRIGGER", "TRG_SECCION_MOVER");
        dropSafe(db, "TRIGGER", "TRG_SECCION_INCREMENTAR");
        dropSafe(db, "TRIGGER", "TRG_CAPACIDAD_TRANSPORTE");
        dropSafe(db, "TRIGGER", "TRG_CAPACIDAD_SECCION");
        dropSafe(db, "TRIGGER", "TRG_ANIO_VEHICULO");

        // Tablas en orden inverso al de creación
        dropSafe(db, "TABLE", DatabaseContract.Venta.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.TelefonoImportador.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Reparacion.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Taller.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Movimiento.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.PersonalInterno.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Transporte.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.TipoTransporte.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.FotoDesperfecto.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.DetalleDesperfecto.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.TipoDesperfecto.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Vehiculo.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Seccion.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.TipoVehiculo.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Modelo.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Marca.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Importacion.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Importador.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Bodega.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Distrito.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Municipio.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Departamento.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Pais.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.AccesoUsuario.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.Usuario.TABLE_NAME);
        dropSafe(db, "TABLE", DatabaseContract.OpcionCrud.TABLE_NAME);

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // Sin PRAGMA foreign_keys — integridad validada programáticamente en DAOs
    }

    // ----------------------------------------------------------------
    // Helpers internos
    // ----------------------------------------------------------------

    private void execSafe(SQLiteDatabase db, String sql, String label) {
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "Error al crear " + label + ": " + e.getMessage());
        }
    }

    private void dropSafe(SQLiteDatabase db, String type, String name) {
        try {
            db.execSQL("DROP " + type + " IF EXISTS " + name);
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar " + type + " " + name + ": " + e.getMessage());
        }
    }
}