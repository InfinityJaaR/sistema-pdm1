package com.ues.sistema_pdm1.activities.importador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.TelefonoImportador;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.List;

public class ImportadorViewActivity extends AppCompatActivity {

    private int importadorId;
    private Importador importador;

    private GenericDAO<Importador> importadorDAO;
    private GenericDAO<TelefonoImportador> telefonoDAO;
    private GenericDAO<Importacion> importacionDAO;

    private final ActivityResultLauncher<Intent> launcherEditar =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) recargar(); });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer_view);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importadorId = getIntent().getIntExtra("importador_id", 0);

        importadorDAO   = new GenericDAO<>(this, Importador.class, "importador");
        telefonoDAO     = new GenericDAO<>(this, TelefonoImportador.class, "telefono_importador");
        importacionDAO  = new GenericDAO<>(this, Importacion.class, "importacion");

        recargar();

        findViewById(R.id.btn_edit).setOnClickListener(v -> abrirEditar());
        findViewById(R.id.btn_gestionar_telefonos).setOnClickListener(v -> mostrarDialogoTelefonos());
    }

    private void recargar() {
        try {
            importador = importadorDAO.obtenerPorId(importadorId);
            if (importador == null) {
                finish();
                return;
            }
            poblarVista();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void poblarVista() {
        // Header
        String nombreCompleto = importador.getNombreImportador() + " " + importador.getApellidoImportador();
        ((TextView) findViewById(R.id.detail_name)).setText(nombreCompleto);
        ((TextView) findViewById(R.id.detail_nui)).setText("NUI: " + importador.getNui());

        // Datos Personales
        ((TextView) findViewById(R.id.detail_first_name)).setText(importador.getNombreImportador());
        ((TextView) findViewById(R.id.detail_last_name)).setText(importador.getApellidoImportador());
        ((TextView) findViewById(R.id.detail_gender)).setText("M".equals(importador.getGenero()) ? "Masculino" : "Femenino");
        ((TextView) findViewById(R.id.detail_birthdate)).setText(importador.getFechaNacimiento());

        // Apellido de casada (condicional)
        String apCasada = importador.getApellidoCasada();
        LinearLayout rowCasada = findViewById(R.id.married_lastname_row);
        if (apCasada != null && !apCasada.isEmpty()) {
            rowCasada.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detail_married_lastname)).setText(apCasada);
        } else {
            rowCasada.setVisibility(View.GONE);
        }

        // Contacto - email y dirección
        String email = importador.getCorreoElectronico();
        ((TextView) findViewById(R.id.detail_email)).setText((email != null && !email.isEmpty()) ? email : "—");
        ((TextView) findViewById(R.id.detail_address)).setText(importador.getDireccionImportador());

        // Teléfonos
        List<TelefonoImportador> tels = telefonoDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(importadorId));
        if (!tels.isEmpty()) {
            ((TextView) findViewById(R.id.detail_phone_primary)).setText(tels.get(0).getNumero());
        } else {
            ((TextView) findViewById(R.id.detail_phone_primary)).setText("—");
        }

        LinearLayout rowTel2 = findViewById(R.id.phone_secondary_row);
        if (tels.size() >= 2) {
            rowTel2.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detail_phone_secondary)).setText(tels.get(1).getNumero());
        } else {
            rowTel2.setVisibility(View.GONE);
        }

        // Info Profesional
        ((TextView) findViewById(R.id.detail_nui_value)).setText(importador.getNui());
        String resp = importador.getNombreResponsable();
        ((TextView) findViewById(R.id.detail_responsible)).setText((resp != null && !resp.isEmpty()) ? resp : "—");

        // Importaciones
        List<Importacion> importaciones = importacionDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(importadorId));
        ((TextView) findViewById(R.id.detail_imports_count)).setText(importaciones.size() + " importación(es)");

        if (importaciones.isEmpty()) {
            ((TextView) findViewById(R.id.detail_imports_list)).setText("Sin importaciones registradas");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Importacion imp : importaciones) {
                sb.append("• ").append(imp.getFechaImportacion()).append("\n");
            }
            ((TextView) findViewById(R.id.detail_imports_list)).setText(sb.toString().trim());
        }
    }

    private void abrirEditar() {
        Intent intent = new Intent(this, ImportadorFormActivity.class);
        intent.putExtra("importador_id", importadorId);
        launcherEditar.launch(intent);
    }

    private void mostrarDialogoTelefonos() {
        List<TelefonoImportador> tels = telefonoDAO.obtenerPor("ID_IMPORTADOR", String.valueOf(importadorId));

        String[] items = new String[tels.size() + 1];
        for (int i = 0; i < tels.size(); i++) {
            items[i] = tels.get(i).getNumero() + " (" + tels.get(i).getTipo() + ")";
        }
        items[tels.size()] = "+ Agregar nuevo teléfono";

        final List<TelefonoImportador> listaTels = tels;

        new AlertDialog.Builder(this)
            .setTitle("Teléfonos del importador")
            .setItems(items, (dialog, which) -> {
                if (which < listaTels.size()) {
                    confirmarEliminarTelefono(listaTels.get(which));
                } else {
                    mostrarDialogoAgregarTelefono();
                }
            })
            .setNegativeButton("Cerrar", null)
            .show();
    }

    private void confirmarEliminarTelefono(TelefonoImportador tel) {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar teléfono")
            .setMessage("¿Eliminar el número " + tel.getNumero() + "?")
            .setPositiveButton("Eliminar", (d, w) -> {
                try {
                    telefonoDAO.eliminar(tel.getId());
                    poblarVista();
                    Toast.makeText(this, "Teléfono eliminado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void mostrarDialogoAgregarTelefono() {
        EditText inputNumero = new EditText(this);
        inputNumero.setHint("Número de teléfono");
        inputNumero.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        inputNumero.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
            .setTitle("Agregar teléfono")
            .setView(inputNumero)
            .setPositiveButton("Agregar", (d, w) -> {
                String num = inputNumero.getText().toString().trim();
                if (num.isEmpty()) {
                    Toast.makeText(this, Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    telefonoDAO.insertar(new TelefonoImportador(0, importadorId, num, Constants.TIPO_TELEFONO_CELULAR));
                    poblarVista();
                    Toast.makeText(this, "Teléfono agregado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, Constants.MSG_OPERACION_FALLIDA, Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
}
