package com.ues.sistema_pdm1.activities.movimiento;

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
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

public class MovimientoDeleteActivity extends AppCompatActivity {

    private int movimientoId;

    private GenericDAO<Movimiento> movimientoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento_delete);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        movimientoId = getIntent().getIntExtra("movimiento_id", 0);
        String label = getIntent().getStringExtra("movimiento_label");

        movimientoDAO = new GenericDAO<>(this, Movimiento.class, "movimiento");

        TextView tvLabel = findViewById(R.id.delete_movimiento_label);
        tvLabel.setText(label != null ? label : "");

        Button btnCancelar  = findViewById(R.id.btn_cancelar_delete_movimiento);
        Button btnConfirmar = findViewById(R.id.btn_confirmar_delete_movimiento);

        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> eliminar());
    }

    private void eliminar() {
        if (movimientoId <= 0) { finish(); return; }

        try {
            movimientoDAO.eliminar(movimientoId);
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
