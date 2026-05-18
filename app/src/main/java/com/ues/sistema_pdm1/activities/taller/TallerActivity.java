package com.ues.sistema_pdm1.activities.taller;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageButton;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Taller;
import com.ues.sistema_pdm1.utils.SessionManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.ues.sistema_pdm1.R;
import java.util.ArrayList;
import java.util.List;

public class TallerActivity extends AppCompatActivity {

    private ListView    lvTalleres;
    private ImageButton btnAgregar, btnBuscar;
    private EditText    etBuscar;

    private GenericDAO<Taller> tallerDAO;
    private List<Taller> talleres          = new ArrayList<>();
    private List<Taller> talleresFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taller);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvTalleres = findViewById(R.id.lv_talleres);
        btnAgregar = findViewById(R.id.btn_taller_agregar);
        btnBuscar  = findViewById(R.id.btn_taller_lupa);
        etBuscar   = findViewById(R.id.et_taller_buscar);

        tallerDAO = new GenericDAO<>(this, Taller.class, "taller");

        cargarTalleres();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvTalleres.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(talleresFiltrados.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarTalleres() {
        try {
            talleres = tallerDAO.obtenerTodos();
            talleresFiltrados = new ArrayList<>(talleres);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_talleres), Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Taller> adapter = new ArrayAdapter<>(
                this, R.layout.item_taller, R.id.item_nombre, talleresFiltrados);
        lvTalleres.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            talleresFiltrados = new ArrayList<>(talleres);
        } else {
            talleresFiltrados = new ArrayList<>();
            for (Taller t : talleres) {
                if (t.getNombreTaller().toLowerCase().contains(texto))
                    talleresFiltrados.add(t);
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Taller taller) {
        TallerOptionsDialog dialog = TallerOptionsDialog.newInstance(taller);
        dialog.setCallbacks(
                () -> abrirVer(taller),
                () -> abrirFormulario(taller),
                () -> confirmarEliminar(taller)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Taller taller) {
        TallerViewDialog.newInstance(taller)
                .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Taller taller) {
        TallerFormDialog dialog = TallerFormDialog.newInstance(taller);
        dialog.setOnSaveListener(guardado -> {
            try {
                if (taller == null) {
                    tallerDAO.insertar(guardado);
                } else {
                    tallerDAO.actualizar(guardado);
                }
                cargarTalleres();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error_guardar_taller), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Taller taller) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_eliminar_taller))
                .setMessage(getString(R.string.msg_eliminar_taller, taller.getNombreTaller()))
                .setPositiveButton(getString(R.string.yes), (d, w) -> {
                    try {
                        tallerDAO.eliminar(taller.getId());
                        cargarTalleres();
                        Toast.makeText(this, getString(R.string.msg_taller_eliminado), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}