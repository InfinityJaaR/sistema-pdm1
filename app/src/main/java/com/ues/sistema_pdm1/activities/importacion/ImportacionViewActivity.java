package com.ues.sistema_pdm1.activities.importacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.LoginActivity;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.DetalleDesperfecto;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.Marca;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.TipoDesperfecto;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.List;

public class ImportacionViewActivity extends AppCompatActivity {

    private int importacionId;

    private GenericDAO<Importacion>       importacionDAO;
    private GenericDAO<Importador>        importadorDAO;
    private GenericDAO<Vehiculo>          vehiculoDAO;
    private GenericDAO<Modelo>            modeloDAO;
    private GenericDAO<Marca>             marcaDAO;
    private GenericDAO<DetalleDesperfecto> detalleDAO;
    private GenericDAO<TipoDesperfecto>   tipoDesperfectoDAO;

    private final ActivityResultLauncher<Intent> launcherEditar =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) recargar(); });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importacion_view);

        if (!SessionManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        importacionId      = getIntent().getIntExtra("importacion_id", 0);
        importacionDAO     = new GenericDAO<>(this, Importacion.class,        "importacion");
        importadorDAO      = new GenericDAO<>(this, Importador.class,         "importador");
        vehiculoDAO        = new GenericDAO<>(this, Vehiculo.class,           "vehiculo");
        modeloDAO          = new GenericDAO<>(this, Modelo.class,             "modelo");
        marcaDAO           = new GenericDAO<>(this, Marca.class,              "marca");
        detalleDAO         = new GenericDAO<>(this, DetalleDesperfecto.class, "detalle_desperfecto");
        tipoDesperfectoDAO = new GenericDAO<>(this, TipoDesperfecto.class,    "tipo_desperfecto");

        recargar();

        findViewById(R.id.btn_edit_importacion).setOnClickListener(v -> {
            Intent intent = new Intent(this, ImportacionFormActivity.class);
            intent.putExtra("importacion_id", importacionId);
            launcherEditar.launch(intent);
        });
    }

    private void recargar() {
        try {
            Importacion imp = importacionDAO.obtenerPorId(importacionId);
            if (imp == null) { finish(); return; }

            ((TextView) findViewById(R.id.view_importacion_titulo)).setText(getString(R.string.label_importacion_prefijo) + imp.getId());
            ((TextView) findViewById(R.id.view_importacion_fecha)).setText(imp.getFechaImportacion());
            ((TextView) findViewById(R.id.view_importacion_fecha_valor)).setText(imp.getFechaImportacion());

            // Buscar nombre del importador
            String nombreImp;
            try {
                Importador importador = importadorDAO.obtenerPorId(imp.getIdImportador());
                nombreImp = importador != null
                    ? importador.getNombreImportador() + " " + importador.getApellidoImportador()
                    : getString(R.string.label_importador_prefijo) + imp.getIdImportador();
            } catch (Exception e) {
                nombreImp = getString(R.string.label_importador_prefijo) + imp.getIdImportador();
            }
            ((TextView) findViewById(R.id.view_importacion_importador)).setText(nombreImp);

            cargarVehiculos(imp.getId());

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarVehiculos(int idImportacion) {
        LinearLayout container = findViewById(R.id.container_vehiculos);
        container.removeAllViews();

        try {
            List<Vehiculo> vehiculos = vehiculoDAO.obtenerPor("ID_IMPORTACION", String.valueOf(idImportacion));

            if (vehiculos.isEmpty()) {
                TextView tvVacio = new TextView(this);
                tvVacio.setText(getString(R.string.msg_sin_vehiculos_importacion));
                tvVacio.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                tvVacio.setTextSize(13);
                container.addView(tvVacio);
                return;
            }

            int dp16 = (int) (16 * getResources().getDisplayMetrics().density);
            int dp8  = (int) (8  * getResources().getDisplayMetrics().density);
            int dp4  = (int) (4  * getResources().getDisplayMetrics().density);

            for (Vehiculo v : vehiculos) {
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_card_white));
                card.setPadding(dp16, dp16, dp16, dp16);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, dp8);
                card.setLayoutParams(cardParams);

                String marcaNombre  = "";
                String modeloNombre = "";
                try {
                    Modelo modelo = modeloDAO.obtenerPorId(v.getIdModelo());
                    if (modelo != null) {
                        modeloNombre = modelo.getNombreModelo();
                        Marca marca = marcaDAO.obtenerPorId(modelo.getIdMarca());
                        if (marca != null) marcaNombre = marca.getNombreMarca();
                    }
                } catch (Exception ignored) {}

                card.addView(fila(getString(R.string.label_marca),  marcaNombre,  dp4));
                card.addView(fila(getString(R.string.label_modelo), modeloNombre, dp4));
                card.addView(fila(getString(R.string.label_anio),   String.valueOf(v.getAnio()), dp4));
                card.addView(fila(getString(R.string.label_color),  v.getColorVehiculo(), dp4));
                card.addView(fila(getString(R.string.label_vin),    v.getVin(), dp4));

                TextView tvDespTitle = new TextView(this);
                tvDespTitle.setText(getString(R.string.label_desperfectos));
                tvDespTitle.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                tvDespTitle.setTextSize(13);
                tvDespTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                LinearLayout.LayoutParams dpTitleParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                dpTitleParams.setMargins(0, dp8, 0, dp4);
                tvDespTitle.setLayoutParams(dpTitleParams);
                card.addView(tvDespTitle);

                try {
                    List<DetalleDesperfecto> detalles = detalleDAO.obtenerPor("ID_VEHICULO", String.valueOf(v.getId()));
                    if (detalles.isEmpty()) {
                        TextView tvSinDesp = new TextView(this);
                        tvSinDesp.setText(getString(R.string.msg_sin_desperfectos));
                        tvSinDesp.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                        tvSinDesp.setTextSize(12);
                        card.addView(tvSinDesp);
                    } else {
                        for (DetalleDesperfecto d : detalles) {
                            String tipoNombre = "";
                            try {
                                TipoDesperfecto tipo = tipoDesperfectoDAO.obtenerPorId(d.getIdTipoDesperfecto());
                                if (tipo != null) tipoNombre = tipo.getNombreTipoDesperfecto();
                            } catch (Exception ignored) {}

                            card.addView(fila(getString(R.string.label_tipo_desperfecto), tipoNombre, dp4));
                            card.addView(fila(getString(R.string.label_descripcion_desperfecto), d.getDescripcionDetalle(), dp4));
                            card.addView(fila(getString(R.string.label_fecha_registro), d.getFechaRegistro(), dp4));
                        }
                    }
                } catch (Exception ignored) {}

                container.addView(card);
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_cargar_datos), Toast.LENGTH_SHORT).show();
        }
    }

    private LinearLayout fila(String etiqueta, String valor, int marginBottom) {
        int dp12 = (int) (12 * getResources().getDisplayMetrics().density);

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        filaParams.setMargins(0, 0, 0, marginBottom);
        fila.setLayoutParams(filaParams);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(etiqueta);
        tvLabel.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvLabel.setTextSize(13);
        tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.setMargins(0, 0, dp12, 0);
        tvLabel.setMinWidth((int) (90 * getResources().getDisplayMetrics().density));
        tvLabel.setLayoutParams(labelParams);

        TextView tvValor = new TextView(this);
        tvValor.setText(valor != null ? valor : "—");
        tvValor.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tvValor.setTextSize(13);
        tvValor.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        fila.addView(tvLabel);
        fila.addView(tvValor);
        return fila;
    }
}
