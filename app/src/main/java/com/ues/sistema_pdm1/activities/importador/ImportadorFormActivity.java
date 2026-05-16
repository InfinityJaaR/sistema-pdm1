package com.ues.sistema_pdm1.activities.importador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Distrito;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.TelefonoImportador;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ImportadorFormActivity extends AppCompatActivity {

    private TextView formTitle;
    private AutoCompleteTextView spinnerGender, spinnerDistrict;
    private TextInputEditText inputName, inputLastname, inputMarriedLastname;
    private SwitchCompat switchMarried;
    private TextInputLayout layoutMarriedLastname;
    private TextInputEditText inputBirthdate, inputAddress, inputEmail;
    private TextInputEditText inputPhonePrimary, inputNui, inputResponsible;
    private ImageView btnDatePicker;
    private LinearLayout containerPhones;
    private TextView btnAddPhone;
    private Button btnCancelar, btnGuardar;

    private GenericDAO<Importador> importadorDAO;
    private GenericDAO<TelefonoImportador> telefonoDAO;
    private GenericDAO<Distrito> distritoDAO;

    private boolean esEdicion = false;
    private int importadorId = 0;
    private List<Distrito> distritos = new ArrayList<>();
    private Distrito distritoSeleccionado = null;
    private final List<TextInputEditText> phoneExtraFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer_form);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importadorId = getIntent().getIntExtra("importador_id", 0);
        esEdicion    = importadorId > 0;

        vincularVistas();

        importadorDAO = new GenericDAO<>(this, Importador.class, "importador");
        telefonoDAO   = new GenericDAO<>(this, TelefonoImportador.class, "telefono_importador");
        distritoDAO   = new GenericDAO<>(this, Distrito.class, "distrito");

        configurarSpinnerGenero();
        configurarSpinnerDistrito();
        configurarDatePicker();
        configurarSwitchCasada();

        formTitle.setText(esEdicion ? R.string.form_title_edit : R.string.form_title_add);

        if (esEdicion) cargarDatos();

        aplicarFormatoTelefono(inputPhonePrimary);

        btnAddPhone.setOnClickListener(v -> agregarFilaTelefono(""));
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void vincularVistas() {
        formTitle             = findViewById(R.id.form_title);
        spinnerGender         = findViewById(R.id.spinner_gender);
        inputName             = findViewById(R.id.input_name);
        inputLastname         = findViewById(R.id.input_lastname);
        switchMarried         = findViewById(R.id.switch_married);
        layoutMarriedLastname = findViewById(R.id.layout_married_lastname);
        inputMarriedLastname  = findViewById(R.id.input_married_lastname);
        inputBirthdate        = findViewById(R.id.input_birthdate);
        btnDatePicker         = findViewById(R.id.btn_date_picker);
        spinnerDistrict       = findViewById(R.id.spinner_district);
        inputAddress          = findViewById(R.id.input_address);
        inputEmail            = findViewById(R.id.input_email);
        inputPhonePrimary     = findViewById(R.id.input_phone_primary);
        containerPhones       = findViewById(R.id.container_phones);
        btnAddPhone           = findViewById(R.id.btn_add_phone);
        inputNui              = findViewById(R.id.input_nui);
        inputResponsible      = findViewById(R.id.input_responsible);
        btnCancelar           = findViewById(R.id.btn_cancelar);
        btnGuardar            = findViewById(R.id.btn_guardar);
    }

    private void configurarSpinnerGenero() {
        String[] opciones = {
            getString(R.string.field_gender_male),
            getString(R.string.field_gender_female)
        };
        spinnerGender.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, opciones));

        switchMarried.setEnabled(false);
        switchMarried.setChecked(false);
        layoutMarriedLastname.setVisibility(View.GONE);

        spinnerGender.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean esFemenino = getString(R.string.field_gender_female).equals(s.toString());
                switchMarried.setEnabled(esFemenino);
                if (!esFemenino) {
                    switchMarried.setChecked(false);
                    layoutMarriedLastname.setVisibility(View.GONE);
                }
            }
        });
    }

    private void configurarSpinnerDistrito() {
        try {
            distritos = distritoDAO.obtenerTodos();
        } catch (Exception e) {
            distritos = new ArrayList<>();
        }
        spinnerDistrict.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, distritos));
        spinnerDistrict.setOnItemClickListener((parent, view, position, id) ->
            distritoSeleccionado = distritos.get(position));
    }

    private void configurarDatePicker() {
        View.OnClickListener abrirCalendario = v -> {
            Calendar maxFecha = Calendar.getInstance();
            maxFecha.add(Calendar.YEAR, -18);

            DatePickerDialog dialog = new DatePickerDialog(this, (dp, year, month, day) -> {
                String fecha = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                inputBirthdate.setText(fecha);
            }, maxFecha.get(Calendar.YEAR), maxFecha.get(Calendar.MONTH), maxFecha.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(maxFecha.getTimeInMillis());
            dialog.show();
        };
        btnDatePicker.setOnClickListener(abrirCalendario);
        inputBirthdate.setOnClickListener(abrirCalendario);
    }

    private void configurarSwitchCasada() {
        switchMarried.setOnCheckedChangeListener((btn, checked) ->
            layoutMarriedLastname.setVisibility(checked ? View.VISIBLE : View.GONE));
    }

    private void cargarDatos() {
        try {
            Importador imp = importadorDAO.obtenerPorId(importadorId);
            if (imp == null) return;

            inputName.setText(imp.getNombreImportador());
            inputLastname.setText(imp.getApellidoImportador());
            inputBirthdate.setText(imp.getFechaNacimiento());
            inputAddress.setText(imp.getDireccionImportador());
            inputEmail.setText(imp.getCorreoElectronico());
            inputNui.setText(imp.getNui());
            inputResponsible.setText(imp.getNombreResponsable());

            String genero = imp.getGenero();
            if ("M".equals(genero)) {
                spinnerGender.setText(getString(R.string.field_gender_male), false);
                switchMarried.setEnabled(false);
                switchMarried.setChecked(false);
                layoutMarriedLastname.setVisibility(View.GONE);
            } else if ("F".equals(genero)) {
                spinnerGender.setText(getString(R.string.field_gender_female), false);
                switchMarried.setEnabled(true);
            }

            String apCasada = imp.getApellidoCasada();
            if (apCasada != null && !apCasada.isEmpty()) {
                switchMarried.setChecked(true);
                layoutMarriedLastname.setVisibility(View.VISIBLE);
                inputMarriedLastname.setText(apCasada);
            }

            for (Distrito d : distritos) {
                if (d.getId() == imp.getIdDistrito()) {
                    distritoSeleccionado = d;
                    spinnerDistrict.setText(d.getNombreDistrito(), false);
                    break;
                }
            }

            List<TelefonoImportador> tels = telefonoDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(importadorId));
            if (!tels.isEmpty()) {
                inputPhonePrimary.setText(tels.get(0).getNumero());
                for (int i = 1; i < tels.size(); i++) {
                    agregarFilaTelefono(tels.get(i).getNumero());
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarFilaTelefono(String numero) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_phone_form_row, containerPhones, false);
        TextInputEditText input = row.findViewById(R.id.input_phone_extra);
        aplicarFormatoTelefono(input);
        if (!numero.isEmpty()) input.setText(numero);
        phoneExtraFields.add(input);

        row.findViewById(R.id.btn_remove_phone).setOnClickListener(v -> {
            containerPhones.removeView(row);
            phoneExtraFields.remove(input);
        });

        containerPhones.addView(row);
    }

    private void aplicarFormatoTelefono(TextInputEditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            private boolean editando = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editando) return;
                editando = true;

                String soloDigitos = s.toString().replaceAll("[^0-9]", "");
                if (soloDigitos.length() > 8) soloDigitos = soloDigitos.substring(0, 8);

                String formateado;
                if (soloDigitos.length() > 4) {
                    formateado = soloDigitos.substring(0, 4) + "-" + soloDigitos.substring(4);
                } else {
                    formateado = soloDigitos;
                }

                campo.setText(formateado);
                campo.setSelection(formateado.length());
                editando = false;
            }
        });
    }

    private void guardar() {
        String nombre    = texto(inputName);
        String apellido  = texto(inputLastname);
        String nui       = texto(inputNui);
        String direccion = texto(inputAddress);
        String fecha     = texto(inputBirthdate);
        String generoTxt = texto(spinnerGender);

        if (nombre.isEmpty() || apellido.isEmpty() || nui.isEmpty()
                || direccion.isEmpty() || fecha.isEmpty() || generoTxt.isEmpty()) {
            Toast.makeText(this, Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        if (distritoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un distrito", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nui.length() != Constants.LONGITUD_NUI) {
            Toast.makeText(this, "El NUI debe tener exactamente " + Constants.LONGITUD_NUI + " caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String[] partes = fecha.split("-");
            int anio = Integer.parseInt(partes[0]);
            int mes  = Integer.parseInt(partes[1]) - 1;
            int dia  = Integer.parseInt(partes[2]);
            Calendar nacimiento = Calendar.getInstance();
            nacimiento.set(anio, mes, dia);
            Calendar limite = Calendar.getInstance();
            limite.add(Calendar.YEAR, -18);
            if (nacimiento.after(limite)) {
                Toast.makeText(this, "El importador debe ser mayor de 18 años", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Fecha de nacimiento no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = texto(inputEmail);
        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String genero      = generoTxt.equals(getString(R.string.field_gender_male)) ? "M" : "F";
        String apCasada    = switchMarried.isChecked() ? texto(inputMarriedLastname) : "";
        String responsable = texto(inputResponsible);

        Importador imp = new Importador(
            importadorId, distritoSeleccionado.getId(),
            nombre, apellido, apCasada, genero,
            direccion, fecha, email, nui, responsable
        );

        try {
            int idParaTelefonos;
            if (esEdicion) {
                importadorDAO.actualizar(imp);
                idParaTelefonos = importadorId;
                List<TelefonoImportador> viejos = telefonoDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(idParaTelefonos));
                for (TelefonoImportador t : viejos) telefonoDAO.eliminar(t.getId());
            } else {
                long newId = importadorDAO.insertar(imp);
                idParaTelefonos = (int) newId;
            }

            String tel1 = texto(inputPhonePrimary);
            if (!tel1.isEmpty()) {
                telefonoDAO.insertar(new TelefonoImportador(0, idParaTelefonos, tel1, Constants.TIPO_TELEFONO_CELULAR));
            }
            for (TextInputEditText campo : phoneExtraFields) {
                String num = campo.getText() != null ? campo.getText().toString().trim() : "";
                if (!num.isEmpty()) {
                    telefonoDAO.insertar(new TelefonoImportador(0, idParaTelefonos, num, Constants.TIPO_TELEFONO_CELULAR));
                }
            }

            Toast.makeText(this, Constants.MSG_OPERACION_EXITOSA, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
        }
    }

    private String texto(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private String texto(AutoCompleteTextView tv) {
        return tv.getText() != null ? tv.getText().toString().trim() : "";
    }
}
