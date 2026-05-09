package com.ues.sistema_pdm1.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ues.sistema_pdm1.data.database.DatabaseManager;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenericDAO<T> implements IBaseDAO<T> {

    private static final String TAG = "GenericDAO";

    private final DatabaseManager dbManager;
    private final Class<T>        entityClass;
    private final String          tableName;
    private final String          pkColumnName;

    public GenericDAO(Context context, Class<T> entityClass, String tableName) {
        this.dbManager   = DatabaseManager.getInstance(context);
        this.entityClass = entityClass;
        this.tableName   = tableName;
        this.pkColumnName = detectPkColumn();
    }

    private String detectPkColumn() {
        String pk = "ID";
        Cursor c = null;
        try {
            dbManager.open();
            c = dbManager.getDatabase().rawQuery(
                "PRAGMA table_info(" + tableName + ")", null);
            if (c != null && c.moveToFirst()) {
                int nameIdx = c.getColumnIndex("name");
                if (nameIdx >= 0) {
                    pk = c.getString(nameIdx).toUpperCase(java.util.Locale.US);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "detectPkColumn [" + tableName + "]: " + e.getMessage());
        } finally {
            if (c != null) c.close();
            dbManager.close();
        }
        return pk;
    }

    // ----------------------------------------------------------------
    // IBaseDAO
    // ----------------------------------------------------------------

    @Override
    public long insertar(T obj) throws SQLException {
        long id = -1;
        try {
            dbManager.open();
            ContentValues values = convertToContentValues(obj);
            id = dbManager.getDatabase().insert(tableName, null, values);
            if (id > 0) {
                setObjectId(obj, id);
            }
        } catch (Exception e) {
            Log.e(TAG, "insertar [" + tableName + "]: " + e.getMessage());
            throw new SQLException("Error al insertar en " + tableName + ": " + e.getMessage());
        } finally {
            dbManager.close();
        }
        return id;
    }

    @Override
    public int actualizar(T obj) throws SQLException {
        int rows = 0;
        try {
            dbManager.open();
            long id = getObjectId(obj);
            ContentValues values = convertToContentValues(obj);
            rows = dbManager.getDatabase().update(
                    tableName, values, pkColumnName + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "actualizar [" + tableName + "]: " + e.getMessage());
            throw new SQLException("Error al actualizar en " + tableName + ": " + e.getMessage());
        } finally {
            dbManager.close();
        }
        return rows;
    }

    @Override
    public int eliminar(long id) throws SQLException {
        int rows = 0;
        try {
            dbManager.open();
            rows = dbManager.getDatabase().delete(
                    tableName, pkColumnName + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "eliminar [" + tableName + "]: " + e.getMessage());
            throw new SQLException("Error al eliminar en " + tableName + ": " + e.getMessage());
        } finally {
            dbManager.close();
        }
        return rows;
    }

    @Override
    public T obtenerPorId(long id) throws SQLException {
        T      obj    = null;
        Cursor cursor = null;
        try {
            dbManager.open();
            cursor = dbManager.getDatabase().query(
                    tableName, null,
                    pkColumnName + " = ?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                obj = convertCursorToObject(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "obtenerPorId [" + tableName + "]: " + e.getMessage());
            throw new SQLException("Error al obtener por id en " + tableName + ": " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            dbManager.close();
        }
        return obj;
    }

    @Override
    public List<T> obtenerTodos() throws SQLException {
        List<T> lista  = new ArrayList<>();
        Cursor  cursor = null;
        try {
            dbManager.open();
            cursor = dbManager.getDatabase().query(
                    tableName, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T obj = convertCursorToObject(cursor);
                    if (obj != null) lista.add(obj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "obtenerTodos [" + tableName + "]: " + e.getMessage());
            throw new SQLException("Error al obtener todos en " + tableName + ": " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            dbManager.close();
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // Métodos adicionales públicos
    // ----------------------------------------------------------------

    public List<T> obtenerPor(String columna, String valor) {
        List<T> lista  = new ArrayList<>();
        Cursor  cursor = null;
        try {
            dbManager.open();
            cursor = dbManager.getDatabase().query(
                    tableName, null,
                    columna + " = ?", new String[]{valor},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T obj = convertCursorToObject(cursor);
                    if (obj != null) lista.add(obj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "obtenerPor [" + tableName + "." + columna + "]: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            dbManager.close();
        }
        return lista;
    }

    public long contar() {
        long   count  = 0;
        Cursor cursor = null;
        try {
            dbManager.open();
            cursor = dbManager.getDatabase()
                    .rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "contar [" + tableName + "]: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            dbManager.close();
        }
        return count;
    }

    // ----------------------------------------------------------------
    // Conversión nombre de campo: camelCase → SCREAMING_SNAKE_CASE
    // Ejemplos: nomUsuario → NOM_USUARIO, idOpcion → ID_OPCION
    // ----------------------------------------------------------------

    private static String camelToSnake(String camel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // Conversión objeto ↔ cursor / ContentValues
    // ----------------------------------------------------------------

    private ContentValues convertToContentValues(T obj) {
        ContentValues values = new ContentValues();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String fieldName  = field.getName();
                Object value      = field.get(obj);
                String columnName = fieldName.equalsIgnoreCase("id")
                        ? pkColumnName
                        : camelToSnake(fieldName);

                // Campo "id" numérico: omitir si es 0 o negativo (autoincrement)
                if (fieldName.equalsIgnoreCase("id")) {
                    if (value == null) continue;
                    if (value instanceof Number && ((Number) value).longValue() <= 0) continue;
                    // String id ("100", "1", etc.) se incluye tal cual
                }

                if (value == null) {
                    values.putNull(columnName);
                } else if (value instanceof String) {
                    values.put(columnName, (String) value);
                } else if (value instanceof Integer) {
                    values.put(columnName, (Integer) value);
                } else if (value instanceof Long) {
                    values.put(columnName, (Long) value);
                } else if (value instanceof Float) {
                    values.put(columnName, (Float) value);
                } else if (value instanceof Double) {
                    values.put(columnName, (Double) value);
                } else if (value instanceof Boolean) {
                    values.put(columnName, (Boolean) value ? 1 : 0);
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG, "convertToContentValues campo [" + field.getName() + "]: " + e.getMessage());
            }
        }
        return values;
    }

    private T convertCursorToObject(Cursor cursor) {
        try {
            T       obj    = entityClass.getDeclaredConstructor().newInstance();
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName  = field.getName();
                String columnName = camelToSnake(fieldName);

                // Para el campo "id": buscar columna en cursor por nombre convertido;
                // si no existe (ej. ID_PAIS en vez de ID), usar la primera columna del cursor.
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex < 0 && fieldName.equalsIgnoreCase("id")) {
                    columnIndex = cursor.getColumnIndex(pkColumnName);
                    if (columnIndex < 0) columnIndex = 0;
                }
                if (columnIndex < 0) continue;

                if (cursor.isNull(columnIndex)) {
                    if (!field.getType().isPrimitive()) {
                        field.set(obj, null);
                    }
                    continue;
                }

                Class<?> type = field.getType();
                if (type == String.class) {
                    field.set(obj, cursor.getString(columnIndex));
                } else if (type == int.class || type == Integer.class) {
                    field.set(obj, cursor.getInt(columnIndex));
                } else if (type == long.class || type == Long.class) {
                    field.set(obj, cursor.getLong(columnIndex));
                } else if (type == float.class || type == Float.class) {
                    field.set(obj, cursor.getFloat(columnIndex));
                } else if (type == double.class || type == Double.class) {
                    field.set(obj, cursor.getDouble(columnIndex));
                } else if (type == boolean.class || type == Boolean.class) {
                    field.set(obj, cursor.getInt(columnIndex) == 1);
                }
            }
            return obj;
        } catch (Exception e) {
            Log.e(TAG, "convertCursorToObject [" + entityClass.getSimpleName() + "]: " + e.getMessage());
            return null;
        }
    }

    private long getObjectId(T obj) throws NoSuchFieldException, IllegalAccessException {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id")) {
                field.setAccessible(true);
                return ((Number) field.get(obj)).longValue();
            }
        }
        throw new NoSuchFieldException(
                "No se encontró campo 'id' en " + entityClass.getSimpleName());
    }

    private void setObjectId(T obj, long id) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id")) {
                field.setAccessible(true);
                try {
                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(obj, (int) id);
                    } else if (field.getType() == String.class) {
                        field.set(obj, String.valueOf(id));
                    } else {
                        field.set(obj, id);
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "setObjectId [" + entityClass.getSimpleName() + "]: " + e.getMessage());
                }
                return;
            }
        }
    }
}
