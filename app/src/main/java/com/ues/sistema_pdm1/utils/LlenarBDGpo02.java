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

            // =================================================================
            // SECCIÓN 1: USUARIOS
            // IDs son String porque ID_USUARIO es TEXT en BD
            // =================================================================
            if (usuarioDAO.contar() == 0) {
                usuarioDAO.insertar(new Usuario("1", "admin",      "adm01"));
                usuarioDAO.insertar(new Usuario("2", "importador", "imp01"));
                usuarioDAO.insertar(new Usuario("3", "personal",   "per01"));
                Log.d(TAG, "Usuarios insertados");
            }

            // =================================================================
            // SECCIÓN 2: OPCIONES DE MENÚ PRINCIPAL (15 módulos)
            // Cada ID representa acceso al módulo; CRUD interno se define en FASE 4
            // =================================================================
            if (opcionDAO.contar() == 0) {
                opcionDAO.insertar(new OpcionCrud("100", "Importador",        0));
                opcionDAO.insertar(new OpcionCrud("200", "Vehículo",          0));
                opcionDAO.insertar(new OpcionCrud("210", "Importación",       0));
                opcionDAO.insertar(new OpcionCrud("300", "Bodega",            0));
                opcionDAO.insertar(new OpcionCrud("310", "Sección",           0));
                opcionDAO.insertar(new OpcionCrud("400", "Movimiento",        0));
                opcionDAO.insertar(new OpcionCrud("410", "Transporte",        0));
                opcionDAO.insertar(new OpcionCrud("500", "Reparación",        0));
                opcionDAO.insertar(new OpcionCrud("510", "Taller",            0));
                opcionDAO.insertar(new OpcionCrud("600", "Venta",             0));
                opcionDAO.insertar(new OpcionCrud("610", "Desperfecto",       0));
                opcionDAO.insertar(new OpcionCrud("620", "Personal Interno",  0));
                opcionDAO.insertar(new OpcionCrud("630", "Marca",             0));
                opcionDAO.insertar(new OpcionCrud("640", "Tipo de Transporte",0));
                opcionDAO.insertar(new OpcionCrud("650", "Tipo de Vehículo",  0));
                Log.d(TAG, "15 opciones de menú insertadas");
            }

            // =================================================================
            // SECCIÓN 3: MATRIZ DE ACCESOS
            // Solo se verifica acceso al módulo (X00), no al CRUD interno
            // =================================================================
            if (accesoDAO.contar() == 0) {

                // ADMIN (id="1"): acceso a los 15 módulos
                String[] todasOpciones = {
                    "100","200","210","300","310",
                    "400","410","500","510","600",
                    "610","620","630","640","650"
                };
                for (String opcion : todasOpciones) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "1"));
                }

                // IMPORTADOR (id="2"): Importador, Importación + catálogos básicos
                for (String opcion : new String[]{"100","210","630","640","650"}) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "2"));
                }

                // PERSONAL (id="3"): Movimiento, Transporte + catálogos básicos
                for (String opcion : new String[]{"400","410","630","640","650"}) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "3"));
                }

                Log.d(TAG, "Matriz de accesos insertada");
            }

            // =================================================================
            // SECCIÓN 4: CATÁLOGOS BÁSICOS
            // id=0 indica que GenericDAO asigna el ID automáticamente
            // =================================================================
            if (paisDAO.contar() == 0) {

                // PAÍS
                paisDAO.insertar(new Pais(0, "El Salvador"));

                // DEPARTAMENTOS (14 de El Salvador)
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

                // MUNICIPIOS (1 por departamento, IDs 1-14)
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

                // DISTRITOS (1 por municipio, IDs 1-14)
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

                // MODELOS (idMarca corresponde al orden de inserción de Marcas)
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
                tipoTransporteDAO.insertar(new TipoTransporte(0, "Remolque",          10));

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
                personalDAO.insertar(new PersonalInterno(0, "Juan",   "Pérez",    "Almacenero"));
                personalDAO.insertar(new PersonalInterno(0, "María",  "González", "Supervisor"));
                personalDAO.insertar(new PersonalInterno(0, "Carlos", "López",    "Operario"));

                Log.d(TAG, "Catálogos básicos insertados");
            }

            // =================================================================
            // SECCIÓN 5: BODEGAS
            // Guardia independiente: se ejecuta aunque los catálogos ya existan.
            // idDistrito apunta a los distritos insertados en Sección 4 (IDs 1-14).
            //   5 = San Salvador Centro, 2 = Santa Ana Centro, 14 = San Miguel Centro
            //   7 = Zacatecoluca Centro, 8 = Sensuntepeque Centro, 3 = Sonsonate Centro
            // =================================================================
            if (bodegaDAO.contar() == 0) {
                bodegaDAO.insertar(new Bodega(0,  5, "Bodega Central",     "Bulevar Los Héroes Km 2, San Salvador", 150, 0));
                bodegaDAO.insertar(new Bodega(0,  2, "Bodega Occidente",   "Carretera a Santa Ana Km 32",           150, 0));
                bodegaDAO.insertar(new Bodega(0, 14, "Bodega Oriente",     "Carretera Panamericana Km 138, San Miguel", 150, 0));
                bodegaDAO.insertar(new Bodega(0,  7, "Bodega Sur",         "Carretera al Puerto Km 56, Zacatecoluca",  100, 0));
                bodegaDAO.insertar(new Bodega(0,  8, "Bodega Paracentral", "Carretera a Sensuntepeque Km 88",       100, 0));
                bodegaDAO.insertar(new Bodega(0,  3, "Bodega Costera",     "Carretera Litoral Km 72, Sonsonate",    100, 0));
                Log.d(TAG, "Bodegas insertadas");
            }

            // =================================================================
            // SECCIÓN 6: SECCIONES
            // Guardia independiente: se ejecuta aunque bodegas ya existan.
            // 3 secciones (niveles 1-3) por cada una de las 6 bodegas.
            // Bodega IDs 1-6 son los asignados automáticamente en Sección 5.
            // capacidadMaxima = 50, capacidadActual = 0 (ningún vehículo aún).
            // =================================================================
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

            // =================================================================
            // SECCIÓN 7: IMPORTADORES
            // idDistrito: 5=San Salvador Centro, 2=Santa Ana Centro, 14=San Miguel Centro
            // =================================================================
            if (importadorDAO.contar() == 0) {
                importadorDAO.insertar(new Importador(0, 5, "NUI-001", "Carlos",  "Mendoza",   "Colonia Escalón, San Salvador",                  "cmendoza@gmail.com",   "activo", 100, 0));
                importadorDAO.insertar(new Importador(0, 2, "NUI-002", "María",   "González",  "Residencial Santa Elena, Antiguo Cuscatlán",     "mgonzalez@hotmail.com","activo",  80, 0));
                importadorDAO.insertar(new Importador(0, 14,"NUI-003", "Roberto", "Hernández", "Colonia San Benito, San Salvador",               "rhernandez@yahoo.com", "activo",  60, 0));
                Log.d(TAG, "Importadores insertados");
            }

            // =================================================================
            // SECCIÓN 8: TELÉFONOS DE IMPORTADOR
            // idImportador: 1=Carlos, 2=María, 3=Roberto (auto-asignados en Sección 7)
            // =================================================================
            if (telefonoDAO.contar() == 0) {
                telefonoDAO.insertar(new TelefonoImportador(0, 1, "7890-1234", "Celular"));
                telefonoDAO.insertar(new TelefonoImportador(0, 1, "2222-5678", "Casa"));
                telefonoDAO.insertar(new TelefonoImportador(0, 2, "7654-3210", "Celular"));
                telefonoDAO.insertar(new TelefonoImportador(0, 2, "2345-6789", "Oficina"));
                telefonoDAO.insertar(new TelefonoImportador(0, 3, "7777-8888", "Celular"));
                Log.d(TAG, "Teléfonos de importador insertados");
            }

            // =================================================================
            // SECCIÓN 9: IMPORTACIONES
            // idImportador: 1=Carlos (2 importaciones), 2=María, 3=Roberto
            // =================================================================
            if (importacionDAO.contar() == 0) {
                importacionDAO.insertar(new Importacion(0, 1, "2024-01-15"));
                importacionDAO.insertar(new Importacion(0, 1, "2024-02-20"));
                importacionDAO.insertar(new Importacion(0, 2, "2024-03-10"));
                importacionDAO.insertar(new Importacion(0, 3, "2024-04-05"));
                Log.d(TAG, "Importaciones insertadas");
            }

            // =================================================================
            // SECCIÓN 10: TRANSPORTES
            // idTipoTransporte: 1=Camión Plataforma(cap 8), 2=Camión Cerrado(cap 6), 3=Remolque(cap 10)
            // =================================================================
            if (transporteDAO.contar() == 0) {
                transporteDAO.insertar(new Transporte(0, 1, "P-123-456", "Camión de traslado principal", 8,  0));
                transporteDAO.insertar(new Transporte(0, 2, "P-789-012", "Camión cerrado de carga",      6,  0));
                transporteDAO.insertar(new Transporte(0, 3, "P-345-678", "Remolque de gran capacidad",   10, 0));
                Log.d(TAG, "Transportes insertados");
            }

            // =================================================================
            // SECCIÓN 11: VEHÍCULOS
            // idImportacion: 1-4 (auto-asignados Sección 9)
            // idModelo: 1=Corolla, 4=Civic, 12=Frontier, 13=Tucson, 17=Serie 3
            // idTipoVehiculo: 1=Sedán, 2=SUV, 3=Pickup
            // idSeccion: 1,2,4,7,10 (distribuidas en distintas bodegas)
            // Trigger TRG_ANIO_VEHICULO bloquea si anio <= 2020 → todos >= 2021
            // Trigger TRG_SECCION_INCREMENTAR suma +1 a capacidadActual al insertar
            // =================================================================
            if (vehiculoDAO.contar() == 0) {
                vehiculoDAO.insertar(new Vehiculo(0, 1, 1,  1, 1,  "VIN2024TOY001", 2022, "Blanco",  "en bodega"));
                vehiculoDAO.insertar(new Vehiculo(0, 1, 4,  1, 2,  "VIN2024HON001", 2023, "Negro",   "en bodega"));
                vehiculoDAO.insertar(new Vehiculo(0, 2, 12, 3, 4,  "VIN2024NIS001", 2021, "Gris",    "en bodega"));
                vehiculoDAO.insertar(new Vehiculo(0, 3, 13, 2, 7,  "VIN2024HYU001", 2023, "Azul",    "en bodega"));
                vehiculoDAO.insertar(new Vehiculo(0, 4, 17, 1, 10, "VIN2024BMW001", 2024, "Plateado", "en bodega"));
                Log.d(TAG, "Vehículos insertados");
            }

            // =================================================================
            // SECCIÓN 12: MOVIMIENTOS
            // idVehiculo: 1-4 | idPersonal: 1-3 (ya en BD) | idTransporte: 1-3
            // idBodegaDestino: 1-6 | Fechas distintas para no exceder límite diario del trigger
            // =================================================================
            if (movimientoDAO.contar() == 0) {
                // Constructor: (id, idTransporte, idPersonal, idVehiculo, idBodega, tipoMovimiento, fechaMovimiento, motivo)
                movimientoDAO.insertar(new Movimiento(0, 1, 1, 1, 1, "entrada", "2024-01-15", "Ingreso a bodega central"));
                movimientoDAO.insertar(new Movimiento(0, 1, 1, 2, 1, "entrada", "2024-01-16", "Ingreso a bodega central"));
                movimientoDAO.insertar(new Movimiento(0, 2, 2, 3, 2, "entrada", "2024-02-20", "Ingreso a bodega occidente"));
                movimientoDAO.insertar(new Movimiento(0, 3, 3, 4, 3, "entrada", "2024-03-10", "Ingreso a bodega oriente"));
                Log.d(TAG, "Movimientos insertados");
            }

            // =================================================================
            // SECCIÓN 13: DETALLES DE DESPERFECTO
            // idVehiculo: 1-4 | idTipoDesperfecto: 1=Rayón,2=Golpe,3=Cristal roto,4=Mecánico
            // =================================================================
            if (detalleDAO.contar() == 0) {
                detalleDAO.insertar(new DetalleDesperfecto(0, 1, 2, "Golpe leve en puerta delantera derecha", "2024-01-15"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 2, 1, "Rayón superficial en capó",              "2024-01-16"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 3, 3, "Vidrio trasero con fisura",              "2024-02-20"));
                detalleDAO.insertar(new DetalleDesperfecto(0, 4, 4, "Falla leve en sistema de frenos",        "2024-03-10"));
                Log.d(TAG, "Detalles de desperfecto insertados");
            }

            // =================================================================
            // SECCIÓN 14: FOTOS DE DESPERFECTO
            // idDetalleDesperfecto: 1-4 (auto-asignados en Sección 13)
            // rutaImagen: ruta de referencia (sin archivo físico en demo)
            // =================================================================
            if (fotoDAO.contar() == 0) {
                fotoDAO.insertar(new FotoDesperfecto(0, 1, "/fotos/v1/golpe_puerta.jpg",   "2024-01-15"));
                fotoDAO.insertar(new FotoDesperfecto(0, 2, "/fotos/v2/rayon_capo.jpg",     "2024-01-16"));
                fotoDAO.insertar(new FotoDesperfecto(0, 3, "/fotos/v3/vidrio_trasero.jpg", "2024-02-20"));
                fotoDAO.insertar(new FotoDesperfecto(0, 4, "/fotos/v4/frenos.jpg",         "2024-03-10"));
                Log.d(TAG, "Fotos de desperfecto insertadas");
            }

            // =================================================================
            // SECCIÓN 15: REPARACIONES
            // idVehiculo: 1-3 | idTaller: 1-3 (ya en BD)
            // Trigger TRG_ESTADO_REPARACION solo dispara en UPDATE, no en INSERT.
            // Por eso los vehículos 1-3 permanecen 'en bodega' tras esta inserción.
            // aptoParaVenta=0 en demo; Ricardo lo actualizará en su módulo.
            // =================================================================
            if (reparacionDAO.contar() == 0) {
                reparacionDAO.insertar(new Reparacion(0, 1, 1, "2024-01-20", "2024-01-25", 0, 1500.00, "Reparación de golpe en puerta"));
                reparacionDAO.insertar(new Reparacion(0, 2, 2, "2024-01-22", "2024-02-01", 0,  850.00, "Pulido y pintura por rayones"));
                reparacionDAO.insertar(new Reparacion(0, 3, 3, "2024-02-25", "2024-03-05", 0,  620.00, "Reemplazo de vidrio trasero"));
                Log.d(TAG, "Reparaciones insertadas");
            }

            // =================================================================
            // SECCIÓN 16: VENTAS
            // idVehiculo: 4 y 5 (no tienen reparaciones abiertas)
            // Trigger TRG_ESTADO_VENDIDO dispara en INSERT y cambia VEHICULO
            // a estado 'vendido' automáticamente para los vehículos 4 y 5.
            // =================================================================
            if (ventaDAO.contar() == 0) {
                ventaDAO.insertar(new Venta(0, 1, 4, "2024-03-15", 22000.00));
                ventaDAO.insertar(new Venta(0, 2, 5, "2024-04-10", 45000.00));
                Log.d(TAG, "Ventas insertadas");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error llenando BD", e);
        }
    }
}
