package com.ues.sistema_pdm1.utils;

public final class Constants {

    private Constants() {}

    // =========================================================
    // 1. OPCIONES DE MENÚ (IDs de OpcionCrud)
    // =========================================================

    // Módulo: Importador (100-104)
    public static final int OPCION_MENU_IMPORTADOR      = 100;
    public static final int OPCION_INSERTAR_IMPORTADOR  = 101;
    public static final int OPCION_ACTUALIZAR_IMPORTADOR = 102;
    public static final int OPCION_ELIMINAR_IMPORTADOR  = 103;
    public static final int OPCION_CONSULTAR_IMPORTADOR = 104;

    // Módulo: Vehículo (200-204)
    public static final int OPCION_MENU_VEHICULO        = 200;
    public static final int OPCION_INSERTAR_VEHICULO    = 201;
    public static final int OPCION_ACTUALIZAR_VEHICULO  = 202;
    public static final int OPCION_ELIMINAR_VEHICULO    = 203;
    public static final int OPCION_CONSULTAR_VEHICULO   = 204;

    // Módulo: Movimiento (300-304)
    public static final int OPCION_MENU_MOVIMIENTO      = 300;
    public static final int OPCION_INSERTAR_MOVIMIENTO  = 301;
    public static final int OPCION_ACTUALIZAR_MOVIMIENTO = 302;
    public static final int OPCION_ELIMINAR_MOVIMIENTO  = 303;
    public static final int OPCION_CONSULTAR_MOVIMIENTO = 304;

    // Módulo: Reparación (400-404)
    public static final int OPCION_MENU_REPARACION      = 400;
    public static final int OPCION_INSERTAR_REPARACION  = 401;
    public static final int OPCION_ACTUALIZAR_REPARACION = 402;
    public static final int OPCION_ELIMINAR_REPARACION  = 403;
    public static final int OPCION_CONSULTAR_REPARACION = 404;

    // Módulo: Venta (500-504)
    public static final int OPCION_MENU_VENTA           = 500;
    public static final int OPCION_INSERTAR_VENTA       = 501;
    public static final int OPCION_ACTUALIZAR_VENTA     = 502;
    public static final int OPCION_ELIMINAR_VENTA       = 503;
    public static final int OPCION_CONSULTAR_VENTA      = 504;

    // Módulo: Catálogos (600-604)
    public static final int OPCION_MENU_CATALOGOS       = 600;
    public static final int OPCION_INSERTAR_CATALOGO    = 601;
    public static final int OPCION_ACTUALIZAR_CATALOGO  = 602;
    public static final int OPCION_ELIMINAR_CATALOGO    = 603;
    public static final int OPCION_CONSULTAR_CATALOGO   = 604;

    // =========================================================
    // 2. ESTADOS DE VEHÍCULO
    // =========================================================

    public static final String ESTADO_VEHICULO_ALMACENADO   = "almacenado";
    public static final String ESTADO_VEHICULO_EN_REPARACION = "en_reparacion";
    public static final String ESTADO_VEHICULO_LISTO        = "listo";
    public static final String ESTADO_VEHICULO_VENDIDO      = "vendido";

    // =========================================================
    // 3. ESTADOS DE TALLER
    // =========================================================

    public static final int TALLER_NO_AUTORIZADO = 0;
    public static final int TALLER_AUTORIZADO    = 1;

    // =========================================================
    // 4. ESTADOS DE REPARACIÓN
    // =========================================================

    public static final int REPARACION_NO_APTO = 0;
    public static final int REPARACION_APTO    = 1;

    // =========================================================
    // 5. ESTADOS DE TRANSPORTE
    // =========================================================

    public static final int TRANSPORTE_ACTIVO   = 1;
    public static final int TRANSPORTE_INACTIVO = 0;

    // =========================================================
    // 6. CÓDIGOS DE REQUEST / RESULT
    // =========================================================

    public static final int REQUEST_LOGIN      = 1001;
    public static final int REQUEST_IMPORTADOR = 1002;
    public static final int REQUEST_VEHICULO   = 1003;
    public static final int REQUEST_MOVIMIENTO = 1004;
    public static final int REQUEST_REPARACION = 1005;
    public static final int REQUEST_VENTA      = 1006;
    public static final int REQUEST_CATALOGO   = 1007;

    public static final int RESULT_OK_INSERTAR  = 2001;
    public static final int RESULT_OK_ACTUALIZAR = 2002;
    public static final int RESULT_OK_ELIMINAR  = 2003;
    public static final int RESULT_CANCELAR     = 2004;

    // =========================================================
    // 7. TIPOS DE TELÉFONO
    // =========================================================

    public static final String TIPO_TELEFONO_CELULAR = "celular";
    public static final String TIPO_TELEFONO_FIJO    = "fijo";

    // =========================================================
    // 8. FORMATOS Y LÍMITES
    // =========================================================

    public static final String FORMATO_FECHA      = "yyyy-MM-dd";
    public static final String FORMATO_HORA       = "HH:mm:ss";
    public static final String FORMATO_FECHA_HORA = "yyyy-MM-dd HH:mm:ss";

    public static final int ANIO_MINIMO_VEHICULO = 2021;
    public static final int LONGITUD_NUI         = 14;
    public static final int LONGITUD_VIN         = 17;

    // =========================================================
    // 9. MENSAJES DEFAULT
    // =========================================================

    public static final String MSG_SESION_EXPIRADA   = "Sesión expirada. Por favor, inicie sesión nuevamente.";
    public static final String MSG_SIN_PERMISO       = "No tiene permiso para realizar esta acción.";
    public static final String MSG_OPERACION_EXITOSA = "Operación realizada exitosamente.";
    public static final String MSG_OPERACION_FALLIDA = "Error al realizar la operación.";
    public static final String MSG_CAMPO_REQUERIDO   = "Este campo es requerido.";
    public static final String MSG_DATOS_INVALIDOS   = "Los datos ingresados no son válidos.";

    // =========================================================
    // 10. EXTRA KEYS PARA INTENTS
    // =========================================================

    public static final String EXTRA_ID        = "id";
    public static final String EXTRA_OBJETO    = "objeto";
    public static final String EXTRA_MODULO    = "modulo";
    public static final String EXTRA_OPERACION = "operacion";
    public static final String EXTRA_USUARIO   = "usuario";

    // =========================================================
    // 11. SHARED PREFERENCES KEYS
    // =========================================================

    public static final String PREF_USUARIO          = "usuario";
    public static final String PREF_ULTIMA_SESION    = "ultima_sesion";
    public static final String PREF_RECORDAR_USUARIO = "recordar_usuario";

    // =========================================================
    // 12. LOG TAGS
    // =========================================================

    public static final String TAG_DATABASE = "DatabaseHelper";
    public static final String TAG_DAO      = "GenericDAO";
    public static final String TAG_SESSION  = "SessionManager";
    public static final String TAG_ACTIVITY = "Activity";
}
