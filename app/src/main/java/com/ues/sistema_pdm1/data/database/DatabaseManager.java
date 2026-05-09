package com.ues.sistema_pdm1.data.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private static DatabaseManager instance;

    private final DatabaseHelper  dbHelper;
    private       SQLiteDatabase  db;

    private DatabaseManager(Context ctx) {
        dbHelper = new DatabaseHelper(ctx.getApplicationContext());
    }

    public static synchronized DatabaseManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseManager(ctx);
        }
        return instance;
    }

    public synchronized void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
            Log.d(TAG, "Conexión abierta: " + DatabaseHelper.class.getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir la base de datos: " + e.getMessage());
            throw new SQLException("No se pudo abrir la base de datos: " + e.getMessage());
        }
    }

    public synchronized void close() {
        if (dbHelper != null) {
            dbHelper.close();
            db = null;
            Log.d(TAG, "Conexión cerrada");
        }
    }

    public SQLiteDatabase getDatabase() {
        if (db == null) {
            throw new IllegalStateException("Base de datos no inicializada. Llame a open() primero.");
        }
        return db;
    }
}