package com.ues.sistema_pdm1.activities.importacion;

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
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

public class ImportacionDeleteActivity extends AppCompatActivity {

    private int importacionId;
    private GenericDAO<Importacion> importacionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacion_delete);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importacionId = getIntent().getIntExtra("importacion_id", 0);
        String label  = getIntent().getStringExtra("importacion_label");

        importacionDAO = new GenericDAO<>(this, Importacion.class, "importacion");

        ((TextView) findViewById(R.id.delete_importacion_label)).setText(label != null ? label : "");

        findViewById(R.id.btn_cancel_importacion_delete).setOnClickListener(v -> finish());
        ((Button) findViewById(R.id.btn_confirm_importacion_delete)).setOnClickListener(v -> eliminar());
    }

    private void eliminar() {
        if (importacionId <= 0) { finish(); return; }
        try {
            importacionDAO.eliminar(importacionId);
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
