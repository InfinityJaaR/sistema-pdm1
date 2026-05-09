package com.ues.sistema_pdm1.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "inventario_pdm1.db";
    private static final int    DB_VERSION = 1;
    private static final String TAG        = "DatabaseHelper";

    // ----------------------------------------------------------------
    // Triggers (tomados de docs/necesitosql/02_triggers_y_datos.txt)
    // ----------------------------------------------------------------

    private static final String TRG_ANIO_VEHICULO =
        "CREATE TRIGGER IF NOT EXISTS TRG_ANIO_VEHICULO " +
        "BEFORE INSERT ON VEHICULO " +
        "BEGIN " +
        "    SELECT CASE " +
        "        WHEN NEW.ANIO <= 2020 " +
        "        THEN RAISE(ABORT, 'No se permiten vehiculos del anio 2020 o anteriores') " +
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

    private static final String TRG_ESTADO_REPARACION =
        "CREATE TRIGGER IF NOT EXISTS TRG_ESTADO_REPARACION " +
        "AFTER UPDATE OF APTO_PARA_VENTA ON REPARACION " +
        "BEGIN " +
        "    UPDATE VEHICULO " +
        "    SET ESTADO_VEHICULO = CASE " +
        "        WHEN NEW.APTO_PARA_VENTA = 1 THEN 'listo' " +
        "        WHEN NEW.REQUIERE_OTRA_REPARACION = 1 THEN 'en reparacion' " +
        "        ELSE ESTADO_VEHICULO " +
        "    END " +
        "    WHERE ID_VEHICULO = NEW.ID_VEHICULO; " +
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
        execSafe(db, TRG_ESTADO_VENDIDO,       "TRG_ESTADO_VENDIDO");
        execSafe(db, TRG_ESTADO_REPARACION,    "TRG_ESTADO_REPARACION");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Triggers primero para evitar conflictos al borrar tablas
        dropSafe(db, "TRIGGER", "TRG_ESTADO_REPARACION");
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