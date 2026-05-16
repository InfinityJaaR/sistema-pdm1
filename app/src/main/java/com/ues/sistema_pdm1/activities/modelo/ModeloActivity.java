package com.ues.sistema_pdm1.activities.modelo;

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
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ModeloActivity extends AppCompatActivity {

    private ListView      lvModelos;
    private ImageButton   btnAgregar, btnBuscar;
    private EditText      etBuscar;

    private GenericDAO<Modelo> modeloDAO;
    private int idMarca;

    private List<Modelo> modelos          = new ArrayList<>();
    private List<Modelo> modelosFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelo);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        idMarca = getIntent().getIntExtra("ID_MARCA", 0);

        lvModelos  = findViewById(R.id.lv_modelos);
        btnAgregar = findViewById(R.id.btn_modelo_agregar);
        btnBuscar  = findViewById(R.id.btn_modelo_lupa);
        etBuscar   = findViewById(R.id.et_modelo_buscar);

        modeloDAO = new GenericDAO<>(this, Modelo.class, "modelo");

        cargarModelos();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvModelos.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(modelosFiltrados.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarModelos() {
        try {
            List<Modelo> todos = modeloDAO.obtenerPor("ID_MARCA", String.valueOf(idMarca));
            modelos = todos != null ? todos : new ArrayList<>();
            modelosFiltrados = new ArrayList<>(modelos);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar modelos", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Modelo> adapter = new ArrayAdapter<>(
                this, R.layout.item_modelo, R.id.item_nombre, modelosFiltrados);
        lvModelos.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            modelosFiltrados = new ArrayList<>(modelos);
        } else {
            modelosFiltrados = new ArrayList<>();
            for (Modelo m : modelos) {
                if (m.getNombreModelo().toLowerCase().contains(texto))
                    modelosFiltrados.add(m);
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Modelo modelo) {
        ModeloOptionsDialog dialog = ModeloOptionsDialog.newInstance(modelo);
        dialog.setCallbacks(
                () -> abrirVer(modelo),
                () -> abrirFormulario(modelo),
                () -> confirmarEliminar(modelo)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Modelo modelo) {
        ModeloViewDialog.newInstance(modelo)
                .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Modelo modelo) {
        ModeloFormDialog dialog = ModeloFormDialog.newInstance(modelo, idMarca);
        dialog.setOnSaveListener(guardado -> {
            try {
                if (modelo == null) {
                    modeloDAO.insertar(guardado);
                } else {
                    modeloDAO.actualizar(guardado);
                }
                cargarModelos();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar modelo", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Modelo modelo) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Modelo")
                .setMessage("¿Desea eliminar '" + modelo.getNombreModelo() + "'?")
                .setPositiveButton("Sí", (d, w) -> {
                    try {
                        modeloDAO.eliminar(modelo.getId());
                        cargarModelos();
                        Toast.makeText(this, "Modelo eliminado", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al eliminar modelo", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}