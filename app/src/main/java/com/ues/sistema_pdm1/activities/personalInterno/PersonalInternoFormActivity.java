package com.ues.sistema_pdm1.activities.personalInterno;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

public class PersonalInternoFormActivity extends AppCompatActivity {

    private TextView formTitle;
    private EditText etNombre, etApellido, etCargo;
    private Button btnCancelar, btnGuardar;

    private GenericDAO<PersonalInterno> personalDAO;

    private boolean esEdicion = false;
    private int personalId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_interno_form);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        personalId = getIntent().getIntExtra("personal_id", 0);
        esEdicion  = personalId > 0;

        formTitle  = findViewById(R.id.form_personal_title);
        etNombre   = findViewById(R.id.et_nombre_personal);
        etApellido = findViewById(R.id.et_apellido_personal);
        etCargo    = findViewById(R.id.et_cargo_personal);
        btnCancelar = findViewById(R.id.btn_cancelar_personal);
        btnGuardar  = findViewById(R.id.btn_guardar_personal);

        personalDAO = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");

        formTitle.setText(esEdicion ? "Editar Personal" : "Nuevo Personal");

        if (esEdicion) cargarDatos();

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void cargarDatos() {
        try {
            PersonalInterno p = personalDAO.obtenerPorId(personalId);
            if (p == null) return;
            etNombre.setText(p.getNombrePersonal());
            etApellido.setText(p.getApellidoPersonal());
            etCargo.setText(p.getCargo());
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardar() {
        String nombre   = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String cargo    = etCargo.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || cargo.isEmpty()) {
            Toast.makeText(this, Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        PersonalInterno p = new PersonalInterno(personalId, nombre, apellido, cargo);

        try {
            if (esEdicion) {
                personalDAO.actualizar(p);
            } else {
                personalDAO.insertar(p);
            }
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
        }
    }
}
