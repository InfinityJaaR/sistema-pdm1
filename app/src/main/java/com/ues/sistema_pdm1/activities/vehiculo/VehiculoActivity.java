package com.ues.sistema_pdm1.activities.vehiculo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
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
import com.ues.sistema_pdm1.models.Marca;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehiculoActivity extends AppCompatActivity {

    private ListView lvVehiculos;
    private ImageButton btnAgregar, btnBuscar;
    private EditText etBuscar;

    private GenericDAO<Vehiculo> vehiculoDAO;
    private GenericDAO<Modelo> modeloDAO;
    private GenericDAO<Marca> marcaDAO;
    
    private List<Vehiculo> vehiculos = new ArrayList<>();
    private List<Vehiculo> vehiculosFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        lvVehiculos = findViewById(R.id.lv_vehiculo_lista);
        btnAgregar = findViewById(R.id.btn_vehiculo_agregar);
        btnBuscar = findViewById(R.id.btn_vehiculo_lupa);
        etBuscar = findViewById(R.id.et_vehiculo_buscar);

        vehiculoDAO = new GenericDAO<>(this, Vehiculo.class, "vehiculo");
        modeloDAO = new GenericDAO<>(this, Modelo.class, "modelo");
        marcaDAO = new GenericDAO<>(this, Marca.class, "marca");

        cargarVehiculos();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));
        btnBuscar.setOnClickListener(v -> filtrar());

        // Filtro en tiempo real para mejor experiencia
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrar(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrar();
                return true;
            }
            return false;
        });

        lvVehiculos.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(vehiculosFiltrados.get(position)));
    }

    private void cargarVehiculos() {
        try {
            vehiculos = vehiculoDAO.obtenerTodos();
            if (vehiculos != null) {
                Collections.reverse(vehiculos); // Más recientes primero
            } else {
                vehiculos = new ArrayList<>();
            }
            // Después de cargar, aplicamos el filtro actual (o lista completa si está vacío)
            filtrar();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar vehículos", Toast.LENGTH_SHORT).show();
        }
    }

    private void refrescarLista() {
        ArrayAdapter<Vehiculo> adapter = new ArrayAdapter<Vehiculo>(
                this, R.layout.item_vehiculo, vehiculosFiltrados) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_vehiculo, parent, false);
                }
                Vehiculo v = getItem(position);
                TextView tvH = convertView.findViewById(R.id.item_headen);
                TextView tvS = convertView.findViewById(R.id.item_subdetalles);

                if (v != null) {
                    try {
                        Modelo m = modeloDAO.obtenerPorId(v.getIdModelo());
                        Marca ma = (m != null) ? marcaDAO.obtenerPorId(m.getIdMarca()) : null;
                        String titulo = v.getAnio() + " " + (ma != null ? ma.getNombreMarca() : "") + " " + (m != null ? m.getNombreModelo() : "");
                        tvH.setText(titulo);
                        tvS.setText("VIN: " + v.getVin() + " | " + v.getEstadoVehiculo());
                    } catch (Exception e) {
                        tvH.setText("VIN: " + v.getVin());
                    }
                }
                return convertView;
            }
        };
        lvVehiculos.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        vehiculosFiltrados = new ArrayList<>();
        if (texto.isEmpty()) {
            vehiculosFiltrados.addAll(vehiculos);
        } else {
            for (Vehiculo v : vehiculos) {
                if (v.getVin().toLowerCase().contains(texto)) {
                    vehiculosFiltrados.add(v);
                }
            }
        }
        refrescarLista();
    }

    private void abrirDialogOpciones(Vehiculo v) {
        VehiculoOptionsDialog dialog = VehiculoOptionsDialog.newInstance(v);
        dialog.setCallbacks(() -> abrirVer(v), () -> abrirFormulario(v), () -> confirmarEliminar(v));
        dialog.show(getSupportFragmentManager(), "opciones");
    }

    private void abrirVer(Vehiculo v) {
        VehiculoViewDialog.newInstance(v).show(getSupportFragmentManager(), "ver");
    }

    private void abrirFormulario(Vehiculo v) {
        VehiculoFormDialog dialog = VehiculoFormDialog.newInstance(v);
        dialog.setOnSaveListener(veh -> {
            try {
                if (v == null) vehiculoDAO.insertar(veh); else vehiculoDAO.actualizar(veh);
                cargarVehiculos();
                Toast.makeText(this, "Vehículo guardado", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Vehiculo v) {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);
        ((TextView)view.findViewById(R.id.confirmacion_text)).setText("¿Eliminar vehículo VIN: " + v.getVin() + "?");
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        view.findViewById(R.id.btn_no).setOnClickListener(view1 -> dialog.dismiss());
        view.findViewById(R.id.btn_si).setOnClickListener(view1 -> {
            try {
                vehiculoDAO.eliminar(v.getId());
                cargarVehiculos();
                dialog.dismiss();
            } catch (Exception e) { }
        });
        dialog.show();
    }
}
