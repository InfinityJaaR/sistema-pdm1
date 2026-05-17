package com.ues.sistema_pdm1.activities.personalInterno;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;


public class PersonalInternoDeleteActivity extends AppCompatActivity {

    private int personalId;

    private GenericDAO<PersonalInterno> personalDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_interno_delete);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        personalId = getIntent().getIntExtra("personal_id", 0);
        String nombre = getIntent().getStringExtra("personal_nombre");

        personalDAO = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");

        TextView tvNombre = findViewById(R.id.delete_personal_nombre);
        tvNombre.setText(nombre != null ? nombre : "");

        Button btnCancelar  = findViewById(R.id.btn_cancelar_delete_personal);
        Button btnConfirmar = findViewById(R.id.btn_confirmar_delete_personal);

        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> eliminar());
    }

    private void eliminar() {
        if (personalId <= 0) { finish(); return; }

        try {
            personalDAO.eliminar(personalId);
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
