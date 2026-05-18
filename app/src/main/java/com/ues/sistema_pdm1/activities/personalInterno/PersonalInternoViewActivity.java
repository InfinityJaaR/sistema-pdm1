package com.ues.sistema_pdm1.activities.personalInterno;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.utils.SessionManager;

public class PersonalInternoViewActivity extends AppCompatActivity {

    private int personalId;

    private GenericDAO<PersonalInterno> personalDAO;

    private final ActivityResultLauncher<Intent> launcherEditar =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) { recargar(); setResult(RESULT_OK); } });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_interno_view);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        personalId = getIntent().getIntExtra("personal_id", 0);

        personalDAO = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");

        recargar();

        findViewById(R.id.btn_editar_personal).setOnClickListener(v -> abrirEditar());
    }

    private void recargar() {
        try {
            PersonalInterno p = personalDAO.obtenerPorId(personalId);
            if (p == null) { finish(); return; }
            poblarVista(p);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void poblarVista(PersonalInterno p) {
        ((TextView) findViewById(R.id.detail_nombre_completo))
            .setText(p.getNombrePersonal() + " " + p.getApellidoPersonal());
        ((TextView) findViewById(R.id.detail_cargo_header)).setText(p.getCargo());
        ((TextView) findViewById(R.id.detail_nombre_personal)).setText(p.getNombrePersonal());
        ((TextView) findViewById(R.id.detail_apellido_personal)).setText(p.getApellidoPersonal());
        ((TextView) findViewById(R.id.detail_cargo_personal)).setText(p.getCargo());
    }

    private void abrirEditar() {
        Intent intent = new Intent(this, PersonalInternoFormActivity.class);
        intent.putExtra("personal_id", personalId);
        launcherEditar.launch(intent);
    }
}
