package com.ues.sistema_pdm1.activities.seccion;

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
import com.ues.sistema_pdm1.models.Seccion;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SeccionActivity extends AppCompatActivity {
    private ListView lvSecciones;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<Seccion> seccionDAO;
    private GenericDAO<Bodega> bodegaDAO;
    private List<Seccion> seccion        = new ArrayList<>();
    private List<Seccion> seccionFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvSecciones   = findViewById(R.id.lv_seccion);
        btnAgregar = findViewById(R.id.btn_seccion_agregar);
        btnBuscar  = findViewById(R.id.btn_seccion_lupa);
        etBuscar   = findViewById(R.id.et_seccion_buscar);

        seccionDAO = new GenericDAO<>(this, Seccion.class, "SECCION");
        bodegaDAO = new GenericDAO<>(this, Bodega.class, "BODEGA");

        cargarSecciones();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvSecciones.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(seccionFiltradas.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarSecciones() {
        try {
            seccion = seccionDAO.obtenerTodos();
            // Cargar el nombre de la bodega para cada sección
            for (Seccion s : seccion) {
                try {
                    Bodega b = bodegaDAO.obtenerPorId(s.getIdBodega());
                    if (b != null) {
                        s.setNombreBodega(b.getNombreBodega());
                    }
                } catch (Exception ignored) {}
            }
            seccionFiltradas = new ArrayList<>(seccion);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar secciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Seccion> adapter = new ArrayAdapter<>(
                this, R.layout.item_seccion, R.id.item_nombre, seccionFiltradas);
        lvSecciones.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            seccionFiltradas = new ArrayList<>(seccion);
        } else {
            seccionFiltradas = new ArrayList<>();
            for (Seccion s : seccion) {
                String nivelStr = String.valueOf(s.getNivel());
                String bodegaNombre = s.getNombreBodega() != null ? s.getNombreBodega().toLowerCase() : "";
                
                if (nivelStr.contains(texto) || bodegaNombre.contains(texto)) {
                    seccionFiltradas.add(s);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Seccion seccion) {
        SeccionOptionsDialog dialog = SeccionOptionsDialog.newInstance(seccion);
        dialog.setCallbacks(
                () -> abrirVer(seccion),
                () -> abrirFormulario(seccion),
                () -> confirmarEliminar(seccion)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Seccion seccion) {
        SeccionViewDialog.newInstance(seccion)
                .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Seccion seccion) {
        SeccionFormDialog dialog = SeccionFormDialog.newInstance(seccion);
        dialog.setOnSaveListener(guardada -> {
            try {
                if (seccion == null) {
                    seccionDAO.insertar(guardada);
                } else {
                    seccionDAO.actualizar(guardada);
                }
                cargarSecciones();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar seccion", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Seccion seccion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Seccion")
                .setMessage("¿Desea eliminar '" + seccion.toString() + "'?")
                .setPositiveButton("Sí", (d, w) -> {
                    try {
                        seccionDAO.eliminar(seccion.getId());
                        cargarSecciones();
                        Toast.makeText(this, "Seccion eliminada", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
