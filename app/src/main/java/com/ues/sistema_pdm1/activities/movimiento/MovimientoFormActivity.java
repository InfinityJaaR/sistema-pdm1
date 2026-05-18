package com.ues.sistema_pdm1.activities.movimiento;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MovimientoFormActivity extends AppCompatActivity {

    private TextView formTitle;
    private AutoCompleteTextView actvTipo, actvVehiculo, actvTransporte, actvPersonal, actvBodega;
    private EditText etFecha, etMotivo;
    private Button btnCancelar, btnGuardar;

    private GenericDAO<Movimiento> movimientoDAO;
    private GenericDAO<Vehiculo> vehiculoDAO;
    private GenericDAO<Transporte> transporteDAO;
    private GenericDAO<PersonalInterno> personalDAO;
    private GenericDAO<Bodega> bodegaDAO;

    private List<Vehiculo> vehiculos = new ArrayList<>();
    private List<Transporte> transportes = new ArrayList<>();
    private List<PersonalInterno> personal = new ArrayList<>();
    private List<Bodega> bodegas = new ArrayList<>();

    private Vehiculo vehiculoSeleccionado = null;
    private Transporte transporteSeleccionado = null;
    private PersonalInterno personalSeleccionado = null;
    private Bodega bodegaSeleccionada = null;

    private boolean esEdicion        = false;
    private int     movimientoId     = 0;
    private boolean vehiculosLoaded  = false;
    private boolean transportesLoaded = false;
    private boolean personalLoaded   = false;
    private boolean bodegasLoaded    = false;

    private static final String[] TIPOS_MOVIMIENTO = {"Entrada", "Salida"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento_form);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        movimientoId = getIntent().getIntExtra("movimiento_id", 0);
        esEdicion    = movimientoId > 0;

        formTitle      = findViewById(R.id.form_movimiento_title);
        actvTipo       = findViewById(R.id.actv_tipo_movimiento);
        etFecha        = findViewById(R.id.et_fecha_movimiento);
        actvVehiculo   = findViewById(R.id.actv_vehiculo_movimiento);
        actvTransporte = findViewById(R.id.actv_transporte_movimiento);
        actvPersonal   = findViewById(R.id.actv_personal_movimiento);
        actvBodega     = findViewById(R.id.actv_bodega_movimiento);
        etMotivo       = findViewById(R.id.et_motivo_movimiento);
        btnCancelar    = findViewById(R.id.btn_cancelar_movimiento);
        btnGuardar     = findViewById(R.id.btn_guardar_movimiento);

        movimientoDAO = new GenericDAO<>(this, Movimiento.class, "movimiento");
        vehiculoDAO   = new GenericDAO<>(this, Vehiculo.class, "vehiculo");
        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");
        personalDAO   = new GenericDAO<>(this, PersonalInterno.class, "personal_interno");
        bodegaDAO     = new GenericDAO<>(this, Bodega.class, "bodega");

        formTitle.setText(esEdicion ? getString(R.string.title_editar_movimiento) : getString(R.string.title_nuevo_movimiento));

        configurarSpinnerTipo();
        configurarDatePicker();
        configurarSpinners();

        if (esEdicion) cargarDatos();

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void configurarSpinnerTipo() {
        actvTipo.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, TIPOS_MOVIMIENTO));
        actvTipo.setOnClickListener(v -> actvTipo.showDropDown());
    }

    private void configurarDatePicker() {
        etFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (dp, year, month, day) -> {
                String fecha = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                etFecha.setText(fecha);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void configurarSpinners() {
        Runnable cargarVehiculos = () -> {
            if (!vehiculosLoaded) {
                try { vehiculos = vehiculoDAO.obtenerTodos(); }
                catch (Exception e) { vehiculos = new ArrayList<>(); }
                vehiculosLoaded = true;
                actvVehiculo.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, vehiculos));
                if (vehiculoSeleccionado != null)
                    actvVehiculo.setText(vehiculoSeleccionado.toString(), false);
            }
            actvVehiculo.showDropDown();
        };
        actvVehiculo.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) cargarVehiculos.run(); });
        actvVehiculo.setOnClickListener(v -> cargarVehiculos.run());
        actvVehiculo.setOnItemClickListener((p, v, pos, id) -> vehiculoSeleccionado = vehiculos.get(pos));

        Runnable cargarTransportes = () -> {
            if (!transportesLoaded) {
                try { transportes = transporteDAO.obtenerTodos(); }
                catch (Exception e) { transportes = new ArrayList<>(); }
                transportesLoaded = true;
                actvTransporte.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, transportes));
                if (transporteSeleccionado != null)
                    actvTransporte.setText(transporteSeleccionado.toString(), false);
            }
            actvTransporte.showDropDown();
        };
        actvTransporte.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) cargarTransportes.run(); });
        actvTransporte.setOnClickListener(v -> cargarTransportes.run());
        actvTransporte.setOnItemClickListener((p, v, pos, id) -> transporteSeleccionado = transportes.get(pos));

        Runnable cargarPersonal = () -> {
            if (!personalLoaded) {
                try { personal = personalDAO.obtenerTodos(); }
                catch (Exception e) { personal = new ArrayList<>(); }
                personalLoaded = true;
                actvPersonal.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, personal));
                if (personalSeleccionado != null)
                    actvPersonal.setText(personalSeleccionado.toString(), false);
            }
            actvPersonal.showDropDown();
        };
        actvPersonal.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) cargarPersonal.run(); });
        actvPersonal.setOnClickListener(v -> cargarPersonal.run());
        actvPersonal.setOnItemClickListener((p, v, pos, id) -> personalSeleccionado = personal.get(pos));

        Runnable cargarBodegas = () -> {
            if (!bodegasLoaded) {
                try { bodegas = bodegaDAO.obtenerTodos(); }
                catch (Exception e) { bodegas = new ArrayList<>(); }
                bodegasLoaded = true;
                actvBodega.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, bodegas));
                if (bodegaSeleccionada != null)
                    actvBodega.setText(bodegaSeleccionada.toString(), false);
            }
            actvBodega.showDropDown();
        };
        actvBodega.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) cargarBodegas.run(); });
        actvBodega.setOnClickListener(v -> cargarBodegas.run());
        actvBodega.setOnItemClickListener((p, v, pos, id) -> bodegaSeleccionada = bodegas.get(pos));
    }

    private void cargarDatos() {
        try {
            Movimiento m = movimientoDAO.obtenerPorId(movimientoId);
            if (m == null) return;

            actvTipo.setText(m.getTipoMovimiento(), false);
            etFecha.setText(m.getFechaMovimiento());
            etMotivo.setText(m.getMotivo());

            vehiculoSeleccionado   = vehiculoDAO.obtenerPorId(m.getIdVehiculo());
            transporteSeleccionado = transporteDAO.obtenerPorId(m.getIdTransporte());
            personalSeleccionado   = personalDAO.obtenerPorId(m.getIdPersonal());
            bodegaSeleccionada     = bodegaDAO.obtenerPorId(m.getIdBodega());

            if (vehiculoSeleccionado   != null) actvVehiculo.setText(vehiculoSeleccionado.toString(), false);
            if (transporteSeleccionado != null) actvTransporte.setText(transporteSeleccionado.toString(), false);
            if (personalSeleccionado   != null) actvPersonal.setText(personalSeleccionado.toString(), false);
            if (bodegaSeleccionada     != null) actvBodega.setText(bodegaSeleccionada.toString(), false);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
        }
    }

    private void guardar() {
        String tipo   = actvTipo.getText().toString().trim();
        String fecha  = etFecha.getText().toString().trim();
        String motivo = etMotivo.getText().toString().trim();

        if (tipo.isEmpty() || fecha.isEmpty() || motivo.isEmpty()
                || vehiculoSeleccionado == null || transporteSeleccionado == null
                || personalSeleccionado == null || bodegaSeleccionada == null) {
            Toast.makeText(this, Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        Movimiento m = new Movimiento(
            movimientoId,
            transporteSeleccionado.getId(),
            personalSeleccionado.getId(),
            vehiculoSeleccionado.getId(),
            bodegaSeleccionada.getId(),
            tipo, fecha, motivo
        );

        try {
            if (esEdicion) {
                movimientoDAO.actualizar(m);
            } else {
                movimientoDAO.insertar(m);
            }
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("capacidad")) {
                Toast.makeText(this,
                    getString(R.string.msg_transporte_capacidad),
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
