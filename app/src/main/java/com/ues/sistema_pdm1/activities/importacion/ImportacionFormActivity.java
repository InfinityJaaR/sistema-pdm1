package com.ues.sistema_pdm1.activities.importacion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ImportacionFormActivity extends AppCompatActivity {

    private TextView formTitle;
    private AutoCompleteTextView spinnerImportador;
    private TextInputEditText inputFecha;
    private ImageView btnDatePicker;
    private Button btnCancelar, btnGuardar;

    private GenericDAO<Importacion> importacionDAO;
    private GenericDAO<Importador> importadorDAO;

    private boolean esEdicion = false;
    private int importacionId = 0;
    private List<Importador> importadores = new ArrayList<>();
    private Importador importadorSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacion_form);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importacionId = getIntent().getIntExtra("importacion_id", 0);
        esEdicion     = importacionId > 0;

        formTitle        = findViewById(R.id.form_importacion_title);
        spinnerImportador = findViewById(R.id.spinner_importador);
        inputFecha       = findViewById(R.id.input_fecha_importacion);
        btnDatePicker    = findViewById(R.id.btn_date_importacion);
        btnCancelar      = findViewById(R.id.btn_cancelar_importacion);
        btnGuardar       = findViewById(R.id.btn_guardar_importacion);

        importacionDAO = new GenericDAO<>(this, Importacion.class, "importacion");
        importadorDAO  = new GenericDAO<>(this, Importador.class, "importador");

        formTitle.setText(esEdicion ? "Editar Importación" : "Agregar Importación");

        configurarSpinnerImportador();
        configurarDatePicker();

        if (esEdicion) cargarDatos();

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void configurarSpinnerImportador() {
        try {
            importadores = importadorDAO.obtenerTodos();
        } catch (Exception e) {
            importadores = new ArrayList<>();
        }
        spinnerImportador.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, importadores));
        spinnerImportador.setOnItemClickListener((parent, view, position, id) ->
            importadorSeleccionado = importadores.get(position));
    }

    private void configurarDatePicker() {
        android.view.View.OnClickListener abrirCalendario = v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (dp, year, month, day) -> {
                String fecha = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                inputFecha.setText(fecha);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        };
        btnDatePicker.setOnClickListener(abrirCalendario);
        inputFecha.setOnClickListener(abrirCalendario);
    }

    private void cargarDatos() {
        try {
            Importacion imp = importacionDAO.obtenerPorId(importacionId);
            if (imp == null) return;

            inputFecha.setText(imp.getFechaImportacion());

            for (Importador importador : importadores) {
                if (importador.getId() == imp.getIdImportador()) {
                    importadorSeleccionado = importador;
                    spinnerImportador.setText(importador.toString(), false);
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardar() {
        String fecha = inputFecha.getText() != null ? inputFecha.getText().toString().trim() : "";

        if (fecha.isEmpty()) {
            Toast.makeText(this, Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
            return;
        }
        if (importadorSeleccionado == null) {
            Toast.makeText(this, "Seleccione un importador", Toast.LENGTH_SHORT).show();
            return;
        }

        Importacion imp = new Importacion(importacionId, importadorSeleccionado.getId(), fecha);

        try {
            if (esEdicion) {
                importacionDAO.actualizar(imp);
            } else {
                importacionDAO.insertar(imp);
            }
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
        }
    }
}
