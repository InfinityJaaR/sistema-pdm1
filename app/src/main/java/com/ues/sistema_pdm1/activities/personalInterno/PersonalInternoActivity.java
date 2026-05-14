package com.ues.sistema_pdm1.activities.personalInterno;

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
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PersonalInternoActivity extends AppCompatActivity {

    private ListView lvPersonal;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<PersonalInterno> personalDAO;
    private List<PersonalInterno> personal         = new ArrayList<>();
    private List<PersonalInterno> personalFiltrado = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_interno);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvPersonal = findViewById(R.id.lv_personal);
        btnAgregar = findViewById(R.id.btn_personal_agregar);
        btnBuscar  = findViewById(R.id.btn_personal_lupa);
        etBuscar   = findViewById(R.id.et_personal_buscar);

        personalDAO = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");

        cargarPersonal();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvPersonal.setOnItemClickListener((parent, view, position, id) ->
            abrirDialogOpciones(personalFiltrado.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarPersonal() {
        try {
            personal = personalDAO.obtenerTodos();
            personalFiltrado = new ArrayList<>(personal);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar personal", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<PersonalInterno> adapter = new ArrayAdapter<>(
            this, R.layout.item_personal_interno, R.id.item_nombre, personalFiltrado);
        lvPersonal.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            personalFiltrado = new ArrayList<>(personal);
        } else {
            personalFiltrado = new ArrayList<>();
            for (PersonalInterno p : personal) {
                String nombre = (p.getNombrePersonal() + " " + p.getApellidoPersonal()).toLowerCase();
                if (nombre.contains(texto) || p.getCargo().toLowerCase().contains(texto)) {
                    personalFiltrado.add(p);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(PersonalInterno p) {
        PersonalInternoOptionsDialog dialog = PersonalInternoOptionsDialog.newInstance(p);
        dialog.setCallbacks(
            () -> abrirVer(p),
            () -> abrirFormulario(p),
            () -> confirmarEliminar(p)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(PersonalInterno p) {
        PersonalInternoViewDialog.newInstance(p)
            .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(PersonalInterno p) {
        PersonalInternoFormDialog dialog = PersonalInternoFormDialog.newInstance(p);
        dialog.setOnSaveListener(guardado -> {
            try {
                if (p == null) {
                    personalDAO.insertar(guardado);
                } else {
                    personalDAO.actualizar(guardado);
                }
                cargarPersonal();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar personal", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(PersonalInterno p) {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar Personal")
            .setMessage("¿Desea eliminar a '" + p.getNombrePersonal() + " " + p.getApellidoPersonal() + "'?")
            .setPositiveButton("Sí", (d, w) -> {
                try {
                    personalDAO.eliminar(p.getId());
                    cargarPersonal();
                    Toast.makeText(this, "Personal eliminado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al eliminar personal", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }
}