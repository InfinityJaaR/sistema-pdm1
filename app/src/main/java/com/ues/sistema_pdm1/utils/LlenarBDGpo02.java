package com.ues.sistema_pdm1.utils;

import android.content.Context;
import android.util.Log;

import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.AccesoUsuario;
import com.ues.sistema_pdm1.models.Departamento;
import com.ues.sistema_pdm1.models.Distrito;
import com.ues.sistema_pdm1.models.Marca;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Municipio;
import com.ues.sistema_pdm1.models.OpcionCrud;
import com.ues.sistema_pdm1.models.Pais;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.models.Taller;
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
            // SECCIÓN 2: OPCIONES CRUD (30 opciones)
            // IDs son String porque ID_OPCION es TEXT en BD
            // =================================================================
            if (opcionDAO.contar() == 0) {

                // Módulo IMPORTADOR (100-104)
                opcionDAO.insertar(new OpcionCrud("100", "Menu Importador",       0));
                opcionDAO.insertar(new OpcionCrud("101", "Agregar Importador",    1));
                opcionDAO.insertar(new OpcionCrud("102", "Modificar Importador",  2));
                opcionDAO.insertar(new OpcionCrud("103", "Eliminar Importador",   3));
                opcionDAO.insertar(new OpcionCrud("104", "Consultar Importador",  4));

                // Módulo VEHÍCULO (200-204)
                opcionDAO.insertar(new OpcionCrud("200", "Menu Vehiculo",         0));
                opcionDAO.insertar(new OpcionCrud("201", "Agregar Vehiculo",      1));
                opcionDAO.insertar(new OpcionCrud("202", "Modificar Vehiculo",    2));
                opcionDAO.insertar(new OpcionCrud("203", "Eliminar Vehiculo",     3));
                opcionDAO.insertar(new OpcionCrud("204", "Consultar Vehiculo",    4));

                // Módulo MOVIMIENTO (300-304)
                opcionDAO.insertar(new OpcionCrud("300", "Menu Movimiento",       0));
                opcionDAO.insertar(new OpcionCrud("301", "Agregar Movimiento",    1));
                opcionDAO.insertar(new OpcionCrud("302", "Modificar Movimiento",  2));
                opcionDAO.insertar(new OpcionCrud("303", "Eliminar Movimiento",   3));
                opcionDAO.insertar(new OpcionCrud("304", "Consultar Movimiento",  4));

                // Módulo REPARACIÓN (400-404)
                opcionDAO.insertar(new OpcionCrud("400", "Menu Reparacion",       0));
                opcionDAO.insertar(new OpcionCrud("401", "Agregar Reparacion",    1));
                opcionDAO.insertar(new OpcionCrud("402", "Modificar Reparacion",  2));
                opcionDAO.insertar(new OpcionCrud("403", "Eliminar Reparacion",   3));
                opcionDAO.insertar(new OpcionCrud("404", "Consultar Reparacion",  4));

                // Módulo VENTA (500-504)
                opcionDAO.insertar(new OpcionCrud("500", "Menu Venta",            0));
                opcionDAO.insertar(new OpcionCrud("501", "Agregar Venta",         1));
                opcionDAO.insertar(new OpcionCrud("502", "Modificar Venta",       2));
                opcionDAO.insertar(new OpcionCrud("503", "Eliminar Venta",        3));
                opcionDAO.insertar(new OpcionCrud("504", "Consultar Venta",       4));

                // Módulo CATÁLOGOS (600-604)
                opcionDAO.insertar(new OpcionCrud("600", "Menu Catalogos",        0));
                opcionDAO.insertar(new OpcionCrud("601", "Agregar Catalogo",      1));
                opcionDAO.insertar(new OpcionCrud("602", "Modificar Catalogo",    2));
                opcionDAO.insertar(new OpcionCrud("603", "Eliminar Catalogo",     3));
                opcionDAO.insertar(new OpcionCrud("604", "Consultar Catalogo",    4));

                Log.d(TAG, "30 opciones CRUD insertadas");
            }

            // =================================================================
            // SECCIÓN 3: MATRIZ DE ACCESOS
            // idOpcion y idUsuario son String (TEXT en BD)
            // =================================================================
            if (accesoDAO.contar() == 0) {

                // Todas las IDs de opción válidas
                String[] todasOpciones = {
                    "100","101","102","103","104",
                    "200","201","202","203","204",
                    "300","301","302","303","304",
                    "400","401","402","403","404",
                    "500","501","502","503","504",
                    "600","601","602","603","604"
                };

                // ADMIN (id="1"): acceso total a las 30 opciones
                for (String opcion : todasOpciones) {
                    accesoDAO.insertar(new AccesoUsuario(opcion, "1"));
                }

                // IMPORTADOR (id="2"): módulo importador + consultar catálogos
                accesoDAO.insertar(new AccesoUsuario("100", "2"));
                accesoDAO.insertar(new AccesoUsuario("101", "2"));
                accesoDAO.insertar(new AccesoUsuario("102", "2"));
                accesoDAO.insertar(new AccesoUsuario("103", "2"));
                accesoDAO.insertar(new AccesoUsuario("104", "2"));
                accesoDAO.insertar(new AccesoUsuario("604", "2"));

                // PERSONAL (id="3"): módulo movimiento + consultar catálogos
                accesoDAO.insertar(new AccesoUsuario("300", "3"));
                accesoDAO.insertar(new AccesoUsuario("301", "3"));
                accesoDAO.insertar(new AccesoUsuario("302", "3"));
                accesoDAO.insertar(new AccesoUsuario("303", "3"));
                accesoDAO.insertar(new AccesoUsuario("304", "3"));
                accesoDAO.insertar(new AccesoUsuario("604", "3"));

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

        } catch (Exception e) {
            Log.e(TAG, "Error llenando BD", e);
        }
    }
}
