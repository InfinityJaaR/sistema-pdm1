package com.ues.sistema_pdm1.activities.importador;

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
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.TelefonoImportador;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ImportadorActivity extends AppCompatActivity {

    private LinearLayout containerLista;
    private EditText searchInput;

    private GenericDAO<Importador> importadorDAO;
    private GenericDAO<TelefonoImportador> telefonoDAO;

    private List<Importador> importadores = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> cargarImportadores());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_importer_list);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        containerLista = findViewById(R.id.container_lista_importadores);
        searchInput    = findViewById(R.id.search_input_importer);

        importadorDAO = new GenericDAO<>(this, Importador.class, "importador");
        telefonoDAO   = new GenericDAO<>(this, TelefonoImportador.class, "telefono_importador");

        findViewById(R.id.fab_agregar).setOnClickListener(v -> abrirFormulario(0));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString().trim().toLowerCase());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarImportadores();
    }

    private void cargarImportadores() {
        try {
            importadores = importadorDAO.obtenerTodos();
        } catch (Exception e) {
            importadores = new ArrayList<>();
            Toast.makeText(this, "Error al cargar importadores", Toast.LENGTH_SHORT).show();
        }
        mostrarImportadores(importadores);
    }

    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            mostrarImportadores(importadores);
            return;
        }
        List<Importador> filtrados = new ArrayList<>();
        for (Importador imp : importadores) {
            String nombre = (imp.getNombreImportador() + " " + imp.getApellidoImportador()).toLowerCase();
            if (nombre.contains(texto) || imp.getNui().toLowerCase().contains(texto)) {
                filtrados.add(imp);
            }
        }
        mostrarImportadores(filtrados);
    }

    private void mostrarImportadores(List<Importador> lista) {
        containerLista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Importador imp : lista) {
            View card = inflater.inflate(R.layout.item_importador, containerLista, false);
            bindCard(card, imp);
            containerLista.addView(card);
        }
    }

    private void bindCard(View card, Importador imp) {
        TextView tvNombre    = card.findViewById(R.id.item_nombre);
        TextView tvNui       = card.findViewById(R.id.item_nui);
        TextView tvEmail     = card.findViewById(R.id.item_email);
        TextView tvTelefono  = card.findViewById(R.id.item_telefono);
        TextView tvDireccion = card.findViewById(R.id.item_direccion);

        tvNombre.setText(nombreCompleto(imp));
        tvNui.setText("NUI: " + imp.getNui());

        String email = imp.getCorreoElectronico();
        tvEmail.setText((email != null && !email.isEmpty()) ? email : "—");

        String dir = imp.getDireccionImportador();
        tvDireccion.setText((dir != null && !dir.isEmpty()) ? dir : "—");

        List<TelefonoImportador> tels = telefonoDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(imp.getId()));
        tvTelefono.setText(tels.isEmpty() ? "—" : tels.get(0).getNumero());

        card.setOnClickListener(v -> abrirVista(imp.getId()));
        card.findViewById(R.id.item_btn_editar).setOnClickListener(v -> abrirFormulario(imp.getId()));
        card.findViewById(R.id.item_btn_eliminar).setOnClickListener(v -> abrirEliminar(imp));
    }

    private String nombreCompleto(Importador imp) {
        String base = imp.getNombreImportador() + " " + imp.getApellidoImportador();
        String apCasada = imp.getApellidoCasada();
        if (apCasada != null && !apCasada.isEmpty()) {
            return base + " de " + apCasada;
        }
        return base;
    }

    private void abrirFormulario(int id) {
        Intent intent = new Intent(this, ImportadorFormActivity.class);
        intent.putExtra("importador_id", id);
        launcher.launch(intent);
    }

    private void abrirVista(int id) {
        Intent intent = new Intent(this, ImportadorViewActivity.class);
        intent.putExtra("importador_id", id);
        launcher.launch(intent);
    }

    private void abrirEliminar(Importador imp) {
        Intent intent = new Intent(this, ImportadorDeleteActivity.class);
        intent.putExtra("importador_id", imp.getId());
        intent.putExtra("importador_nombre", nombreCompleto(imp));
        launcher.launch(intent);
    }
}
