package com.ues.sistema_pdm1.activities.importacion;

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
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.utils.SessionManager;

public class ImportacionViewActivity extends AppCompatActivity {

    private int importacionId;

    private GenericDAO<Importacion> importacionDAO;
    private GenericDAO<Importador> importadorDAO;

    private final ActivityResultLauncher<Intent> launcherEditar =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) recargar(); });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacion_view);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importacionId  = getIntent().getIntExtra("importacion_id", 0);
        importacionDAO = new GenericDAO<>(this, Importacion.class, "importacion");
        importadorDAO  = new GenericDAO<>(this, Importador.class, "importador");

        recargar();

        findViewById(R.id.btn_edit_importacion).setOnClickListener(v -> {
            Intent intent = new Intent(this, ImportacionFormActivity.class);
            intent.putExtra("importacion_id", importacionId);
            launcherEditar.launch(intent);
        });
    }

    private void recargar() {
        try {
            Importacion imp = importacionDAO.obtenerPorId(importacionId);
            if (imp == null) { finish(); return; }

            ((TextView) findViewById(R.id.view_importacion_titulo)).setText("Importación #" + imp.getId());
            ((TextView) findViewById(R.id.view_importacion_fecha)).setText(imp.getFechaImportacion());
            ((TextView) findViewById(R.id.view_importacion_fecha_valor)).setText(imp.getFechaImportacion());

            // Buscar nombre del importador
            String nombreImp;
            try {
                Importador importador = importadorDAO.obtenerPorId(imp.getIdImportador());
                nombreImp = importador != null
                    ? importador.getNombreImportador() + " " + importador.getApellidoImportador()
                    : "Importador #" + imp.getIdImportador();
            } catch (Exception e) {
                nombreImp = "Importador #" + imp.getIdImportador();
            }
            ((TextView) findViewById(R.id.view_importacion_importador)).setText(nombreImp);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
