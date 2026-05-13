package com.ues.sistema_pdm1.data.database;

public final class DatabaseContract {

    private DatabaseContract() {}

    public static class OpcionCrud {
        public static final String TABLE_NAME = "OPCIONCRUD";
        public static final String COL_ID_OPCION  = "ID_OPCION";
        public static final String COL_DES_OPCION = "DES_OPCION";
        public static final String COL_NUM_CRUD   = "NUM_CRUD";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS OPCIONCRUD (" +
            "    ID_OPCION       TEXT    NOT NULL," +
            "    DES_OPCION      TEXT    NOT NULL," +
            "    NUM_CRUD        INTEGER NOT NULL," +
            "    PRIMARY KEY (ID_OPCION)" +
            ")";
    }

    public static class Usuario {
        public static final String TABLE_NAME    = "USUARIO";
        public static final String COL_ID_USUARIO  = "ID_USUARIO";
        public static final String COL_NOM_USUARIO = "NOM_USUARIO";
        public static final String COL_CLAVE       = "CLAVE";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS USUARIO (" +
            "    ID_USUARIO      TEXT    NOT NULL," +
            "    NOM_USUARIO     TEXT    NOT NULL," +
            "    CLAVE           TEXT    NOT NULL," +
            "    PRIMARY KEY (ID_USUARIO)" +
            ")";
    }

    public static class AccesoUsuario {
        public static final String TABLE_NAME   = "ACCESOUSUARIO";
        public static final String COL_ID_OPCION  = "ID_OPCION";
        public static final String COL_ID_USUARIO = "ID_USUARIO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS ACCESOUSUARIO (" +
            "    ID_OPCION       TEXT    NOT NULL," +
            "    ID_USUARIO      TEXT    NOT NULL," +
            "    PRIMARY KEY (ID_OPCION, ID_USUARIO)," +
            "    FOREIGN KEY (ID_OPCION)   REFERENCES OPCIONCRUD (ID_OPCION)," +
            "    FOREIGN KEY (ID_USUARIO)  REFERENCES USUARIO (ID_USUARIO)" +
            ")";
    }

    public static class Pais {
        public static final String TABLE_NAME  = "PAIS";
        public static final String COL_ID_PAIS    = "ID_PAIS";
        public static final String COL_NOMBRE_PAIS = "NOMBRE_PAIS";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS PAIS (" +
            "    ID_PAIS         INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    NOMBRE_PAIS     TEXT    NOT NULL" +
            ")";
    }

    public static class Departamento {
        public static final String TABLE_NAME            = "DEPARTAMENTO";
        public static final String COL_ID_DEPARTAMENTO     = "ID_DEPARTAMENTO";
        public static final String COL_ID_PAIS             = "ID_PAIS";
        public static final String COL_NOMBRE_DEPARTAMENTO = "NOMBRE_DEPARTAMENTO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS DEPARTAMENTO (" +
            "    ID_DEPARTAMENTO     INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_PAIS             INTEGER NOT NULL," +
            "    NOMBRE_DEPARTAMENTO TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_PAIS) REFERENCES PAIS (ID_PAIS)" +
            ")";
    }

    public static class Municipio {
        public static final String TABLE_NAME         = "MUNICIPIO";
        public static final String COL_ID_MUNICIPIO     = "ID_MUNICIPIO";
        public static final String COL_ID_DEPARTAMENTO  = "ID_DEPARTAMENTO";
        public static final String COL_NOMBRE_MUNICIPIO = "NOMBRE_MUNICIPIO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS MUNICIPIO (" +
            "    ID_MUNICIPIO        INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_DEPARTAMENTO     INTEGER," +
            "    NOMBRE_MUNICIPIO    TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_DEPARTAMENTO) REFERENCES DEPARTAMENTO (ID_DEPARTAMENTO)" +
            ")";
    }

    public static class Distrito {
        public static final String TABLE_NAME      = "DISTRITO";
        public static final String COL_ID_DISTRITO    = "ID_DISTRITO";
        public static final String COL_ID_MUNICIPIO   = "ID_MUNICIPIO";
        public static final String COL_NOMBRE_DISTRITO = "NOMBRE_DISTRITO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS DISTRITO (" +
            "    ID_DISTRITO         INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_MUNICIPIO        INTEGER," +
            "    NOMBRE_DISTRITO     TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_MUNICIPIO) REFERENCES MUNICIPIO (ID_MUNICIPIO)" +
            ")";
    }

    public static class Bodega {
        public static final String TABLE_NAME      = "BODEGA";
        public static final String COL_ID_BODEGA      = "ID_BODEGA";
        public static final String COL_ID_DISTRITO    = "ID_DISTRITO";
        public static final String COL_NOMBRE_BODEGA  = "NOMBRE_BODEGA";
        public static final String COL_DIRECCION_BODEGA = "DIRECCION_BODEGA";
        public static final String COL_CAPACIDAD_TOTAL = "CAPACIDAD_TOTAL";
        public static final String COL_CAPACIDAD_ACTUAL = "CAPACIDAD_ACTUAL";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS BODEGA (" +
            "    ID_BODEGA           INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_DISTRITO         INTEGER," +
            "    NOMBRE_BODEGA       TEXT    NOT NULL," +
            "    DIRECCION_BODEGA    TEXT    NOT NULL," +
            "    CAPACIDAD_TOTAL     INTEGER NOT NULL DEFAULT 0 CHECK (CAPACIDAD_TOTAL <= 50)," +
            "    CAPACIDAD_ACTUAL    INTEGER NOT NULL DEFAULT 0," +
            "    FOREIGN KEY (ID_DISTRITO) REFERENCES DISTRITO (ID_DISTRITO)" +
            ")";
    }

    public static class Importador {
        public static final String TABLE_NAME              = "IMPORTADOR";
        public static final String COL_ID_IMPORTADOR        = "ID_IMPORTADOR";
        public static final String COL_ID_DISTRITO          = "ID_DISTRITO";
        public static final String COL_NOMBRE_IMPORTADOR    = "NOMBRE_IMPORTADOR";
        public static final String COL_APELLIDO_IMPORTADOR  = "APELLIDO_IMPORTADOR";
        public static final String COL_APELLIDO_CASADA      = "APELLIDO_CASADA";
        public static final String COL_GENERO               = "GENERO";
        public static final String COL_DIRECCION_IMPORTADOR = "DIRECCION_IMPORTADOR";
        public static final String COL_FECHA_NACIMIENTO     = "FECHA_NACIMIENTO";
        public static final String COL_CORREO_ELECTRONICO   = "CORREO_ELECTRONICO";
        public static final String COL_NUI                  = "NUI";
        public static final String COL_NOMBRE_RESPONSABLE   = "NOMBRE_RESPONSABLE";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS IMPORTADOR (" +
            "    ID_IMPORTADOR           INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_DISTRITO             INTEGER," +
            "    NOMBRE_IMPORTADOR       TEXT    NOT NULL," +
            "    APELLIDO_IMPORTADOR     TEXT    NOT NULL," +
            "    APELLIDO_CASADA         TEXT," +
            "    GENERO                  TEXT    NOT NULL," +
            "    DIRECCION_IMPORTADOR    TEXT    NOT NULL," +
            "    FECHA_NACIMIENTO        TEXT    NOT NULL," +
            "    CORREO_ELECTRONICO      TEXT," +
            "    NUI                     TEXT    NOT NULL," +
            "    NOMBRE_RESPONSABLE      TEXT," +
            "    FOREIGN KEY (ID_DISTRITO) REFERENCES DISTRITO (ID_DISTRITO)" +
            ")";
    }

    public static class Importacion {
        public static final String TABLE_NAME         = "IMPORTACION";
        public static final String COL_ID_IMPORTACION  = "ID_IMPORTACION";
        public static final String COL_ID_IMPORTADOR   = "ID_IMPORTADOR";
        public static final String COL_FECHA_IMPORTACION = "FECHA_IMPORTACION";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS IMPORTACION (" +
            "    ID_IMPORTACION      INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_IMPORTADOR       INTEGER," +
            "    FECHA_IMPORTACION   TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_IMPORTADOR) REFERENCES IMPORTADOR (ID_IMPORTADOR)" +
            ")";
    }

    public static class Marca {
        public static final String TABLE_NAME   = "MARCA";
        public static final String COL_ID_MARCA    = "ID_MARCA";
        public static final String COL_NOMBRE_MARCA = "NOMBRE_MARCA";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS MARCA (" +
            "    ID_MARCA        INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    NOMBRE_MARCA    TEXT    NOT NULL" +
            ")";
    }

    public static class Modelo {
        public static final String TABLE_NAME    = "MODELO";
        public static final String COL_ID_MODELO    = "ID_MODELO";
        public static final String COL_ID_MARCA     = "ID_MARCA";
        public static final String COL_NOMBRE_MODELO = "NOMBRE_MODELO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS MODELO (" +
            "    ID_MODELO       INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_MARCA        INTEGER," +
            "    NOMBRE_MODELO   TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_MARCA) REFERENCES MARCA (ID_MARCA)" +
            ")";
    }

    public static class TipoVehiculo {
        public static final String TABLE_NAME                 = "TIPO_VEHICULO";
        public static final String COL_ID_TIPO_VEHICULO         = "ID_TIPO_VEHICULO";
        public static final String COL_DESCRIPCION_TIPO_VEHICULO = "DESCRIPCION_TIPO_VEHICULO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS TIPO_VEHICULO (" +
            "    ID_TIPO_VEHICULO            INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    DESCRIPCION_TIPO_VEHICULO   TEXT    NOT NULL" +
            ")";
    }

    public static class Seccion {
        public static final String TABLE_NAME       = "SECCION";
        public static final String COL_ID_SECCION      = "ID_SECCION";
        public static final String COL_ID_BODEGA       = "ID_BODEGA";
        public static final String COL_NIVEL           = "NIVEL";
        public static final String COL_CAPACIDAD_MAXIMA = "CAPACIDAD_MAXIMA";
        public static final String COL_CAPACIDAD_ACTUAL = "CAPACIDAD_ACTUAL";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS SECCION (" +
            "    ID_SECCION          INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_BODEGA           INTEGER," +
            "    NIVEL               INTEGER NOT NULL," +
            "    CAPACIDAD_MAXIMA    INTEGER NOT NULL," +
            "    CAPACIDAD_ACTUAL    INTEGER NOT NULL," +
            "    FOREIGN KEY (ID_BODEGA) REFERENCES BODEGA (ID_BODEGA)" +
            ")";
    }

    public static class Vehiculo {
        public static final String TABLE_NAME       = "VEHICULO";
        public static final String COL_ID_VEHICULO    = "ID_VEHICULO";
        public static final String COL_ID_TIPO_VEHICULO = "ID_TIPO_VEHICULO";
        public static final String COL_ID_SECCION     = "ID_SECCION";
        public static final String COL_ID_MODELO      = "ID_MODELO";
        public static final String COL_ID_IMPORTACION = "ID_IMPORTACION";
        public static final String COL_VIN            = "VIN";
        public static final String COL_ANIO           = "ANIO";
        public static final String COL_COLOR_VEHICULO  = "COLOR_VEHICULO";
        public static final String COL_ESTADO_VEHICULO = "ESTADO_VEHICULO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS VEHICULO (" +
            "    ID_VEHICULO         INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_TIPO_VEHICULO    INTEGER," +
            "    ID_SECCION          INTEGER," +
            "    ID_MODELO           INTEGER," +
            "    ID_IMPORTACION      INTEGER," +
            "    VIN                 TEXT    NOT NULL," +
            "    ANIO                INTEGER NOT NULL," +
            "    COLOR_VEHICULO      TEXT    NOT NULL," +
            "    ESTADO_VEHICULO     TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_IMPORTACION)   REFERENCES IMPORTACION (ID_IMPORTACION)," +
            "    FOREIGN KEY (ID_MODELO)        REFERENCES MODELO (ID_MODELO)," +
            "    FOREIGN KEY (ID_TIPO_VEHICULO) REFERENCES TIPO_VEHICULO (ID_TIPO_VEHICULO)," +
            "    FOREIGN KEY (ID_SECCION)       REFERENCES SECCION (ID_SECCION)" +
            ")";
    }

    public static class TipoDesperfecto {
        public static final String TABLE_NAME                    = "TIPO_DESPERFECTO";
        public static final String COL_ID_TIPO_DESPERFECTO         = "ID_TIPO_DESPERFECTO";
        public static final String COL_NOMBRE_TIPO_DESPERFECTO      = "NOMBRE_TIPO_DESPERFECTO";
        public static final String COL_DESCRIPCION_TIPO_DESPERFECTO = "DESCRIPCION_TIPO_DESPERFECTO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS TIPO_DESPERFECTO (" +
            "    ID_TIPO_DESPERFECTO             INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    NOMBRE_TIPO_DESPERFECTO         TEXT    NOT NULL," +
            "    DESCRIPCION_TIPO_DESPERFECTO    TEXT" +
            ")";
    }

    public static class DetalleDesperfecto {
        public static final String TABLE_NAME              = "DETALLE_DESPERFECTO";
        public static final String COL_ID_DETALLE_DESPERFECTO = "ID_DETALLE_DESPERFECTO";
        public static final String COL_ID_VEHICULO           = "ID_VEHICULO";
        public static final String COL_ID_TIPO_DESPERFECTO   = "ID_TIPO_DESPERFECTO";
        public static final String COL_DESCRIPCION_DETALLE   = "DESCRIPCION_DETALLE";
        public static final String COL_FECHA_REGISTRO        = "FECHA_REGISTRO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS DETALLE_DESPERFECTO (" +
            "    ID_DETALLE_DESPERFECTO  INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_VEHICULO             INTEGER," +
            "    ID_TIPO_DESPERFECTO     INTEGER," +
            "    DESCRIPCION_DETALLE     TEXT    NOT NULL," +
            "    FECHA_REGISTRO          TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_VEHICULO)         REFERENCES VEHICULO (ID_VEHICULO)," +
            "    FOREIGN KEY (ID_TIPO_DESPERFECTO) REFERENCES TIPO_DESPERFECTO (ID_TIPO_DESPERFECTO)" +
            ")";
    }

    public static class FotoDesperfecto {
        public static final String TABLE_NAME             = "FOTO_DESPERFECTO";
        public static final String COL_ID_FOTO_DESPERFECTO    = "ID_FOTO_DESPERFECTO";
        public static final String COL_ID_DETALLE_DESPERFECTO = "ID_DETALLE_DESPERFECTO";
        public static final String COL_RUTA_IMAGEN            = "RUTA_IMAGEN";
        public static final String COL_FECHA_TOMA             = "FECHA_TOMA";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS FOTO_DESPERFECTO (" +
            "    ID_FOTO_DESPERFECTO         INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_DETALLE_DESPERFECTO      INTEGER," +
            "    RUTA_IMAGEN                 TEXT    NOT NULL," +
            "    FECHA_TOMA                  TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_DETALLE_DESPERFECTO) REFERENCES DETALLE_DESPERFECTO (ID_DETALLE_DESPERFECTO)" +
            ")";
    }

    public static class TipoTransporte {
        public static final String TABLE_NAME                   = "TIPO_TRANSPORTE";
        public static final String COL_ID_TIPO_TRANSPORTE         = "ID_TIPO_TRANSPORTE";
        public static final String COL_DESCRIPCION_TIPO_TRANSPORTE = "DESCRIPCION_TIPO_TRANSPORTE";
        public static final String COL_CAPACIDAD_MAX_VEHICULOS    = "CAPACIDAD_MAX_VEHICULOS";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS TIPO_TRANSPORTE (" +
            "    ID_TIPO_TRANSPORTE              INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    DESCRIPCION_TIPO_TRANSPORTE     TEXT    NOT NULL," +
            "    CAPACIDAD_MAX_VEHICULOS         INTEGER NOT NULL" +
            ")";
    }

    public static class Transporte {
        public static final String TABLE_NAME             = "TRANSPORTE";
        public static final String COL_ID_TRANSPORTE        = "ID_TRANSPORTE";
        public static final String COL_ID_TIPO_TRANSPORTE   = "ID_TIPO_TRANSPORTE";
        public static final String COL_PLACA                = "PLACA";
        public static final String COL_DESCRIPCION_TRANSPORTE = "DESCRIPCION_TRANSPORTE";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS TRANSPORTE (" +
            "    ID_TRANSPORTE               INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_TIPO_TRANSPORTE          INTEGER," +
            "    PLACA                       TEXT    NOT NULL," +
            "    DESCRIPCION_TRANSPORTE      TEXT," +
            "    FOREIGN KEY (ID_TIPO_TRANSPORTE) REFERENCES TIPO_TRANSPORTE (ID_TIPO_TRANSPORTE)" +
            ")";
    }

    public static class PersonalInterno {
        public static final String TABLE_NAME          = "PERSONAL_INTERNO";
        public static final String COL_ID_PERSONAL      = "ID_PERSONAL";
        public static final String COL_NOMBRE_PERSONAL  = "NOMBRE_PERSONAL";
        public static final String COL_APELLIDO_PERSONAL = "APELLIDO_PERSONAL";
        public static final String COL_CARGO            = "CARGO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS PERSONAL_INTERNO (" +
            "    ID_PERSONAL         INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    NOMBRE_PERSONAL     TEXT    NOT NULL," +
            "    APELLIDO_PERSONAL   TEXT    NOT NULL," +
            "    CARGO               TEXT    NOT NULL" +
            ")";
    }

    public static class Movimiento {
        public static final String TABLE_NAME        = "MOVIMIENTO";
        public static final String COL_ID_MOVIMIENTO   = "ID_MOVIMIENTO";
        public static final String COL_ID_TRANSPORTE   = "ID_TRANSPORTE";
        public static final String COL_ID_PERSONAL     = "ID_PERSONAL";
        public static final String COL_ID_VEHICULO     = "ID_VEHICULO";
        public static final String COL_ID_BODEGA       = "ID_BODEGA";
        public static final String COL_TIPO_MOVIMIENTO = "TIPO_MOVIMIENTO";
        public static final String COL_FECHA_MOVIMIENTO = "FECHA_MOVIMIENTO";
        public static final String COL_MOTIVO          = "MOTIVO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS MOVIMIENTO (" +
            "    ID_MOVIMIENTO       INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_TRANSPORTE       INTEGER," +
            "    ID_PERSONAL         INTEGER," +
            "    ID_VEHICULO         INTEGER," +
            "    ID_BODEGA           INTEGER," +
            "    TIPO_MOVIMIENTO     TEXT    NOT NULL," +
            "    FECHA_MOVIMIENTO    TEXT    NOT NULL," +
            "    MOTIVO              TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_TRANSPORTE) REFERENCES TRANSPORTE (ID_TRANSPORTE)," +
            "    FOREIGN KEY (ID_VEHICULO)   REFERENCES VEHICULO (ID_VEHICULO)," +
            "    FOREIGN KEY (ID_PERSONAL)   REFERENCES PERSONAL_INTERNO (ID_PERSONAL)," +
            "    FOREIGN KEY (ID_BODEGA)     REFERENCES BODEGA (ID_BODEGA)" +
            ")";
    }

    public static class Taller {
        public static final String TABLE_NAME        = "TALLER";
        public static final String COL_ID_TALLER      = "ID_TALLER";
        public static final String COL_NOMBRE_TALLER  = "NOMBRE_TALLER";
        public static final String COL_DIRECCION_TALLER = "DIRECCION_TALLER";
        public static final String COL_TELEFONO_TALLER = "TELEFONO_TALLER";
        public static final String COL_AUTORIZADO     = "AUTORIZADO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS TALLER (" +
            "    ID_TALLER           INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    NOMBRE_TALLER       TEXT    NOT NULL," +
            "    DIRECCION_TALLER    TEXT    NOT NULL," +
            "    TELEFONO_TALLER     TEXT    NOT NULL," +
            "    AUTORIZADO          INTEGER NOT NULL" +
            ")";
    }

    public static class Reparacion {
        public static final String TABLE_NAME                 = "REPARACION";
        public static final String COL_ID_REPARACION            = "ID_REPARACION";
        public static final String COL_ID_TALLER               = "ID_TALLER";
        public static final String COL_ID_VEHICULO             = "ID_VEHICULO";
        public static final String COL_FECHA_INICIO            = "FECHA_INICIO";
        public static final String COL_FECHA_FIN               = "FECHA_FIN";
        public static final String COL_DESCRIPCION_TRABAJO     = "DESCRIPCION_TRABAJO";
        public static final String COL_APTO_PARA_VENTA         = "APTO_PARA_VENTA";
        public static final String COL_REQUIERE_OTRA_REPARACION = "REQUIERE_OTRA_REPARACION";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS REPARACION (" +
            "    ID_REPARACION               INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_TALLER                   INTEGER," +
            "    ID_VEHICULO                 INTEGER," +
            "    FECHA_INICIO                TEXT    NOT NULL," +
            "    FECHA_FIN                   TEXT," +
            "    DESCRIPCION_TRABAJO         TEXT," +
            "    APTO_PARA_VENTA             INTEGER," +
            "    REQUIERE_OTRA_REPARACION    INTEGER," +
            "    FOREIGN KEY (ID_TALLER)   REFERENCES TALLER (ID_TALLER)," +
            "    FOREIGN KEY (ID_VEHICULO) REFERENCES VEHICULO (ID_VEHICULO)" +
            ")";
    }

    public static class TelefonoImportador {
        public static final String TABLE_NAME     = "TELEFONO_IMPORTADOR";
        public static final String COL_ID_TELEFONO  = "ID_TELEFONO";
        public static final String COL_ID_IMPORTADOR = "ID_IMPORTADOR";
        public static final String COL_NUMERO       = "NUMERO";
        public static final String COL_TIPO         = "TIPO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS TELEFONO_IMPORTADOR (" +
            "    ID_TELEFONO         INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_IMPORTADOR       INTEGER," +
            "    NUMERO              TEXT    NOT NULL," +
            "    TIPO                TEXT    NOT NULL," +
            "    FOREIGN KEY (ID_IMPORTADOR) REFERENCES IMPORTADOR (ID_IMPORTADOR)" +
            ")";
    }

    public static class Venta {
        public static final String TABLE_NAME   = "VENTA";
        public static final String COL_ID_VENTA   = "ID_VENTA";
        public static final String COL_ID_IMPORTADOR = "ID_IMPORTADOR";
        public static final String COL_ID_VEHICULO = "ID_VEHICULO";
        public static final String COL_FECHA_VENTA = "FECHA_VENTA";
        public static final String COL_PRECIO     = "PRECIO";
        public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS VENTA (" +
            "    ID_VENTA        INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    ID_IMPORTADOR   INTEGER," +
            "    ID_VEHICULO     INTEGER," +
            "    FECHA_VENTA     TEXT    NOT NULL," +
            "    PRECIO          REAL    NOT NULL," +
            "    FOREIGN KEY (ID_VEHICULO)   REFERENCES VEHICULO (ID_VEHICULO)," +
            "    FOREIGN KEY (ID_IMPORTADOR) REFERENCES IMPORTADOR (ID_IMPORTADOR)" +
            ")";
    }
}
