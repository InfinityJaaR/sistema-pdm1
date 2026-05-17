package com.ues.sistema_pdm1.activities.transporte;

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
import com.ues.sistema_pdm1.models.TipoTransporte;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.utils.SessionManager;

public class TransporteViewActivity extends AppCompatActivity {

    private int transporteId;

    private GenericDAO<Transporte> transporteDAO;
    private GenericDAO<TipoTransporte> tipoDAO;

    private final ActivityResultLauncher<Intent> launcherEditar =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) { recargar(); setResult(RESULT_OK); } });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_view);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        transporteId = getIntent().getIntExtra("transporte_id", 0);

        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");
        tipoDAO       = new GenericDAO<>(this, TipoTransporte.class, "tipo_transporte");

        recargar();

        findViewById(R.id.btn_editar_transporte).setOnClickListener(v -> abrirEditar());
    }

    private void recargar() {
        try {
            Transporte t = transporteDAO.obtenerPorId(transporteId);
            if (t == null) { finish(); return; }
            poblarVista(t);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void poblarVista(Transporte t) {
        ((TextView) findViewById(R.id.detail_placa)).setText(t.getPlaca());

        String tipoNombre = "—";
        try {
            TipoTransporte tipo = tipoDAO.obtenerPorId(t.getIdTipoTransporte());
            if (tipo != null) tipoNombre = tipo.getDescripcionTipoTransporte();
        } catch (Exception ignored) {}

        ((TextView) findViewById(R.id.detail_tipo_transporte)).setText(tipoNombre);

        String desc = t.getDescripcionTransporte();
        ((TextView) findViewById(R.id.detail_descripcion_transporte))
            .setText((desc != null && !desc.isEmpty()) ? desc : "—");
    }

    private void abrirEditar() {
        Intent intent = new Intent(this, TransporteFormActivity.class);
        intent.putExtra("transporte_id", transporteId);
        launcherEditar.launch(intent);
    }
}
