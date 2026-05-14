package com.ues.sistema_pdm1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.importador.ImportadorActivity;
import com.ues.sistema_pdm1.activities.marca.MarcaActivity;
import com.ues.sistema_pdm1.activities.movimiento.MovimientoActivity;
import com.ues.sistema_pdm1.activities.personalInterno.PersonalInternoActivity;
import com.ues.sistema_pdm1.activities.transporte.TransporteActivity;
import com.ues.sistema_pdm1.activities.reparacion.ReparacionActivity;
import com.ues.sistema_pdm1.activities.venta.VentaActivity;
import com.ues.sistema_pdm1.activities.vehiculo.VehiculoActivity;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.LlenarBDGpo02;
import com.ues.sistema_pdm1.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvUserName;

    private LinearLayout row1, row2, row3, row4, row5, row6, row7;

    private LinearLayout cardImportadores, cardImportaciones;
    private LinearLayout cardVehiculos,    cardDesperfectos;
    private LinearLayout cardMovimientos,  cardTransporte;
    private LinearLayout cardReparaciones, cardTalleres;
    private LinearLayout cardVentas,       cardBodegas;
    private LinearLayout cardPersonal,     cardSeccion;
    private LinearLayout cardMarca;

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

        vincularVistas();
        configurarLogout();
        configurarTarjetas();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.nav_logout)
                    .setMessage("¿Desea cerrar la aplicación?")
                    .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                    .setNegativeButton(R.string.no,  (dialog, which) -> dialog.dismiss())
                    .show();
            }
        });
    }

    private void vincularVistas() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(SessionManager.getInstance().getNombreUsuario());

        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);
        row3 = findViewById(R.id.row3);
        row4 = findViewById(R.id.row4);
        row5 = findViewById(R.id.row5);
        row6 = findViewById(R.id.row6);
        row7 = findViewById(R.id.row7);

        cardImportadores  = findViewById(R.id.cardImportadores);
        cardImportaciones = findViewById(R.id.cardImportaciones);
        cardVehiculos     = findViewById(R.id.cardVehiculos);
        cardDesperfectos  = findViewById(R.id.cardDesperfectos);
        cardMovimientos   = findViewById(R.id.cardMovimientos);
        cardTransporte    = findViewById(R.id.cardTransporte);
        cardReparaciones  = findViewById(R.id.cardReparaciones);
        cardTalleres      = findViewById(R.id.cardTalleres);
        cardVentas        = findViewById(R.id.cardVentas);
        cardBodegas       = findViewById(R.id.cardBodegas);
        cardPersonal      = findViewById(R.id.cardPersonal);
        cardSeccion       = findViewById(R.id.cardSeccion);
        cardMarca         = findViewById(R.id.cardMarca);
    }

    private void configurarLogout() {
        LinearLayout layoutUser = findViewById(R.id.layoutUser);
        layoutUser.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    SessionManager.getInstance().logout();
                    irALogin();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show()
        );
    }

    private void configurarTarjetas() {
        configurarTarjeta(cardImportadores,  Constants.MENU_IMPORTADOR);
        configurarTarjeta(cardImportaciones, Constants.MENU_IMPORTACION);
        configurarTarjeta(cardVehiculos,     Constants.MENU_VEHICULO);
        configurarTarjeta(cardDesperfectos,  Constants.MENU_DESPERFECTO);
        configurarTarjeta(cardMovimientos,   Constants.MENU_MOVIMIENTO);
        configurarTarjeta(cardTransporte,    Constants.MENU_TRANSPORTE);
        configurarTarjeta(cardReparaciones,  Constants.MENU_REPARACION);
        configurarTarjeta(cardTalleres,      Constants.MENU_TALLER);
        configurarTarjeta(cardVentas,        Constants.MENU_VENTA);
        configurarTarjeta(cardBodegas,       Constants.MENU_BODEGA);
        configurarTarjeta(cardPersonal,      Constants.MENU_PERSONAL);
        configurarTarjeta(cardSeccion,       Constants.MENU_SECCION);
        configurarTarjeta(cardMarca,         Constants.MENU_MARCA);

        ocultarFilaSiSinAcceso(row1, Constants.MENU_IMPORTADOR,  Constants.MENU_IMPORTACION);
        ocultarFilaSiSinAcceso(row2, Constants.MENU_VEHICULO,    Constants.MENU_DESPERFECTO);
        ocultarFilaSiSinAcceso(row3, Constants.MENU_MOVIMIENTO,  Constants.MENU_TRANSPORTE);
        ocultarFilaSiSinAcceso(row4, Constants.MENU_REPARACION,  Constants.MENU_TALLER);
        ocultarFilaSiSinAcceso(row5, Constants.MENU_VENTA,       Constants.MENU_BODEGA);
        ocultarFilaSiSinAcceso(row6, Constants.MENU_PERSONAL,    Constants.MENU_SECCION);

        if (!SessionManager.getInstance().tieneAcceso(Constants.MENU_MARCA)) {
            row7.setVisibility(View.GONE);
        }
    }

    private void configurarTarjeta(LinearLayout card, int menuId) {
        if (SessionManager.getInstance().tieneAcceso(menuId)) {
            card.setOnClickListener(v -> abrirModulo(menuId));
        } else {
            card.setVisibility(View.GONE);
        }
    }

    private void ocultarFilaSiSinAcceso(LinearLayout fila, int idA, int idB) {
        SessionManager s = SessionManager.getInstance();
        if (!s.tieneAcceso(idA) && !s.tieneAcceso(idB)) {
            fila.setVisibility(View.GONE);
        }
    }

    private void abrirModulo(int idOpcion) {
        Intent intent;
        switch (idOpcion) {
            case Constants.MENU_IMPORTADOR:
                intent = new Intent(this, ImportadorActivity.class);
                break;
            case Constants.MENU_VEHICULO:
                intent = new Intent(this, VehiculoActivity.class);
                break;
            case Constants.MENU_MOVIMIENTO:
                intent = new Intent(this, MovimientoActivity.class);
                break;
            case Constants.MENU_REPARACION:
                intent = new Intent(this, ReparacionActivity.class);
                break;
            case Constants.MENU_VENTA:
                intent = new Intent(this, VentaActivity.class);
                break;
            case Constants.MENU_MARCA:
                intent = new Intent(this, MarcaActivity.class);
                break;
            case Constants.MENU_PERSONAL:
                intent = new Intent(this, PersonalInternoActivity.class);
                break;
            case Constants.MENU_TRANSPORTE:
                intent = new Intent(this, TransporteActivity.class);
                break;
            case Constants.MENU_IMPORTACION:
                intent = new Intent(this, com.ues.sistema_pdm1.activities.importacion.ImportacionActivity.class);
                break;
            default:
                Toast.makeText(this, "Módulo en construcción", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}