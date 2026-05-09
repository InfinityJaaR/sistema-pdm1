package com.ues.sistema_pdm1.data.dao;

import java.sql.SQLException;
import java.util.List;

public interface IBaseDAO<T> {

    long    insertar(T obj)      throws SQLException;
    int     actualizar(T obj)    throws SQLException;
    int     eliminar(long id)    throws SQLException;
    T       obtenerPorId(long id) throws SQLException;
    List<T> obtenerTodos()       throws SQLException;
}