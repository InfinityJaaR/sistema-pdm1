package com.ues.sistema_pdm1.activities.movimiento;

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
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MovimientoActivity extends AppCompatActivity {

    private ListView   lvMovimiento;
    private ImageButton btnAgregar, btnBuscar;
    private EditText   etBuscar;

    private GenericDAO<Movimiento> movimientoDAO;
    private List<Movimiento> movimientos         = new ArrayList<>();
    private List<Movimiento> movimientosFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvMovimiento = findViewById(R.id.lv_movimiento);
        btnAgregar   = findViewById(R.id.btn_movimiento_agregar);
        btnBuscar    = findViewById(R.id.btn_movimiento_lupa);
        etBuscar     = findViewById(R.id.et_movimiento_buscar);

        movimientoDAO = new GenericDAO<>(this, Movimiento.class, "movimiento");

        cargarMovimientos();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvMovimiento.setOnItemClickListener((parent, view, position, id) ->
            abrirDialogOpciones(movimientosFiltrados.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarMovimientos() {
        try {
            movimientos = movimientoDAO.obtenerTodos();
            movimientosFiltrados = new ArrayList<>(movimientos);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar movimientos", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Movimiento> adapter = new ArrayAdapter<>(
            this, R.layout.item_movimiento, R.id.item_nombre, movimientosFiltrados);
        lvMovimiento.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            movimientosFiltrados = new ArrayList<>(movimientos);
        } else {
            movimientosFiltrados = new ArrayList<>();
            for (Movimiento m : movimientos) {
                String tipo   = m.getTipoMovimiento()  != null ? m.getTipoMovimiento().toLowerCase()  : "";
                String fecha  = m.getFechaMovimiento() != null ? m.getFechaMovimiento().toLowerCase() : "";
                String motivo = m.getMotivo()          != null ? m.getMotivo().toLowerCase()          : "";
                if (tipo.contains(texto) || fecha.contains(texto) || motivo.contains(texto)) {
                    movimientosFiltrados.add(m);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Movimiento m) {
        MovimientoOptionsDialog dialog = MovimientoOptionsDialog.newInstance(m);
        dialog.setCallbacks(
            () -> abrirVer(m),
            () -> abrirFormulario(m),
            () -> confirmarEliminar(m)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Movimiento m) {
        MovimientoViewDialog.newInstance(m)
            .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Movimiento m) {
        MovimientoFormDialog dialog = MovimientoFormDialog.newInstance(m);
        dialog.setOnSaveListener(guardado -> {
            try {
                if (m == null) {
                    movimientoDAO.insertar(guardado);
                } else {
                    movimientoDAO.actualizar(guardado);
                }
                cargarMovimientos();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar movimiento", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Movimiento m) {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar Movimiento")
            .setMessage("¿Desea eliminar el movimiento '" + m.getTipoMovimiento()
                + "' del " + m.getFechaMovimiento() + "?")
            .setPositiveButton("Sí", (d, w) -> {
                try {
                    movimientoDAO.eliminar(m.getId());
                    cargarMovimientos();
                    Toast.makeText(this, "Movimiento eliminado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al eliminar movimiento", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }
}
