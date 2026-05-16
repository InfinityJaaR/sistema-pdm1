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

    private boolean esEdicion = false;
    private int movimientoId = 0;

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

        formTitle.setText(esEdicion ? "Editar Movimiento" : "Nuevo Movimiento");

        configurarSpinnerTipo();
        configurarDatePicker();
        cargarSpinners();

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

    private void cargarSpinners() {
        try { vehiculos   = vehiculoDAO.obtenerTodos();   } catch (Exception e) { vehiculos   = new ArrayList<>(); }
        try { transportes = transporteDAO.obtenerTodos(); } catch (Exception e) { transportes = new ArrayList<>(); }
        try { personal    = personalDAO.obtenerTodos();   } catch (Exception e) { personal    = new ArrayList<>(); }
        try { bodegas     = bodegaDAO.obtenerTodos();     } catch (Exception e) { bodegas     = new ArrayList<>(); }

        actvVehiculo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehiculos));
        actvVehiculo.setOnClickListener(v -> actvVehiculo.showDropDown());
        actvVehiculo.setOnItemClickListener((p, v, pos, id) -> vehiculoSeleccionado = vehiculos.get(pos));

        actvTransporte.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, transportes));
        actvTransporte.setOnClickListener(v -> actvTransporte.showDropDown());
        actvTransporte.setOnItemClickListener((p, v, pos, id) -> transporteSeleccionado = transportes.get(pos));

        actvPersonal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, personal));
        actvPersonal.setOnClickListener(v -> actvPersonal.showDropDown());
        actvPersonal.setOnItemClickListener((p, v, pos, id) -> personalSeleccionado = personal.get(pos));

        actvBodega.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bodegas));
        actvBodega.setOnClickListener(v -> actvBodega.showDropDown());
        actvBodega.setOnItemClickListener((p, v, pos, id) -> bodegaSeleccionada = bodegas.get(pos));
    }

    private void cargarDatos() {
        try {
            Movimiento m = movimientoDAO.obtenerPorId(movimientoId);
            if (m == null) return;

            actvTipo.setText(m.getTipoMovimiento(), false);
            etFecha.setText(m.getFechaMovimiento());
            etMotivo.setText(m.getMotivo());

            for (Vehiculo v : vehiculos) {
                if (v.getId() == m.getIdVehiculo()) {
                    vehiculoSeleccionado = v;
                    actvVehiculo.setText(v.toString(), false);
                    break;
                }
            }
            for (Transporte t : transportes) {
                if (t.getId() == m.getIdTransporte()) {
                    transporteSeleccionado = t;
                    actvTransporte.setText(t.toString(), false);
                    break;
                }
            }
            for (PersonalInterno p : personal) {
                if (p.getId() == m.getIdPersonal()) {
                    personalSeleccionado = p;
                    actvPersonal.setText(p.toString(), false);
                    break;
                }
            }
            for (Bodega b : bodegas) {
                if (b.getId() == m.getIdBodega()) {
                    bodegaSeleccionada = b;
                    actvBodega.setText(b.toString(), false);
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
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
                    "El transporte ha alcanzado su capacidad máxima para este día",
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
