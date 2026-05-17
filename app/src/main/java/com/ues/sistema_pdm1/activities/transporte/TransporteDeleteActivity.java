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
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.List;

public class TransporteDeleteActivity extends AppCompatActivity {

    private int transporteId;

    private GenericDAO<Transporte> transporteDAO;
    private GenericDAO<Movimiento> movimientoDAO;

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
        movimientoDAO = new GenericDAO<>(this, Movimiento.class, "movimiento");

        TextView tvPlaca = findViewById(R.id.delete_transporte_placa);
        tvPlaca.setText(placa != null ? placa : "");

        Button btnCancelar  = findViewById(R.id.btn_cancelar_delete_transporte);
        Button btnConfirmar = findViewById(R.id.btn_confirmar_delete_transporte);

        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> eliminar());
    }

    private void eliminar() {
        if (transporteId <= 0) { finish(); return; }

        List<Movimiento> movimientos = movimientoDAO.obtenerPor("ID_TRANSPORTE",
            String.valueOf(transporteId));
        if (!movimientos.isEmpty()) {
            Toast.makeText(this,
                "No se puede eliminar: el transporte tiene " + movimientos.size()
                    + " movimiento(s) registrado(s)",
                Toast.LENGTH_LONG).show();
            return;
        }

        try {
            transporteDAO.eliminar(transporteId);
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
        }
    }
}
