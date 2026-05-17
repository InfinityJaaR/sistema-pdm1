package com.ues.sistema_pdm1.activities.transporte;

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
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TransporteActivity extends AppCompatActivity {

    private LinearLayout containerLista;
    private EditText searchInput;

    private GenericDAO<Transporte> transporteDAO;
    private List<Transporte> transportes = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> cargarTransportes());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        containerLista = findViewById(R.id.container_lista_transporte);
        searchInput    = findViewById(R.id.et_transporte_buscar);

        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");

        findViewById(R.id.btn_transporte_agregar).setOnClickListener(v -> abrirFormulario(0));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString().trim().toLowerCase());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarTransportes();
    }

    private void cargarTransportes() {
        try {
            transportes = transporteDAO.obtenerTodos();
        } catch (Exception e) {
            transportes = new ArrayList<>();
            Toast.makeText(this, getString(R.string.error_cargar_transportes), Toast.LENGTH_SHORT).show();
        }
        mostrarTransportes(transportes);
    }

    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            mostrarTransportes(transportes);
            return;
        }
        List<Transporte> filtrados = new ArrayList<>();
        for (Transporte t : transportes) {
            String desc = t.getDescripcionTransporte() != null ? t.getDescripcionTransporte().toLowerCase() : "";
            if (t.getPlaca().toLowerCase().contains(texto) || desc.contains(texto)) {
                filtrados.add(t);
            }
        }
        mostrarTransportes(filtrados);
    }

    private void mostrarTransportes(List<Transporte> lista) {
        containerLista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Transporte t : lista) {
            View card = inflater.inflate(R.layout.item_transporte, containerLista, false);
            bindCard(card, t);
            containerLista.addView(card);
        }
    }

    private void bindCard(View card, Transporte t) {
        TextView tvPlaca       = card.findViewById(R.id.item_placa);
        TextView tvDescripcion = card.findViewById(R.id.item_descripcion);

        tvPlaca.setText(t.getPlaca());
        String desc = t.getDescripcionTransporte();
        tvDescripcion.setText((desc != null && !desc.isEmpty()) ? desc : "—");

        card.setOnClickListener(v -> abrirVista(t.getId()));
        card.findViewById(R.id.item_btn_editar).setOnClickListener(v -> abrirFormulario(t.getId()));
        View btnEliminar = card.findViewById(R.id.item_btn_eliminar);
        if (SessionManager.getInstance().puedeEliminar()) {
            btnEliminar.setOnClickListener(v -> abrirEliminar(t));
        } else {
            btnEliminar.setVisibility(View.GONE);
        }
    }

    private void abrirFormulario(int id) {
        Intent intent = new Intent(this, TransporteFormActivity.class);
        intent.putExtra("transporte_id", id);
        launcher.launch(intent);
    }

    private void abrirVista(int id) {
        Intent intent = new Intent(this, TransporteViewActivity.class);
        intent.putExtra("transporte_id", id);
        launcher.launch(intent);
    }

    private void abrirEliminar(Transporte t) {
        Intent intent = new Intent(this, TransporteDeleteActivity.class);
        intent.putExtra("transporte_id", t.getId());
        intent.putExtra("transporte_placa", t.getPlaca());
        launcher.launch(intent);
    }
}
