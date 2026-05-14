package com.ues.sistema_pdm1.activities.importador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.TelefonoImportador;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.List;

public class ImportadorDeleteActivity extends AppCompatActivity {

    private int importadorId;

    private GenericDAO<Importador> importadorDAO;
    private GenericDAO<TelefonoImportador> telefonoDAO;
    private GenericDAO<Importacion> importacionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer_delete);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importadorId = getIntent().getIntExtra("importador_id", 0);
        String nombre = getIntent().getStringExtra("importador_nombre");

        importadorDAO   = new GenericDAO<>(this, Importador.class, "importador");
        telefonoDAO     = new GenericDAO<>(this, TelefonoImportador.class, "telefono_importador");
        importacionDAO  = new GenericDAO<>(this, Importacion.class, "importacion");

        TextView tvNombre = findViewById(R.id.delete_importer_name);
        tvNombre.setText(nombre != null ? nombre : "");

        Button btnCancelar = findViewById(R.id.btn_cancel_delete);
        Button btnConfirmar = findViewById(R.id.btn_confirm_delete);

        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> eliminar());
    }

    private void eliminar() {
        if (importadorId <= 0) {
            finish();
            return;
        }

        // Verificar si tiene importaciones vinculadas
        List<Importacion> importaciones = importacionDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(importadorId));
        if (!importaciones.isEmpty()) {
            Toast.makeText(this,
                "No se puede eliminar: el importador tiene " + importaciones.size() + " importación(es) registrada(s)",
                Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Eliminar teléfonos primero
            List<TelefonoImportador> tels = telefonoDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(importadorId));
            for (TelefonoImportador t : tels) {
                telefonoDAO.eliminar(t.getId());
            }

            // Eliminar importador
            importadorDAO.eliminar(importadorId);
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
        }
    }
}
