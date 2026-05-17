package com.ues.sistema_pdm1.activities.marca;

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
import com.ues.sistema_pdm1.models.Marca;
import com.ues.sistema_pdm1.utils.SessionManager;
import com.ues.sistema_pdm1.activities.modelo.ModeloActivity;
import java.util.ArrayList;
import java.util.List;

public class MarcaActivity extends AppCompatActivity {

    private ListView lvMarcas;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<Marca> marcaDAO;
    private List<Marca> marcas        = new ArrayList<>();
    private List<Marca> marcasFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marca);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvMarcas   = findViewById(R.id.lv_marcas);
        btnAgregar = findViewById(R.id.btn_marca_agregar);
        btnBuscar  = findViewById(R.id.btn_marca_lupa);
        etBuscar   = findViewById(R.id.et_marca_buscar);

        marcaDAO = new GenericDAO<>(this, Marca.class, "marca");

        cargarMarcas();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvMarcas.setOnItemClickListener((parent, view, position, id) ->
            abrirDialogOpciones(marcasFiltradas.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
        //Inicio
        lvMarcas.setOnItemLongClickListener((parent, view, position, id) -> {
            Marca marca = marcasFiltradas.get(position);
            Intent intent = new Intent(this, ModeloActivity.class);
            intent.putExtra("ID_MARCA", marca.getId());
            startActivity(intent);
            return true;
        });
        //Fin
    }

    private void cargarMarcas() {
        try {
            marcas = marcaDAO.obtenerTodos();
            marcasFiltradas = new ArrayList<>(marcas);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_marcas), Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Marca> adapter = new ArrayAdapter<>(
            this, R.layout.item_marca, R.id.item_nombre, marcasFiltradas);
        lvMarcas.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            marcasFiltradas = new ArrayList<>(marcas);
        } else {
            marcasFiltradas = new ArrayList<>();
            for (Marca m : marcas) {
                if (m.getNombreMarca().toLowerCase().contains(texto)) {
                    marcasFiltradas.add(m);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Marca marca) {
        MarcaOptionsDialog dialog = MarcaOptionsDialog.newInstance(marca);
        dialog.setCallbacks(
            () -> abrirVer(marca),
            () -> abrirFormulario(marca),
            () -> confirmarEliminar(marca)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Marca marca) {
        MarcaViewDialog.newInstance(marca)
            .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Marca marca) {
        MarcaFormDialog dialog = MarcaFormDialog.newInstance(marca);
        dialog.setOnSaveListener(guardada -> {
            try {
                if (marca == null) {
                    marcaDAO.insertar(guardada);
                } else {
                    marcaDAO.actualizar(guardada);
                }
                cargarMarcas();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error_guardar_marca), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Marca marca) {
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_eliminar_marca))
            .setMessage(getString(R.string.msg_eliminar_marca, marca.getNombreMarca()))
            .setPositiveButton(getString(R.string.yes), (d, w) -> {
                try {
                    marcaDAO.eliminar(marca.getId());
                    cargarMarcas();
                    Toast.makeText(this, getString(R.string.msg_marca_eliminada), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton(getString(R.string.no), null)
            .show();
    }
}