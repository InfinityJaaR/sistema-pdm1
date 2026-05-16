package com.ues.sistema_pdm1.activities.personalInterno;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PersonalInternoActivity extends AppCompatActivity {

    private LinearLayout containerLista;
    private EditText searchInput;

    private GenericDAO<PersonalInterno> personalDAO;
    private List<PersonalInterno> personal = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> cargarPersonal());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_interno);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        containerLista = findViewById(R.id.container_lista_personal);
        searchInput    = findViewById(R.id.et_personal_buscar);

        personalDAO = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");

        findViewById(R.id.btn_personal_agregar).setOnClickListener(v -> abrirFormulario(0));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString().trim().toLowerCase());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarPersonal();
    }

    private void cargarPersonal() {
        try {
            personal = personalDAO.obtenerTodos();
        } catch (Exception e) {
            personal = new ArrayList<>();
            Toast.makeText(this, "Error al cargar personal", Toast.LENGTH_SHORT).show();
        }
        mostrarPersonal(personal);
    }

    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            mostrarPersonal(personal);
            return;
        }
        List<PersonalInterno> filtrados = new ArrayList<>();
        for (PersonalInterno p : personal) {
            String nombre = (p.getNombrePersonal() + " " + p.getApellidoPersonal()).toLowerCase();
            String cargo  = p.getCargo() != null ? p.getCargo().toLowerCase() : "";
            if (nombre.contains(texto) || cargo.contains(texto)) {
                filtrados.add(p);
            }
        }
        mostrarPersonal(filtrados);
    }

    private void mostrarPersonal(List<PersonalInterno> lista) {
        containerLista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (PersonalInterno p : lista) {
            View card = inflater.inflate(R.layout.item_personal_interno, containerLista, false);
            bindCard(card, p);
            containerLista.addView(card);
        }
    }

    private void bindCard(View card, PersonalInterno p) {
        TextView tvNombre = card.findViewById(R.id.item_nombre);
        TextView tvCargo  = card.findViewById(R.id.item_cargo);

        tvNombre.setText(p.getNombrePersonal() + " " + p.getApellidoPersonal());
        tvCargo.setText(p.getCargo() != null ? p.getCargo() : "—");

        card.setOnClickListener(v -> abrirVista(p.getId()));
        card.findViewById(R.id.item_btn_editar).setOnClickListener(v -> abrirFormulario(p.getId()));
        card.findViewById(R.id.item_btn_eliminar).setOnClickListener(v -> abrirEliminar(p));
    }

    private void abrirFormulario(int id) {
        Intent intent = new Intent(this, PersonalInternoFormActivity.class);
        intent.putExtra("personal_id", id);
        launcher.launch(intent);
    }

    private void abrirVista(int id) {
        Intent intent = new Intent(this, PersonalInternoViewActivity.class);
        intent.putExtra("personal_id", id);
        launcher.launch(intent);
    }

    private void abrirEliminar(PersonalInterno p) {
        Intent intent = new Intent(this, PersonalInternoDeleteActivity.class);
        intent.putExtra("personal_id", p.getId());
        intent.putExtra("personal_nombre", p.getNombrePersonal() + " " + p.getApellidoPersonal());
        launcher.launch(intent);
    }
}
