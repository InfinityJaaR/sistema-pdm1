package com.ues.sistema_pdm1.utils;

import android.content.Context;
import android.util.Log;

import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.AccesoUsuario;
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.models.Departamento;
import com.ues.sistema_pdm1.models.Distrito;
import com.ues.sistema_pdm1.models.Marca;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Municipio;
import com.ues.sistema_pdm1.models.OpcionCrud;
import com.ues.sistema_pdm1.models.Pais;
import com.ues.sistema_pdm1.models.DetalleDesperfecto;
import com.ues.sistema_pdm1.models.FotoDesperfecto;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.models.Reparacion;
import com.ues.sistema_pdm1.models.Seccion;
import com.ues.sistema_pdm1.models.Taller;
import com.ues.sistema_pdm1.models.TelefonoImportador;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.models.Venta;
import com.ues.sistema_pdm1.models.TipoDesperfecto;
import com.ues.sistema_pdm1.models.TipoTransporte;
import com.ues.sistema_pdm1.models.TipoVehiculo;
import com.ues.sistema_pdm1.models.Usuario;

public class LlenarBDGpo02 {

    private static final String TAG = "LlenarBDGpo02";

    public static void llenarDatosIniciales(Context context) {

        GenericDAO<Usuario>        usuarioDAO        = new GenericDAO<>(context, Usuario.class,        "usuario");
        GenericDAO<OpcionCrud>     opcionDAO         = new GenericDAO<>(context, OpcionCrud.class,     "opcioncrud");
        GenericDAO<AccesoUsuario>  accesoDAO         = new GenericDAO<>(context, AccesoUsuario.class,  "accesousuario");
        GenericDAO<Pais>           paisDAO           = new GenericDAO<>(context, Pais.class,           "pais");
        GenericDAO<Departamento>   departamentoDAO   = new GenericDAO<>(context, Departamento.class,   "departamento");
        GenericDAO<Municipio>      municipioDAO      = new GenericDAO<>(context, Municipio.class,      "municipio");
        GenericDAO<Distrito>       distritoDAO       = new GenericDAO<>(context, Distrito.class,       "distrito");
        GenericDAO<Marca>          marcaDAO          = new GenericDAO<>(context, Marca.class,          "marca");
        GenericDAO<Modelo>         modeloDAO         = new GenericDAO<>(context, Modelo.class,         "modelo");
        GenericDAO<TipoVehiculo>   tipoVehiculoDAO   = new GenericDAO<>(context, TipoVehiculo.class,   "tipo_vehiculo");
        GenericDAO<TipoTransporte> tipoTransporteDAO = new GenericDAO<>(context, TipoTransporte.class, "tipo_transporte");
        GenericDAO<TipoDesperfecto> tipoDesperfectoDAO = new GenericDAO<>(context, TipoDesperfecto.class, "tipo_desperfecto");
        GenericDAO<Taller>         tallerDAO         = new GenericDAO<>(context, Taller.class,         "taller");
        GenericDAO<PersonalInterno> personalDAO      = new GenericDAO<>(context, PersonalInterno.class, "personal_interno");
        GenericDAO<Bodega>              bodegaDAO         = new GenericDAO<>(context, Bodega.class,              "bodega");
        GenericDAO<Seccion>             seccionDAO        = new GenericDAO<>(context, Seccion.class,             "seccion");
        GenericDAO<Importador>          importadorDAO     = new GenericDAO<>(context, Importador.class,          "importador");
        GenericDAO<TelefonoImportador>  telefonoDAO       = new GenericDAO<>(context, TelefonoImportador.class,  "telefono_importador");
        GenericDAO<Importacion>         importacionDAO    = new GenericDAO<>(context, Importacion.class,         "importacion");
        GenericDAO<Transporte>          transporteDAO     = new GenericDAO<>(context, Transporte.class,          "transporte");
        GenericDAO<Vehiculo>            vehiculoDAO       = new GenericDAO<>(context, Vehiculo.class,            "vehiculo");
        GenericDAO<Movimiento>          movimientoDAO     = new GenericDAO<>(context, Movimiento.class,          "movimiento");
        GenericDAO<DetalleDesperfecto>  detalleDAO        = new GenericDAO<>(context, DetalleDesperfecto.class,  "detalle_desperfecto");
        GenericDAO<FotoDesperfecto>     fotoDAO           = new GenericDAO<>(context, FotoDesperfecto.class,     "foto_desperfecto");
        GenericDAO<Reparacion>          reparacionDAO     = new GenericDAO<>(context, Reparacion.class,          "reparacion");
        GenericDAO<Venta>               ventaDAO          = new GenericDAO<>(context, Venta.class,               "venta");

        try {

            if (usuarioDAO.contar() == 0) {
                usuarioDAO.insertar(new Usuario("1", "admin",      "adm01"));
                usuarioDAO.insertar(new Usuario("2", "importador", "imp01"));
                usuarioDAO.insertar(new Usuario("3", "personal",   "per01"));
                Log.d(TAG, "Usuarios insertados");
            }

            if (opcionDAO.contar() == 0) {
                opcionDAO.insertar(new OpcionCrud("100", "Importador",       0));
                opcionDAO.insertar(new OpcionCrud("200", "Vehículo",         0));
                opcionDAO.insertar(new OpcionCrud("210", "Importación",      0));
                opcionDAO.insertar(new OpcionCrud("300", "Bodega",           0));
                opcionDAO.insertar(new OpcionCrud("310", "Sección",          0));
                opcionDAO.insertar(new OpcionCrud("400", "Movimiento",       0));
                opcionDAO.insertar(new OpcionCrud("410", "Transporte",       0));
                opcionDAO.insertar(new OpcionCrud("500", "Reparación",       0));
                opcionDAO.insertar(new OpcionCrud("510", "Taller",           0));
                opcionDAO.insertar(new OpcionCrud("600", "Venta",            0));
                opcionDAO.insertar(new OpcionCrud("610", "Desperfecto",      0));
                opcionDAO.insertar(new OpcionCrud("620", "Personal Interno", 0));
                opcionDAO.insertar(new OpcionCrud("630", "Marca",            0));
                Log.d(TAG, "13 opciones de menú insertadas");
            }

            if (accesoDAO.contar() == 0) {
                // ADMIN
                String[] todasOpciones = {
                    "100","200","210","300","310",
                    "400","410","500","510","600",
                    "610","620","630"
                };
                for (String opcion : todasOpciones) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "1"));
                }
                // IMPORTADOR
                for (String opcion : new String[]{"100","200","210","610"}) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "2"));
                }
                // PERSONAL
                for (String opcion : new String[]{"300","310","400","410","620"}) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "3"));
                }
                Log.d(TAG, "Matriz de accesos insertada");
            }

            if (paisDAO.contar() == 0) {
                // PAÍS
                paisDAO.insertar(new Pais(0, "El Salvador"));

                // DEPARTAMENTOS
                departamentoDAO.insertar(new Departamento(0, 1, "Ahuachapán"));
                departamentoDAO.insertar(new Departamento(0, 1, "Santa Ana"));
                departamentoDAO.insertar(new Departamento(0, 1, "Sonsonate"));
                departamentoDAO.insertar(new Departamento(0, 1, "La Libertad"));
                departamentoDAO.insertar(new Departamento(0, 1, "San Salvador"));
                departamentoDAO.insertar(new Departamento(0, 1, "Cuscatlán"));
                departamentoDAO.insertar(new Departamento(0, 1, "La Paz"));
                departamentoDAO.insertar(new Departamento(0, 1, "Cabañas"));
                departamentoDAO.insertar(new Departamento(0, 1, "San Vicente"));
                departamentoDAO.insertar(new Departamento(0, 1, "Usulután"));
                departamentoDAO.insertar(new Departamento(0, 1, "Morazán"));
                departamentoDAO.insertar(new Departamento(0, 1, "La Unión"));
                departamentoDAO.insertar(new Departamento(0, 1, "Chalatenango"));
                departamentoDAO.insertar(new Departamento(0, 1, "San Miguel"));

                // MUNICIPIOS
                municipioDAO.insertar(new Municipio(0, 1,  "Ahuachapán"));
                municipioDAO.insertar(new Municipio(0, 2,  "Santa Ana"));
                municipioDAO.insertar(new Municipio(0, 3,  "Sonsonate"));
                municipioDAO.insertar(new Municipio(0, 4,  "Santa Tecla"));
                municipioDAO.insertar(new Municipio(0, 5,  "San Salvador"));
                municipioDAO.insertar(new Municipio(0, 6,  "Cojutepeque"));
                municipioDAO.insertar(new Municipio(0, 7,  "Zacatecoluca"));
                municipioDAO.insertar(new Municipio(0, 8,  "Sensuntepeque"));
                municipioDAO.insertar(new Municipio(0, 9,  "San Vicente"));
                municipioDAO.insertar(new Municipio(0, 10, "Usulután"));
                municipioDAO.insertar(new Municipio(0, 11, "San Francisco Gotera"));
                municipioDAO.insertar(new Municipio(0, 12, "La Unión"));
                municipioDAO.insertar(new Municipio(0, 13, "Nueva Concepción"));
                municipioDAO.insertar(new Municipio(0, 14, "San Miguel"));

                // DISTRITOS
                distritoDAO.insertar(new Distrito(0, 1,  "Ahuachapán Centro"));
                distritoDAO.insertar(new Distrito(0, 2,  "Santa Ana Centro"));
                distritoDAO.insertar(new Distrito(0, 3,  "Sonsonate Centro"));
                distritoDAO.insertar(new Distrito(0, 4,  "Santa Tecla Centro"));
                distritoDAO.insertar(new Distrito(0, 5,  "San Salvador Centro"));
                distritoDAO.insertar(new Distrito(0, 6,  "Cojutepeque Centro"));
                distritoDAO.insertar(new Distrito(0, 7,  "Zacatecoluca Centro"));
                distritoDAO.insertar(new Distrito(0, 8,  "Sensuntepeque Centro"));
                distritoDAO.insertar(new Distrito(0, 9,  "San Vicente Centro"));
                distritoDAO.insertar(new Distrito(0, 10, "Usulután Centro"));
                distritoDAO.insertar(new Distrito(0, 11, "Gotera Centro"));
                distritoDAO.insertar(new Distrito(0, 12, "La Unión Centro"));
                distritoDAO.insertar(new Distrito(0, 13, "Nueva Concepción Centro"));
                distritoDAO.insertar(new Distrito(0, 14, "San Miguel Centro"));

                // MARCAS
                marcaDAO.insertar(new Marca(0, "Toyota"));
                marcaDAO.insertar(new Marca(0, "Honda"));
                marcaDAO.insertar(new Marca(0, "Ford"));
                marcaDAO.insertar(new Marca(0, "Chevrolet"));
                marcaDAO.insertar(new Marca(0, "Nissan"));
                marcaDAO.insertar(new Marca(0, "Hyundai"));
                marcaDAO.insertar(new Marca(0, "KIA"));
                marcaDAO.insertar(new Marca(0, "BMW"));
                marcaDAO.insertar(new Marca(0, "Mercedes Benz"));

                // MODELOS
                modeloDAO.insertar(new Modelo(0, 1, "Corolla"));
                modeloDAO.insertar(new Modelo(0, 1, "Camry"));
                modeloDAO.insertar(new Modelo(0, 1, "Hilux"));
                modeloDAO.insertar(new Modelo(0, 2, "Civic"));
                modeloDAO.insertar(new Modelo(0, 2, "Accord"));
                modeloDAO.insertar(new Modelo(0, 2, "CR-V"));
                modeloDAO.insertar(new Modelo(0, 3, "Fiesta"));
                modeloDAO.insertar(new Modelo(0, 3, "Fusion"));
                modeloDAO.insertar(new Modelo(0, 4, "Spark"));
                modeloDAO.insertar(new Modelo(0, 4, "Silverado"));
                modeloDAO.insertar(new Modelo(0, 5, "Sentra"));
                modeloDAO.insertar(new Modelo(0, 5, "Frontier"));
                modeloDAO.insertar(new Modelo(0, 6, "Tucson"));
                modeloDAO.insertar(new Modelo(0, 6, "Santa Fe"));
                modeloDAO.insertar(new Modelo(0, 7, "Sportage"));
                modeloDAO.insertar(new Modelo(0, 7, "Sorento"));
                modeloDAO.insertar(new Modelo(0, 8, "Serie 3"));
                modeloDAO.insertar(new Modelo(0, 9, "Clase C"));

                // TIPOS DE VEHÍCULO
                tipoVehiculoDAO.insertar(new TipoVehiculo(0, "Sedán"));
                tipoVehiculoDAO.insertar(new TipoVehiculo(0, "SUV"));
                tipoVehiculoDAO.insertar(new TipoVehiculo(0, "Pickup"));
                tipoVehiculoDAO.insertar(new TipoVehiculo(0, "Minivan"));
                tipoVehiculoDAO.insertar(new TipoVehiculo(0, "Hatchback"));

                // TIPOS DE TRANSPORTE
                tipoTransporteDAO.insertar(new TipoTransporte(0, "Camión Plataforma", 8));
                tipoTransporteDAO.insertar(new TipoTransporte(0, "Camión Cerrado",    6));
                tipoTransporteDAO.insertar(new TipoTransporte(0, "Remolque",         10));

                // TIPOS DE DESPERFECTO
                tipoDesperfectoDAO.insertar(new TipoDesperfecto(0, "Rayón",        "Rayones en carrocería"));
                tipoDesperfectoDAO.insertar(new TipoDesperfecto(0, "Golpe",        "Deformación en chapa"));
                tipoDesperfectoDAO.insertar(new TipoDesperfecto(0, "Cristal roto", "Ventana o parabrisas roto"));
                tipoDesperfectoDAO.insertar(new TipoDesperfecto(0, "Mecánico",     "Defecto en motor o sistema"));

                // TALLERES
                tallerDAO.insertar(new Taller(0, "Taller Autorizado A", "Av. Principal 123",  "2234-5678", 1));
                tallerDAO.insertar(new Taller(0, "Taller B",            "Calle 5 Oriente",    "2234-5679", 0));
                tallerDAO.insertar(new Taller(0, "Taller C",            "Zona Industrial",    "2234-5680", 1));

                // PERSONAL INTERNO
                personalDAO.insertar(new PersonalInterno(0, "Juan",   "Pérez",    "Empleado"));
                personalDAO.insertar(new PersonalInterno(0, "María",  "González", "Supervisor"));
                personalDAO.insertar(new PersonalInterno(0, "Carlos", "López",    "Empleado"));

                Log.d(TAG, "Catálogos básicos insertados");
            }

            if (bodegaDAO.contar() == 0) {
                bodegaDAO.insertar(new Bodega(0,  5, "Bodega Central",     "Bulevar Los Héroes Km 2, San Salvador"));
                bodegaDAO.insertar(new Bodega(0,  2, "Bodega Occidente",   "Carretera a Santa Ana Km 32"));
                bodegaDAO.insertar(new Bodega(0, 14, "Bodega Oriente",     "Carretera Panamericana Km 138, San Miguel"));
                bodegaDAO.insertar(new Bodega(0,  7, "Bodega Sur",         "Carretera al Puerto Km 56, Zacatecoluca"));
                bodegaDAO.insertar(new Bodega(0,  8, "Bodega Paracentral", "Carretera a Sensuntepeque Km 88"));
                bodegaDAO.insertar(new Bodega(0,  3, "Bodega Costera",     "Carretera Litoral Km 72, Sonsonate"));
                Log.d(TAG, "Bodegas insertadas");
            }

            if (seccionDAO.contar() == 0) {
                // Bodega 1 — Central
                seccionDAO.insertar(new Seccion(0, 1, 1, 50, 0));
                seccionDAO.insertar(new Seccion(0, 1, 2, 50, 0));
                seccionDAO.insertar(new Seccion(0, 1, 3, 50, 0));
                // Bodega 2 — Occidente
                seccionDAO.insertar(new Seccion(0, 2, 1, 50, 0));
                seccionDAO.insertar(new Seccion(0, 2, 2, 50, 0));
                seccionDAO.insertar(new Seccion(0, 2, 3, 50, 0));
                // Bodega 3 — Oriente
                seccionDAO.insertar(new Seccion(0, 3, 1, 50, 0));
                seccionDAO.insertar(new Seccion(0, 3, 2, 50, 0));
                seccionDAO.insertar(new Seccion(0, 3, 3, 50, 0));
                // Bodega 4 — Sur
                seccionDAO.insertar(new Seccion(0, 4, 1, 50, 0));
                seccionDAO.insertar(new Seccion(0, 4, 2, 50, 0));
                seccionDAO.insertar(new Seccion(0, 4, 3, 50, 0));
                // Bodega 5 — Paracentral
                seccionDAO.insertar(new Seccion(0, 5, 1, 50, 0));
                seccionDAO.insertar(new Seccion(0, 5, 2, 50, 0));
                seccionDAO.insertar(new Seccion(0, 5, 3, 50, 0));
                // Bodega 6 — Costera
                seccionDAO.insertar(new Seccion(0, 6, 1, 50, 0));
                seccionDAO.insertar(new Seccion(0, 6, 2, 50, 0));
                seccionDAO.insertar(new Seccion(0, 6, 3, 50, 0));
                Log.d(TAG, "Secciones insertadas");
            }

            if (importadorDAO.contar() == 0) {
                importadorDAO.insertar(new Importador(0,  5, "Carlos",  "Mendoza",   null, "M", "Colonia Escalón, San Salvador",              "1985-03-15", "cmendoza@gmail.com",    "01234567890001", "Luis Ramírez"));
                importadorDAO.insertar(new Importador(0,  2, "María",   "González",  null, "F", "Residencial Santa Elena, Antiguo Cuscatlán", "1990-07-22", "mgonzalez@hotmail.com", "02345678901234", "Ana Flores"));
                importadorDAO.insertar(new Importador(0, 14, "Roberto", "Hernández", null, "M", "Colonia San Benito, San Salvador",           "1978-11-30", "rhernandez@yahoo.com",  "03456789012345", "Pedro Castillo"));
                Log.d(TAG, "Importadores insertados");
            }

            if (telefonoDAO.contar() == 0) {
                telefonoDAO.insertar(new TelefonoImportador(0, 1, "7890-1234", "Celular"));
                telefonoDAO.insertar(new TelefonoImportador(0, 1, "2222-5678", "Casa"));
                telefonoDAO.insertar(new TelefonoImportador(0, 2, "7654-3210", "Celular"));
                telefonoDAO.insertar(new TelefonoImportador(0, 2, "2345-6789", "Oficina"));
                telefonoDAO.insertar(new TelefonoImportador(0, 3, "7777-8888", "Celular"));
                telefonoDAO.insertar(new TelefonoImportador(0, 3, "2290-5555", "Casa"));
                Log.d(TAG, "Teléfonos de importador insertados");
            }

            if (importacionDAO.contar() == 0) {
                importacionDAO.insertar(new Importacion(0, 1, "2024-01-15"));
                importacionDAO.insertar(new Importacion(0, 1, "2024-02-20"));
                importacionDAO.insertar(new Importacion(0, 2, "2024-03-10"));
                importacionDAO.insertar(new Importacion(0, 3, "2024-04-05"));
                Log.d(TAG, "Importaciones insertadas");
            }

            if (transporteDAO.contar() == 0) {
                transporteDAO.insertar(new Transporte(0, 1, "P-123-456", "Camión de traslado principal"));
                transporteDAO.insertar(new Transporte(0, 2, "P-789-012", "Camión cerrado de carga"));
                transporteDAO.insertar(new Transporte(0, 3, "P-345-678", "Remolque de gran capacidad"));
                Log.d(TAG, "Transportes insertados");
            }

            if (vehiculoDAO.contar() == 0) {
                vehiculoDAO.insertar(new Vehiculo(0, 1, 1,  1, 1,  "JT2S3FEJ3M1234567", 2022, "Blanco",   "en bodega")); // Toyota Corolla
                vehiculoDAO.insertar(new Vehiculo(0, 1, 4,  1, 2,  "1HGCR2F39KA123456", 2023, "Negro",    "en bodega")); // Honda Civic
                vehiculoDAO.insertar(new Vehiculo(0, 2, 12, 3, 4,  "1N4AL3AP4LC123456", 2021, "Gris",     "en bodega")); // Nissan Frontier
                vehiculoDAO.insertar(new Vehiculo(0, 3, 13, 2, 7,  "KM8J33A45MU123456", 2023, "Azul",     "en bodega")); // Hyundai Tucson
                vehiculoDAO.insertar(new Vehiculo(0, 4, 17, 1, 10, "WBA3B5C58MF123456", 2024, "Plateado", "en bodega")); // BMW Serie 3
                vehiculoDAO.insertar(new Vehiculo(0, 1, 9,  5, 3,  "1G1BC5SM5J7123456", 2023, "Rojo",     "en bodega")); // Chevrolet Spark
                vehiculoDAO.insertar(new Vehiculo(0, 2, 2,  1, 5,  "4T1BF1FK5GU123456", 2022, "Blanco",   "en bodega")); // Toyota Camry
                vehiculoDAO.insertar(new Vehiculo(0, 3, 6,  2, 6,  "5J6RM4H53EL123456", 2024, "Negro",    "en bodega")); // Honda CR-V
                vehiculoDAO.insertar(new Vehiculo(0, 4, 8,  1, 8,  "3FA6P0HD5GR123456", 2023, "Plateado", "en bodega")); // Ford Fusion
                vehiculoDAO.insertar(new Vehiculo(0, 2, 15, 2, 11, "KNDPC3AC5F7123456", 2022, "Azul",     "en bodega")); // KIA Sportage
                Log.d(TAG, "Vehículos insertados");
            }

            if (movimientoDAO.contar() == 0) {
                movimientoDAO.insertar(new Movimiento(0, 1, 1, 1,  1, "entrada", "2024-01-15", "Ingreso a bodega central"));   // v1 Corolla
                movimientoDAO.insertar(new Movimiento(0, 1, 1, 2,  1, "entrada", "2024-01-16", "Ingreso a bodega central"));   // v2 Civic
                movimientoDAO.insertar(new Movimiento(0, 2, 2, 3,  2, "entrada", "2024-02-20", "Ingreso a bodega occidente")); // v3 Frontier
                movimientoDAO.insertar(new Movimiento(0, 3, 3, 4,  3, "entrada", "2024-03-10", "Ingreso a bodega oriente"));   // v4 Tucson
                movimientoDAO.insertar(new Movimiento(0, 1, 3, 5,  4, "entrada", "2024-04-05", "Ingreso a bodega sur"));       // v5 BMW
                movimientoDAO.insertar(new Movimiento(0, 2, 1, 6,  1, "entrada", "2024-01-17", "Ingreso a bodega central"));   // v6 Spark
                movimientoDAO.insertar(new Movimiento(0, 3, 3, 7,  2, "entrada", "2024-02-21", "Ingreso a bodega occidente")); // v7 Camry
                movimientoDAO.insertar(new Movimiento(0, 2, 1, 8,  2, "entrada", "2024-03-11", "Ingreso a bodega occidente")); // v8 CR-V
                movimientoDAO.insertar(new Movimiento(0, 1, 2, 9,  3, "entrada", "2024-04-06", "Ingreso a bodega oriente"));   // v9 Fusion
                movimientoDAO.insertar(new Movimiento(0, 3, 3, 10, 4, "entrada", "2024-05-02", "Ingreso a bodega sur"));       // v10 Sportage
                Log.d(TAG, "Movimientos insertados");
            }

            if (detalleDAO.contar() == 0) {
                detalleDAO.insertar(new DetalleDesperfecto(0, 1, 2, "Golpe leve en puerta delantera derecha", "2024-01-15"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 2, 1, "Rayón superficial en capó",              "2024-01-16"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 3, 3, "Vidrio trasero con fisura",              "2024-02-20"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 4, 4, "Falla leve en sistema de frenos",        "2024-03-10"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 9, 4, "Falla en sistema de suspensión",         "2024-04-06"));
                Log.d(TAG, "Detalles de desperfecto insertados");
            }

            if (fotoDAO.contar() == 0) {
                fotoDAO.insertar(new FotoDesperfecto(0, 1, "/fotos/v1/golpe_puerta.jpg",   "2024-01-15"));
                fotoDAO.insertar(new FotoDesperfecto(0, 2, "/fotos/v2/rayon_capo.jpg",     "2024-01-16"));
                fotoDAO.insertar(new FotoDesperfecto(0, 3, "/fotos/v3/vidrio_trasero.jpg", "2024-02-20"));
                fotoDAO.insertar(new FotoDesperfecto(0, 4, "/fotos/v4/frenos.jpg",         "2024-03-10"));
                fotoDAO.insertar(new FotoDesperfecto(0, 5, "/fotos/v9/suspension.jpg",     "2024-04-06"));
                Log.d(TAG, "Fotos de desperfecto insertadas");
            }

            if (reparacionDAO.contar() == 0) {
                reparacionDAO.insertar(new Reparacion(0, 1, 1, "2024-01-20", "2024-01-25", "Reparación de golpe en puerta",   0, 0));
                reparacionDAO.insertar(new Reparacion(0, 2, 2, "2024-01-22", "2024-02-01", "Pulido y pintura por rayones",    0, 0));
                reparacionDAO.insertar(new Reparacion(0, 3, 3, "2024-02-25", "2024-03-05", "Reemplazo de vidrio trasero",     0, 0));
                reparacionDAO.insertar(new Reparacion(0, 7, 1, "2024-03-01", "2024-03-10", "Revisión general previa a venta", 0, 0));
                reparacionDAO.actualizar(new Reparacion(4, 7, 1, "2024-03-01", "2024-03-10", "Revisión general previa a venta", 1, 0));
                reparacionDAO.insertar(new Reparacion(0, 9, 2, "2024-04-15", "2024-04-25", "Revisión mecánica completa",      0, 0));
                Log.d(TAG, "Reparaciones insertadas");
            }

            if (ventaDAO.contar() == 0) {
                ventaDAO.insertar(new Venta(0, 1, 4,  "2024-03-15", 22000.00));
                ventaDAO.insertar(new Venta(0, 2, 5,  "2024-04-10", 45000.00));
                ventaDAO.insertar(new Venta(0, 3, 10, "2024-05-05", 28000.00));
                Log.d(TAG, "Ventas insertadas");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error llenando BD", e);
        }
    }
}
