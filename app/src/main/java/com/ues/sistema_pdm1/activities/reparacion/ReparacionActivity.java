package com.ues.sistema_pdm1.activities.reparacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Reparacion;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ReparacionActivity extends AppCompatActivity {

    private ListView      lvReparaciones;
    private ImageButton   btnAgregar, btnBuscar;
    private EditText      etBuscar;

    private GenericDAO<Reparacion> reparacionDAO;
    private GenericDAO<Vehiculo>   vehiculoDAO;

    private List<Reparacion> reparaciones          = new ArrayList<>();
    private List<Reparacion> reparacionesFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparacion);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvReparaciones = findViewById(R.id.lv_reparaciones);
        btnAgregar     = findViewById(R.id.btn_reparacion_agregar);
        btnBuscar      = findViewById(R.id.btn_reparacion_lupa);
        etBuscar       = findViewById(R.id.et_reparacion_buscar);

        reparacionDAO = new GenericDAO<>(this, Reparacion.class, "reparacion");
        vehiculoDAO   = new GenericDAO<>(this, Vehiculo.class, "vehiculo");

        cargarReparaciones();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvReparaciones.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(reparacionesFiltradas.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarReparaciones() {
        try {
            reparaciones = reparacionDAO.obtenerTodos();
            reparacionesFiltradas = new ArrayList<>(reparaciones);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar reparaciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Reparacion> adapter = new ArrayAdapter<>(
                this, R.layout.item_reparacion, R.id.item_nombre, reparacionesFiltradas);
        lvReparaciones.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            reparacionesFiltradas = new ArrayList<>(reparaciones);
        } else {
            reparacionesFiltradas = new ArrayList<>();
            for (Reparacion r : reparaciones) {
                if (r.toString().toLowerCase().contains(texto))
                    reparacionesFiltradas.add(r);
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Reparacion reparacion) {
        ReparacionOptionsDialog dialog = ReparacionOptionsDialog.newInstance(reparacion);
        dialog.setCallbacks(
                () -> abrirVer(reparacion),
                () -> abrirFormulario(reparacion),
                () -> confirmarEliminar(reparacion)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Reparacion reparacion) {
        ReparacionViewDialog.newInstance(reparacion)
                .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Reparacion reparacion) {
        ReparacionFormDialog dialog = ReparacionFormDialog.newInstance(reparacion);
        dialog.setOnSaveListener(guardado -> {
            if (reparacion == null) {
                insertar(guardado);
            } else {
                actualizar(guardado);
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void insertar(Reparacion reparacion) {
        try {
            reparacionDAO.insertar(reparacion);
            cargarReparaciones();
            Toast.makeText(this, "Reparación registrada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar reparación", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizar(Reparacion reparacion) {
        if (reparacion.getAptoParaVenta() == 1 && reparacion.getRequiereOtraReparacion() == 1) {
            Toast.makeText(this,
                    "No puede ser apto para venta y requerir otra reparación a la vez",
                    Toast.LENGTH_LONG).show();
            return;
        }

        List<Vehiculo> vehiculos;
        try {
            vehiculos = vehiculoDAO.obtenerPor(
                    "ID_VEHICULO", String.valueOf(reparacion.getIdVehiculo()));
        } catch (Exception e) {
            Toast.makeText(this, "Error al verificar vehículo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vehiculos == null || vehiculos.isEmpty()) {
            Toast.makeText(this, "No se encontró el vehículo asociado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Vehiculo vehiculo = vehiculos.get(0);

        if (reparacion.getAptoParaVenta() == 1
                && !vehiculo.getEstadoVehiculo().equals("en reparacion")) {
            Toast.makeText(this,
                    "El vehículo no está en reparación, no se puede marcar como apto",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            reparacionDAO.actualizar(reparacion);
            cargarReparaciones();
            Toast.makeText(this, "Reparación actualizada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al actualizar reparación", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminar(Reparacion reparacion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Reparación")
                .setMessage("¿Desea eliminar esta reparación?")
                .setPositiveButton("Sí", (d, w) -> {
                    try {
                        reparacionDAO.eliminar(reparacion.getId());
                        cargarReparaciones();
                        Toast.makeText(this, "Reparación eliminada", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al eliminar reparación", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}