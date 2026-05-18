package com.ues.sistema_pdm1.activities.movimiento;

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
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MovimientoActivity extends AppCompatActivity {

    private LinearLayout containerLista;
    private EditText searchInput;

    private GenericDAO<Movimiento> movimientoDAO;
    private List<Movimiento> movimientos = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> cargarMovimientos());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        containerLista = findViewById(R.id.container_lista_movimiento);
        searchInput    = findViewById(R.id.et_movimiento_buscar);

        movimientoDAO = new GenericDAO<>(this, Movimiento.class, "movimiento");

        findViewById(R.id.btn_movimiento_agregar).setOnClickListener(v -> abrirFormulario(0));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString().trim().toLowerCase());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarMovimientos();
    }

    private void cargarMovimientos() {
        try {
            movimientos = movimientoDAO.obtenerTodos();
        } catch (Exception e) {
            movimientos = new ArrayList<>();
            Toast.makeText(this, getString(R.string.error_cargar_movimientos), Toast.LENGTH_SHORT).show();
        }
        mostrarMovimientos(movimientos);
    }

    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            mostrarMovimientos(movimientos);
            return;
        }
        List<Movimiento> filtrados = new ArrayList<>();
        for (Movimiento m : movimientos) {
            String tipo   = m.getTipoMovimiento()  != null ? m.getTipoMovimiento().toLowerCase()  : "";
            String fecha  = m.getFechaMovimiento() != null ? m.getFechaMovimiento().toLowerCase() : "";
            String motivo = m.getMotivo()          != null ? m.getMotivo().toLowerCase()          : "";
            if (tipo.contains(texto) || fecha.contains(texto) || motivo.contains(texto)) {
                filtrados.add(m);
            }
        }
        mostrarMovimientos(filtrados);
    }

    private void mostrarMovimientos(List<Movimiento> lista) {
        containerLista.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Movimiento m : lista) {
            View card = inflater.inflate(R.layout.item_movimiento, containerLista, false);
            bindCard(card, m);
            containerLista.addView(card);
        }
    }

    private void bindCard(View card, Movimiento m) {
        TextView tvTipoFecha = card.findViewById(R.id.item_tipo_fecha);
        TextView tvMotivo    = card.findViewById(R.id.item_motivo);

        tvTipoFecha.setText(m.getTipoMovimiento() + " — " + m.getFechaMovimiento());
        tvMotivo.setText(m.getMotivo() != null ? m.getMotivo() : "—");

        card.setOnClickListener(v -> abrirVista(m.getId()));
        card.findViewById(R.id.item_btn_editar).setOnClickListener(v -> abrirFormulario(m.getId()));
        View btnEliminar = card.findViewById(R.id.item_btn_eliminar);
        if (SessionManager.getInstance().puedeEliminar()) {
            btnEliminar.setOnClickListener(v -> abrirEliminar(m));
        } else {
            btnEliminar.setVisibility(View.GONE);
        }
    }

    private void abrirFormulario(int id) {
        Intent intent = new Intent(this, MovimientoFormActivity.class);
        intent.putExtra("movimiento_id", id);
        launcher.launch(intent);
    }

    private void abrirVista(int id) {
        Intent intent = new Intent(this, MovimientoViewActivity.class);
        intent.putExtra("movimiento_id", id);
        launcher.launch(intent);
    }

    private void abrirEliminar(Movimiento m) {
        Intent intent = new Intent(this, MovimientoDeleteActivity.class);
        intent.putExtra("movimiento_id", m.getId());
        intent.putExtra("movimiento_label", m.getTipoMovimiento() + " — " + m.getFechaMovimiento());
        launcher.launch(intent);
    }
}
