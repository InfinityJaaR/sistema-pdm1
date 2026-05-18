package com.ues.sistema_pdm1.activities.transporte;

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
import com.ues.sistema_pdm1.models.TipoTransporte;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TransporteFormActivity extends AppCompatActivity {

    private TextView formTitle;
    private AutoCompleteTextView actvTipo;
    private EditText etPlaca, etDescripcion;
    private Button btnCancelar, btnGuardar;

    private GenericDAO<Transporte> transporteDAO;
    private GenericDAO<TipoTransporte> tipoDAO;

    private List<TipoTransporte> tipos = new ArrayList<>();
    private TipoTransporte tipoSeleccionado = null;

    private boolean esEdicion   = false;
    private int     transporteId = 0;
    private boolean tiposLoaded  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_form);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        transporteId = getIntent().getIntExtra("transporte_id", 0);
        esEdicion    = transporteId > 0;

        formTitle    = findViewById(R.id.form_transporte_title);
        actvTipo     = findViewById(R.id.actv_tipo_transporte);
        etPlaca      = findViewById(R.id.et_placa_transporte);
        etDescripcion = findViewById(R.id.et_descripcion_transporte);
        btnCancelar  = findViewById(R.id.btn_cancelar_transporte);
        btnGuardar   = findViewById(R.id.btn_guardar_transporte);

        transporteDAO = new GenericDAO<>(this, Transporte.class, "transporte");
        tipoDAO       = new GenericDAO<>(this, TipoTransporte.class, "tipo_transporte");

        formTitle.setText(esEdicion ? getString(R.string.title_editar_transporte) : getString(R.string.title_nuevo_transporte));

        configurarSpinnerTipo();

        if (esEdicion) cargarDatos();

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void configurarSpinnerTipo() {
        Runnable cargar = () -> {
            if (!tiposLoaded) {
                try { tipos = tipoDAO.obtenerTodos(); }
                catch (Exception e) { tipos = new ArrayList<>(); }
                tiposLoaded = true;
                actvTipo.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, tipos));
                if (tipoSeleccionado != null)
                    actvTipo.setText(tipoSeleccionado.toString(), false);
            }
            actvTipo.showDropDown();
        };
        actvTipo.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) cargar.run(); });
        actvTipo.setOnClickListener(v -> cargar.run());
        actvTipo.setOnItemClickListener((parent, view, position, id) ->
            tipoSeleccionado = tipos.get(position));
    }

    private void cargarDatos() {
        try {
            Transporte t = transporteDAO.obtenerPorId(transporteId);
            if (t == null) return;
            etPlaca.setText(t.getPlaca());
            etDescripcion.setText(t.getDescripcionTransporte());
            tipoSeleccionado = tipoDAO.obtenerPorId(t.getIdTipoTransporte());
            if (tipoSeleccionado != null)
                actvTipo.setText(tipoSeleccionado.toString(), false);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
        }
    }

    private void guardar() {
        String placa = etPlaca.getText().toString().trim().toUpperCase();

        if (tipoSeleccionado == null) {
            Toast.makeText(this, getString(R.string.error_seleccione_tipo_transporte), Toast.LENGTH_SHORT).show();
            return;
        }
        if (placa.isEmpty()) {
            Toast.makeText(this, Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = etDescripcion.getText().toString().trim();

        Transporte t = new Transporte(transporteId, tipoSeleccionado.getId(), placa, descripcion);

        try {
            if (esEdicion) {
                transporteDAO.actualizar(t);
            } else {
                transporteDAO.insertar(t);
            }
            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
        }
    }
}
