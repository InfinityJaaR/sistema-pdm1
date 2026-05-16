package com.ues.sistema_pdm1.activities.detalleDesperfecto;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.DetalleDesperfecto;
import com.ues.sistema_pdm1.models.FotoDesperfecto;
import com.ues.sistema_pdm1.models.TipoDesperfecto;
import com.ues.sistema_pdm1.models.Vehiculo;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalleDesperfectoActivity extends AppCompatActivity {

    private static final String TAG = "DetalleDesperfectoAct";
    private ListView lvDetalles;
    private EditText etBuscar;
    private ImageButton btnBuscar, btnAgregar;

    private GenericDAO<DetalleDesperfecto> detalleDAO;
    private GenericDAO<Vehiculo> vehiculoDAO;
    private GenericDAO<TipoDesperfecto> tipoDAO;
    private GenericDAO<FotoDesperfecto> fotoDAO;

    private List<DetalleDesperfecto> listaOriginal = new ArrayList<>();
    private List<DetalleDesperfecto> listaFiltrada = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_desperfecto);

        // 1. Inicializar DAOs
        detalleDAO = new GenericDAO<>(this, DetalleDesperfecto.class, "detalle_desperfecto");
        vehiculoDAO = new GenericDAO<>(this, Vehiculo.class, "vehiculo");
        tipoDAO = new GenericDAO<>(this, TipoDesperfecto.class, "tipo_desperfecto");
        fotoDAO = new GenericDAO<>(this, FotoDesperfecto.class, "foto_desperfecto");

        // 2. Vincular vistas
        lvDetalles = findViewById(R.id.lv_detalles);
        btnAgregar = findViewById(R.id.btn_detalle_agregar);
        btnBuscar  = findViewById(R.id.btn_detalle_lupa);
        etBuscar   = findViewById(R.id.et_detalle_buscar);

        // 3. Configurar Eventos
        btnAgregar.setOnClickListener(v -> {
            DetalleDesperfectoFormDialog dialog = DetalleDesperfectoFormDialog.newInstance();
            dialog.setOnDetalleGuardadoListener(this::cargarDatos);
            dialog.show(getSupportFragmentManager(), "FormDesperfecto");
        });

        // Configurar búsqueda
        btnBuscar.setOnClickListener(v -> filtrar());
        
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrar(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 4. Menú de opciones
        lvDetalles.setOnItemClickListener((parent, view, position, id) -> {
            DetalleDesperfecto seleccionado = listaFiltrada.get(position);
            DetalleDesperfectoOptionsDialog options = DetalleDesperfectoOptionsDialog.newInstance(seleccionado);
            options.setCallbacks(
                    () -> abrirVer(seleccionado),
                    () -> abrirEditar(seleccionado),
                    () -> abrirConfirmacionEliminar(seleccionado)
            );
            options.show(getSupportFragmentManager(), "OptionsDialog");
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            listaOriginal = detalleDAO.obtenerTodos();
            if (listaOriginal == null) listaOriginal = new ArrayList<>();
            
            // Mostrar el más reciente primero (invierte la lista de la BD)
            Collections.reverse(listaOriginal);
            
            listaFiltrada = new ArrayList<>(listaOriginal);
            refrescarLista();
        } catch (SQLException e) {
            Log.e(TAG, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void refrescarLista() {
        ArrayAdapter<DetalleDesperfecto> adapter = new ArrayAdapter<DetalleDesperfecto>(
                this, R.layout.item_detalle_desperfecto, listaFiltrada) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_detalle_desperfecto, parent, false);
                }
                DetalleDesperfecto item = getItem(position);
                TextView tvVehiculo = convertView.findViewById(R.id.tv_item_vehiculo);
                TextView tvTipo = convertView.findViewById(R.id.tv_item_tipo_dano);
                TextView tvFecha = convertView.findViewById(R.id.tv_item_fecha);

                if (item != null) {
                    try {
                        Vehiculo v = vehiculoDAO.obtenerPorId(item.getIdVehiculo());
                        tvVehiculo.setText(v != null ? "Vehículo: " + v.getVin() : "ID: " + item.getIdVehiculo());
                        TipoDesperfecto t = tipoDAO.obtenerPorId(item.getIdTipoDesperfecto());
                        tvTipo.setText(t != null ? t.getNombreTipoDesperfecto() : "Desconocido");
                    } catch (SQLException e) {
                        tvVehiculo.setText("Error");
                    }
                    tvFecha.setText(item.getFechaRegistro());
                }
                return convertView;
            }
        };
        lvDetalles.setAdapter(adapter);
    }

    private void filtrar() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        if (texto.isEmpty()) {
            listaFiltrada = new ArrayList<>(listaOriginal);
        } else {
            listaFiltrada = new ArrayList<>();
            for (DetalleDesperfecto d : listaOriginal) {
                boolean coincide = false;
                
                // 1. Buscar en descripción
                if (d.getDescripcionDetalle().toLowerCase().contains(texto)) {
                    coincide = true;
                } 
                
                // 2. Buscar en VIN del Vehículo
                if (!coincide) {
                    try {
                        Vehiculo v = vehiculoDAO.obtenerPorId(d.getIdVehiculo());
                        if (v != null && v.getVin().toLowerCase().contains(texto)) {
                            coincide = true;
                        }
                    } catch (SQLException ignored) {}
                }

                // 3. Buscar en el nombre del Tipo de Desperfecto
                if (!coincide) {
                    try {
                        TipoDesperfecto t = tipoDAO.obtenerPorId(d.getIdTipoDesperfecto());
                        if (t != null && t.getNombreTipoDesperfecto().toLowerCase().contains(texto)) {
                            coincide = true;
                        }
                    } catch (SQLException ignored) {}
                }

                if (coincide) {
                    listaFiltrada.add(d);
                }
            }
        }
        refrescarLista();
    }

    private void abrirVer(DetalleDesperfecto d) {
        DetalleDesperfectoFormDialog.newInstance(d, true)
                .show(getSupportFragmentManager(), "ViewDialog");
    }

    private void abrirEditar(DetalleDesperfecto d) {
        DetalleDesperfectoFormDialog form = DetalleDesperfectoFormDialog.newInstance(d, false);
        form.setOnDetalleGuardadoListener(this::cargarDatos);
        form.show(getSupportFragmentManager(), "EditDialog");
    }

    private void abrirConfirmacionEliminar(DetalleDesperfecto d) {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);
        TextView tvMsg = view.findViewById(R.id.confirmacion_text);
        tvMsg.setText("¿Desea eliminar este desperfecto y sus fotos?\n(" + d.getDescripcionDetalle() + ")");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        view.findViewById(R.id.btn_no).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_si).setOnClickListener(v -> {
            try {
                // Limpiar fotos asociadas
                List<FotoDesperfecto> fotos = fotoDAO.obtenerPor("ID_DETALLE_DESPERFECTO", String.valueOf(d.getId()));
                for (FotoDesperfecto f : fotos) {
                    new File(f.getRutaImagen()).delete();
                    fotoDAO.eliminar(f.getId());
                }
                detalleDAO.eliminar(d.getId());
                cargarDatos();
                dialog.dismiss();
                Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
