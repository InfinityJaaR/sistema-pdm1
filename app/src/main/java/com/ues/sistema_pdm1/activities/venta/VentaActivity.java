package com.ues.sistema_pdm1.activities.venta;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Venta;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class VentaActivity extends AppCompatActivity {

    private ListView lvVentas;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<Venta> ventaDAO;
    private List<Venta> ventas = new ArrayList<>();
    private List<Venta> ventasFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        if (!SessionManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        lvVentas = findViewById(R.id.lv_ventas);
        btnAgregar = findViewById(R.id.btn_venta_agregar);
        btnBuscar = findViewById(R.id.btn_venta_lupa);
        etBuscar = findViewById(R.id.et_venta_buscar);

        ventaDAO = new GenericDAO<>(this, Venta.class, "venta");

        cargarVentas();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));

        lvVentas.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(ventasFiltradas.get(position)));

        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });
    }

    private void cargarVentas() {
        try {
            ventas = ventaDAO.obtenerTodos();
            ventasFiltradas = new ArrayList<>(ventas);
            refrescarLista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar ventas", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        VentaAdapter adapter = new VentaAdapter(this, ventasFiltradas);
        lvVentas.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            ventasFiltradas = new ArrayList<>(ventas);
        } else {
            ventasFiltradas = new ArrayList<>();
            for (Venta v : ventas) {
                // Filtrar por ID de venta o algún otro criterio si fuera necesario
                if (String.valueOf(v.getId()).contains(texto)) {
                    ventasFiltradas.add(v);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Venta venta) {
        VentaOptionsDialog dialog = VentaOptionsDialog.newInstance(venta);
        dialog.setCallbacks(
                () -> abrirVer(venta),
                () -> abrirFormulario(venta),
                () -> confirmarEliminar(venta)
        );
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Venta venta) {
        VentaViewDialog.newInstance(venta)
                .show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Venta venta) {
        VentaFormDialog dialog = VentaFormDialog.newInstance(venta);
        dialog.setOnSaveListener(guardada -> {
            try {
                if (venta == null) {
                    ventaDAO.insertar(guardada);
                } else {
                    ventaDAO.actualizar(guardada);
                }
                cargarVentas();
                Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar venta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Venta venta) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Venta")
                .setMessage("¿Desea eliminar la venta #" + venta.getId() + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    try {
                        ventaDAO.eliminar(venta.getId());
                        cargarVentas();
                        Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private class VentaAdapter extends ArrayAdapter<Venta> {
        public VentaAdapter(android.content.Context context, List<Venta> objetos) {
            super(context, 0, objetos);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_venta, parent, false);
            }

            Venta v = getItem(position);

            TextView tvTitulo = convertView.findViewById(R.id.item_venta_titulo);
            TextView tvFecha = convertView.findViewById(R.id.item_venta_fecha);
            TextView tvPrecio = convertView.findViewById(R.id.item_venta_precio);

            if (v != null) {
                tvTitulo.setText("Venta #" + v.getId());
                tvFecha.setText(v.getFechaVenta());
                tvPrecio.setText(String.format(java.util.Locale.getDefault(), "$%.2f", v.getPrecio()));
            }

            return convertView;
        }
    }
}
