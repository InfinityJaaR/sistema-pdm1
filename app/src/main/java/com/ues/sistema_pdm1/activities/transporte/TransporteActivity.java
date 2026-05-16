package com.ues.sistema_pdm1.activities.transporte;

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
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TransporteActivity extends AppCompatActivity {

    private ListView lvTransporte;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<Transporte> transporteDAO;
    private List<Transporte> transportes         = new ArrayList<>();
    private List<Transporte> transportesFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvTransporte = findViewById(R.id.lv_transporte);
        btnAgregar   = findViewById(R.id.btn_transporte_agregar);
        btnBuscar    = findViewById(R.id.btn_transporte_lupa);
        etBuscar     = findViewById(R.id.et_transporte_buscar);

        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");

        cargarTransportes();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvTransporte.setOnItemClickListener((parent, view, position, id) ->
            abrirDialogOpciones(transportesFiltrados.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarTransportes() {
        try {
            transportes = transporteDAO.obtenerTodos();
            transportesFiltrados = new ArrayList<>(transportes);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar transportes", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Transporte> adapter = new ArrayAdapter<>(
            this, R.layout.item_transporte, R.id.item_nombre, transportesFiltrados);
        lvTransporte.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            transportesFiltrados = new ArrayList<>(transportes);
        } else {
            transportesFiltrados = new ArrayList<>();
            for (Transporte t : transportes) {
                String desc = t.getDescripcionTransporte() != null ? t.getDescripcionTransporte() : "";
                if (t.getPlaca().toLowerCase().contains(texto) ||
                    desc.toLowerCase().contains(texto)) {
                    transportesFiltrados.add(t);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Transporte t) {
        TransporteOptionsDialog dialog = TransporteOptionsDialog.newInstance(t);
        dialog.setCallbacks(
            () -> abrirVer(t),
            () -> abrirFormulario(t),
            () -> confirmarEliminar(t)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Transporte t) {
        TransporteViewDialog.newInstance(t)
            .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Transporte t) {
        TransporteFormDialog dialog = TransporteFormDialog.newInstance(t);
        dialog.setOnSaveListener(guardado -> {
            try {
                if (t == null) {
                    transporteDAO.insertar(guardado);
                } else {
                    transporteDAO.actualizar(guardado);
                }
                cargarTransportes();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar transporte", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Transporte t) {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar Transporte")
            .setMessage("¿Desea eliminar el transporte con placa '" + t.getPlaca() + "'?")
            .setPositiveButton("Sí", (d, w) -> {
                try {
                    transporteDAO.eliminar(t.getId());
                    cargarTransportes();
                    Toast.makeText(this, "Transporte eliminado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al eliminar transporte", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }
}
