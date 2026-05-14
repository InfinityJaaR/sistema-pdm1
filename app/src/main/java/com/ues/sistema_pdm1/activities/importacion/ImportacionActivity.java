package com.ues.sistema_pdm1.activities.importacion;

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
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ImportacionActivity extends AppCompatActivity {

    private LinearLayout containerLista;
    private EditText searchInput;

    private GenericDAO<Importacion> importacionDAO;
    private GenericDAO<Importador> importadorDAO;

    private List<Importacion> importaciones = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> cargarImportaciones());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacion_list);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        containerLista = findViewById(R.id.container_lista_importaciones);
        searchInput    = findViewById(R.id.search_input_importacion);

        importacionDAO = new GenericDAO<>(this, Importacion.class, "importacion");
        importadorDAO  = new GenericDAO<>(this, Importador.class, "importador");

        findViewById(R.id.fab_agregar_importacion).setOnClickListener(v -> abrirFormulario(0));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarImportaciones();
    }

    private void cargarImportaciones() {
        try {
            importaciones = importacionDAO.obtenerTodos();
        } catch (Exception e) {
            importaciones = new ArrayList<>();
            Toast.makeText(this, "Error al cargar importaciones", Toast.LENGTH_SHORT).show();
        }
        mostrarImportaciones(importaciones);
    }

    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            mostrarImportaciones(importaciones);
            return;
        }
        List<Importacion> filtradas = new ArrayList<>();
        for (Importacion imp : importaciones) {
            if (imp.getFechaImportacion().contains(texto)) filtradas.add(imp);
        }
        mostrarImportaciones(filtradas);
    }

    private void mostrarImportaciones(List<Importacion> lista) {
        containerLista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Importacion imp : lista) {
            View card = inflater.inflate(R.layout.item_importacion, containerLista, false);
            bindCard(card, imp);
            containerLista.addView(card);
        }
    }

    private void bindCard(View card, Importacion imp) {
        TextView tvImportador = card.findViewById(R.id.item_importacion_importador);
        TextView tvFecha      = card.findViewById(R.id.item_importacion_fecha);

        String nombreImp = nombreImportador(imp.getIdImportador());
        tvImportador.setText(nombreImp);
        tvFecha.setText("Fecha: " + imp.getFechaImportacion());

        card.setOnClickListener(v -> abrirVista(imp.getId()));
        card.findViewById(R.id.item_importacion_btn_editar).setOnClickListener(v -> abrirFormulario(imp.getId()));
        card.findViewById(R.id.item_importacion_btn_eliminar).setOnClickListener(v -> abrirEliminar(imp));
    }

    private String nombreImportador(int idImportador) {
        try {
            Importador imp = importadorDAO.obtenerPorId(idImportador);
            return imp != null
                ? imp.getNombreImportador() + " " + imp.getApellidoImportador()
                : "Importador #" + idImportador;
        } catch (Exception e) {
            return "Importador #" + idImportador;
        }
    }

    private void abrirFormulario(int id) {
        Intent intent = new Intent(this, ImportacionFormActivity.class);
        intent.putExtra("importacion_id", id);
        launcher.launch(intent);
    }

    private void abrirVista(int id) {
        Intent intent = new Intent(this, ImportacionViewActivity.class);
        intent.putExtra("importacion_id", id);
        launcher.launch(intent);
    }

    private void abrirEliminar(Importacion imp) {
        Intent intent = new Intent(this, ImportacionDeleteActivity.class);
        intent.putExtra("importacion_id", imp.getId());
        intent.putExtra("importacion_label", "Importación del " + imp.getFechaImportacion());
        launcher.launch(intent);
    }
}
