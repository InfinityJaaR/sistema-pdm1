package com.ues.sistema_pdm1.activities.bodega;

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
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BodegaActivity extends AppCompatActivity {
    private ListView lvBodegas;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<Bodega> bodegaDAO;
    private List<Bodega> bodega        = new ArrayList<>();
    private List<Bodega> bodegaFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodega);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvBodegas   = findViewById(R.id.lv_bodegas);
        btnAgregar = findViewById(R.id.btn_bodega_agregar);
        btnBuscar  = findViewById(R.id.btn_bodega_lupa);
        etBuscar   = findViewById(R.id.et_bodega_buscar);

        bodegaDAO = new GenericDAO<>(this, Bodega.class, "BODEGA");

        cargarBodegas();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvBodegas.setOnItemClickListener((parent, view, position, id) ->
            abrirDialogOpciones(bodegaFiltradas.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarBodegas() {
        try {
            bodega = bodegaDAO.obtenerTodos();
            bodegaFiltradas = new ArrayList<>(bodega);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar bodegas", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Bodega> adapter = new ArrayAdapter<>(
            this, R.layout.item_bodega, R.id.item_nombre, bodegaFiltradas);
        lvBodegas.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            bodegaFiltradas = new ArrayList<>(bodega);
            } else {
            bodegaFiltradas = new ArrayList<>();
            for (Bodega b : bodega) {
                if (b.getNombreBodega().toLowerCase().contains(texto)) {
                    bodegaFiltradas.add(b);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Bodega bodega) {
        BodegaOptionsDialog dialog = BodegaOptionsDialog.newInstance(bodega);
        dialog.setCallbacks(
            () -> abrirVer(bodega),
            () -> abrirFormulario(bodega),
            () -> confirmarEliminar(bodega)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Bodega bodega) {
        BodegaViewDialog.newInstance(bodega)
            .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Bodega bodega) {
        BodegaFormDialog dialog = BodegaFormDialog.newInstance(bodega);
        dialog.setOnSaveListener(guardada -> {
            try {
                if (bodega == null) {
                    bodegaDAO.insertar(guardada);
                } else {
                    bodegaDAO.actualizar(guardada);
                }
                cargarBodegas();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar bodega", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Bodega bodega) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Bodega")
                .setMessage("¿Desea eliminar '" + bodega.getNombreBodega() + "'?")
                .setPositiveButton("Sí", (d, w) -> {
                    try {
                        bodegaDAO.eliminar(bodega.getId());
                        cargarBodegas();
                        Toast.makeText(this, "Bodega eliminada", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
