package com.ues.sistema_pdm1.utils;

import android.content.Context;
import android.util.Log;

import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.AccesoUsuario;
import com.ues.sistema_pdm1.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static final String TAG = "SessionManager";

    private static SessionManager instance;

    private Usuario usuarioLogueado;
    private List<Integer> idsOpcionesAccesibles;

    private GenericDAO<Usuario> usuarioDAO;
    private GenericDAO<AccesoUsuario> accesoDAO;

    private SessionManager() {
        idsOpcionesAccesibles = new ArrayList<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void inicializar(Context context) {
        usuarioDAO = new GenericDAO<>(context, Usuario.class, "usuario");
        accesoDAO = new GenericDAO<>(context, AccesoUsuario.class, "accesousuario");
    }

    public boolean login(String nomUsuario, String clave) {
        try {
            usuarioLogueado = buscarUsuario(nomUsuario, clave);
            if (usuarioLogueado == null) {
                return false;
            }
            cargarAccesos(usuarioLogueado.getId());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error en login: " + e.getMessage(), e);
            usuarioLogueado = null;
            idsOpcionesAccesibles.clear();
            return false;
        }
    }

    public void logout() {
        usuarioLogueado = null;
        idsOpcionesAccesibles.clear();
        Log.d(TAG, "Sesión cerrada");
    }

    public boolean isLoggedIn() {
        return usuarioLogueado != null;
    }

    public String getNombreUsuario() {
        if (isLoggedIn()) {
            return usuarioLogueado.getNomUsuario();
        }
        return "No logueado";
    }

    public Usuario getUsuarioActual() {
        return usuarioLogueado;
    }

    public boolean tieneAcceso(int idOpcion) {
        if (!isLoggedIn()) {
            return false;
        }
        return idsOpcionesAccesibles.contains(idOpcion);
    }

    public boolean tieneAcceso(String nombreModulo) {
        try {
            int idOpcion = Integer.parseInt(nombreModulo);
            return tieneAcceso(idOpcion);
        } catch (NumberFormatException e) {
            Log.e(TAG, "idOpcion no es un número válido: " + nombreModulo);
            return false;
        }
    }

    public boolean puedeEliminar() {
        if (!isLoggedIn()) return false;
        return !"3".equals(usuarioLogueado.getId());
    }

    public List<Integer> obtenerAccesibles() {
        return new ArrayList<>(idsOpcionesAccesibles);
    }

    private Usuario buscarUsuario(String nomUsuario, String clave) {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            for (Usuario u : usuarios) {
                if (nomUsuario.equals(u.getNomUsuario()) && clave.equals(u.getClave())) {
                    return u;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al buscar usuario: " + e.getMessage(), e);
        }
        return null;
    }

    private void cargarAccesos(String idUsuario) {
        idsOpcionesAccesibles.clear();
        try {
            List<AccesoUsuario> accesos = accesoDAO.obtenerTodos();
            for (AccesoUsuario acceso : accesos) {
                if (idUsuario.equals(acceso.getIdUsuario())) {
                    try {
                        idsOpcionesAccesibles.add(Integer.parseInt(acceso.getIdOpcion()));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "ID_OPCION no numérico ignorado: " + acceso.getIdOpcion());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar accesos: " + e.getMessage(), e);
        }
    }
}
