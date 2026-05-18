package com.ues.sistema_pdm1.activities.transporte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;


public class TransporteDeleteActivity extends AppCompatActivity {

    private int transporteId;

    private GenericDAO<Transporte> transporteDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_delete);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        transporteId = getIntent().getIntExtra("transporte_id", 0);
        String placa = getIntent().getStringExtra("transporte_placa");

        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");

        TextView tvPlaca = findViewById(R.id.delete_transporte_placa);
        tvPlaca.setText(placa != null ? placa : "");

        Button btnCancelar  = findViewById(R.id.btn_cancelar_delete_transporte);
        Button btnConfirmar = findViewById(R.id.btn_confirmar_delete_transporte);

        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> eliminar());
    }

    private void eliminar() {
        if (transporteId <= 0) { finish(); return; }

        try {
            transporteDAO.eliminar(transporteId);
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
