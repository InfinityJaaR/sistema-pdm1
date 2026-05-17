package com.ues.sistema_pdm1.activities.movimiento;

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
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.List;

public class MovimientoViewActivity extends AppCompatActivity {

    private int movimientoId;

    private GenericDAO<Movimiento> movimientoDAO;
    private GenericDAO<Vehiculo> vehiculoDAO;
    private GenericDAO<Transporte> transporteDAO;
    private GenericDAO<PersonalInterno> personalDAO;
    private GenericDAO<Bodega> bodegaDAO;

    private final ActivityResultLauncher<Intent> launcherEditar =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) { recargar(); setResult(RESULT_OK); } });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento_view);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        movimientoId = getIntent().getIntExtra("movimiento_id", 0);

        movimientoDAO = new GenericDAO<>(this, Movimiento.class, "movimiento");
        vehiculoDAO   = new GenericDAO<>(this, Vehiculo.class, "vehiculo");
        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");
        personalDAO   = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");
        bodegaDAO     = new GenericDAO<>(this, Bodega.class, "bodega");

        recargar();

        findViewById(R.id.btn_editar_movimiento).setOnClickListener(v -> abrirEditar());
    }

    private void recargar() {
        try {
            Movimiento m = movimientoDAO.obtenerPorId(movimientoId);
            if (m == null) { finish(); return; }
            poblarVista(m);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void poblarVista(Movimiento m) {
        ((TextView) findViewById(R.id.detail_tipo_movimiento_header)).setText(m.getTipoMovimiento());
        ((TextView) findViewById(R.id.detail_fecha_movimiento_header)).setText(m.getFechaMovimiento());
        ((TextView) findViewById(R.id.detail_tipo_movimiento)).setText(m.getTipoMovimiento());
        ((TextView) findViewById(R.id.detail_fecha_movimiento)).setText(m.getFechaMovimiento());
        ((TextView) findViewById(R.id.detail_motivo_movimiento))
            .setText(m.getMotivo() != null ? m.getMotivo() : "—");

        ((TextView) findViewById(R.id.detail_vehiculo_movimiento)).setText(resolverVehiculo(m.getIdVehiculo()));
        ((TextView) findViewById(R.id.detail_transporte_movimiento)).setText(resolverTransporte(m.getIdTransporte()));
        ((TextView) findViewById(R.id.detail_personal_movimiento)).setText(resolverPersonal(m.getIdPersonal()));
        ((TextView) findViewById(R.id.detail_bodega_movimiento)).setText(resolverBodega(m.getIdBodega()));
    }

    private String resolverVehiculo(int id) {
        try {
            Vehiculo v = vehiculoDAO.obtenerPorId(id);
            return v != null ? v.getVin() : "—";
        } catch (Exception e) { return "—"; }
    }

    private String resolverTransporte(int id) {
        try {
            Transporte t = transporteDAO.obtenerPorId(id);
            return t != null ? t.getPlaca() : "—";
        } catch (Exception e) { return "—"; }
    }

    private String resolverPersonal(int id) {
        try {
            PersonalInterno p = personalDAO.obtenerPorId(id);
            return p != null ? (p.getNombrePersonal() + " " + p.getApellidoPersonal()) : "—";
        } catch (Exception e) { return "—"; }
    }

    private String resolverBodega(int id) {
        try {
            Bodega b = bodegaDAO.obtenerPorId(id);
            return b != null ? b.getNombreBodega() : "—";
        } catch (Exception e) { return "—"; }
    }

    private void abrirEditar() {
        Intent intent = new Intent(this, MovimientoFormActivity.class);
        intent.putExtra("movimiento_id", movimientoId);
        launcherEditar.launch(intent);
    }
}
