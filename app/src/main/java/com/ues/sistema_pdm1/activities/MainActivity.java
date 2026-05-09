package com.ues.sistema_pdm1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.catalogos.CatalogosActivity;
import com.ues.sistema_pdm1.activities.importador.ImportadorActivity;
import com.ues.sistema_pdm1.activities.movimiento.MovimientoActivity;
import com.ues.sistema_pdm1.activities.reparacion.ReparacionActivity;
import com.ues.sistema_pdm1.activities.venta.VentaActivity;
import com.ues.sistema_pdm1.activities.vehiculo.VehiculoActivity;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.LlenarBDGpo02;
import com.ues.sistema_pdm1.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnLogout;
    private Button btnImportador;
    private Button btnVehiculo;
    private Button btnMovimiento;
    private Button btnReparacion;
    private Button btnVenta;
    private Button btnCatalogos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SessionManager.getInstance().isLoggedIn()) {
            irALogin();
            return;
        }

        SessionManager.getInstance().inicializar(getApplicationContext());
        LlenarBDGpo02.llenarDatosIniciales(getApplicationContext());

        tvBienvenida  = findViewById(R.id.tv_bienvenida);
        btnLogout     = findViewById(R.id.btn_logout);
        btnImportador = findViewById(R.id.btn_importador);
        btnVehiculo   = findViewById(R.id.btn_vehiculo);
        btnMovimiento = findViewById(R.id.btn_movimiento);
        btnReparacion = findViewById(R.id.btn_reparacion);
        btnVenta      = findViewById(R.id.btn_venta);
        btnCatalogos  = findViewById(R.id.btn_catalogos);

        tvBienvenida.setText("Bienvenido: " + SessionManager.getInstance().getNombreUsuario());

        configurarMenu();

        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance().logout();
            irALogin();
        });
    }

    private void configurarMenu() {
        SessionManager session = SessionManager.getInstance();

        if (session.tieneAcceso(Constants.OPCION_MENU_IMPORTADOR)) {
            btnImportador.setVisibility(View.VISIBLE);
            btnImportador.setOnClickListener(v ->
                startActivity(new Intent(this, ImportadorActivity.class)));
        } else {
            btnImportador.setVisibility(View.GONE);
        }

        if (session.tieneAcceso(Constants.OPCION_MENU_VEHICULO)) {
            btnVehiculo.setVisibility(View.VISIBLE);
            btnVehiculo.setOnClickListener(v ->
                startActivity(new Intent(this, VehiculoActivity.class)));
        } else {
            btnVehiculo.setVisibility(View.GONE);
        }

        if (session.tieneAcceso(Constants.OPCION_MENU_MOVIMIENTO)) {
            btnMovimiento.setVisibility(View.VISIBLE);
            btnMovimiento.setOnClickListener(v ->
                startActivity(new Intent(this, MovimientoActivity.class)));
        } else {
            btnMovimiento.setVisibility(View.GONE);
        }

        if (session.tieneAcceso(Constants.OPCION_MENU_REPARACION)) {
            btnReparacion.setVisibility(View.VISIBLE);
            btnReparacion.setOnClickListener(v ->
                startActivity(new Intent(this, ReparacionActivity.class)));
        } else {
            btnReparacion.setVisibility(View.GONE);
        }

        if (session.tieneAcceso(Constants.OPCION_MENU_VENTA)) {
            btnVenta.setVisibility(View.VISIBLE);
            btnVenta.setOnClickListener(v ->
                startActivity(new Intent(this, VentaActivity.class)));
        } else {
            btnVenta.setVisibility(View.GONE);
        }

        if (session.tieneAcceso(Constants.OPCION_MENU_CATALOGOS)) {
            btnCatalogos.setVisibility(View.VISIBLE);
            btnCatalogos.setOnClickListener(v ->
                startActivity(new Intent(this, CatalogosActivity.class)));
        } else {
            btnCatalogos.setVisibility(View.GONE);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Desea cerrar la aplicación?")
            .setPositiveButton("Sí", (dialog, which) -> super.onBackPressed())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        tvBienvenida  = null;
        btnLogout     = null;
        btnImportador = null;
        btnVehiculo   = null;
        btnMovimiento = null;
        btnReparacion = null;
        btnVenta      = null;
        btnCatalogos  = null;
        super.onDestroy();
    }
}
