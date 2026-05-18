package com.ues.sistema_pdm1.activities.vehiculo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.models.Marca;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Seccion;
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
    private GenericDAO<Seccion> seccionDAO;
    private GenericDAO<Bodega> bodegaDAO;

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
        seccionDAO = new GenericDAO<>(this, Seccion.class, "seccion");
        bodegaDAO = new GenericDAO<>(this, Bodega.class, "bodega");

        cargarVehiculos();

        btnAgregar.setOnClickListener(v -> abrirFormulario(null));
        btnBuscar.setOnClickListener(v -> filtrar());

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrar(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvVehiculos.setOnItemClickListener((parent, view, position, id) ->
                abrirDialogOpciones(vehiculosFiltrados.get(position)));
    }

    private void cargarVehiculos() {
        try {
            vehiculos = vehiculoDAO.obtenerTodos();
            if (vehiculos != null) {
                Collections.reverse(vehiculos);
            } else {
                vehiculos = new ArrayList<>();
            }
            filtrar();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_vehiculos), Toast.LENGTH_SHORT).show();
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
                        tvH.setText(titulo.trim());
                        tvS.setText(getString(R.string.label_vin) + ": " + v.getVin() + " | " + v.getEstadoVehiculo());
                    } catch (Exception e) {
                        tvH.setText(getString(R.string.label_vin) + ": " + v.getVin());
                        tvS.setText(v.getEstadoVehiculo());
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
                boolean coincide = false;
                if (v.getVin().toLowerCase().contains(texto)) coincide = true;
                if (!coincide && v.getEstadoVehiculo().toLowerCase().contains(texto)) coincide = true;
                if (!coincide && String.valueOf(v.getAnio()).contains(texto)) coincide = true;
                if (!coincide) {
                    try {
                        Modelo m = modeloDAO.obtenerPorId(v.getIdModelo());
                        if (m != null) {
                            if (m.getNombreModelo().toLowerCase().contains(texto)) {
                                coincide = true;
                            } else {
                                Marca ma = marcaDAO.obtenerPorId(m.getIdMarca());
                                if (ma != null && ma.getNombreMarca().toLowerCase().contains(texto)) coincide = true;
                            }
                        }
                    } catch (Exception ignored) {}
                }
                if (coincide) vehiculosFiltrados.add(v);
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
                Toast.makeText(this, getString(R.string.msg_vehiculo_guardado), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "form");
    }

    private void confirmarEliminar(Vehiculo v) {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);
        ((TextView)view.findViewById(R.id.confirmacion_text)).setText(getString(R.string.msg_confirmar_eliminar_vehiculo, v.getVin()));
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this).setView(view).create();

        view.findViewById(R.id.btn_no).setOnClickListener(view1 -> dialog.dismiss());
        view.findViewById(R.id.btn_si).setOnClickListener(view1 -> {
            try {
                vehiculoDAO.eliminar(v.getId());
                cargarVehiculos();
                Toast.makeText(this, getString(R.string.msg_vehiculo_eliminado), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
