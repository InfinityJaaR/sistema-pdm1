package com.ues.sistema_pdm1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.activities.importador.ImportadorActivity;
import com.ues.sistema_pdm1.activities.movimiento.MovimientoActivity;
import com.ues.sistema_pdm1.activities.reparacion.ReparacionActivity;
import com.ues.sistema_pdm1.activities.marca.MarcaActivity;
import com.ues.sistema_pdm1.activities.venta.VentaActivity;
import com.ues.sistema_pdm1.activities.vehiculo.VehiculoActivity;
import com.ues.sistema_pdm1.adapters.MenuItemAdapter;
import com.ues.sistema_pdm1.models.MenuItem;
import com.ues.sistema_pdm1.utils.Constants;
import com.ues.sistema_pdm1.utils.LlenarBDGpo02;
import com.ues.sistema_pdm1.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView lvOpcionesMenu;
    private TextView tvUsername;
    private Button btnLogout;

    private List<MenuItem> menuItems;
    private MenuItemAdapter adapter;

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

        drawerLayout   = findViewById(R.id.drawer_layout);
        lvOpcionesMenu = findViewById(R.id.lv_opciones_menu);
        tvUsername     = findViewById(R.id.tvUsername);
        btnLogout      = findViewById(R.id.btn_logout);

        tvUsername.setText("Usuario: " + SessionManager.getInstance().getNombreUsuario());

        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance().logout();
            irALogin();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Salir")
                    .setMessage("¿Desea cerrar la aplicación?")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            }
        });

        cargarMenuDinamico();

        lvOpcionesMenu.setOnItemClickListener((parent, view, position, id) -> {
            MenuItem item = menuItems.get(position);
            abrirModulo(item.getId());
        });

    }

    private void cargarMenuDinamico() {
        menuItems = new ArrayList<>();
        SessionManager session = SessionManager.getInstance();

        for (MenuItem item : crearTodasLasOpciones()) {
            if (session.tieneAcceso(item.getId())) {
                menuItems.add(item);
            }
        }

        adapter = new MenuItemAdapter(this, menuItems);
        lvOpcionesMenu.setAdapter(adapter);
    }

    private List<MenuItem> crearTodasLasOpciones() {
        List<MenuItem> opciones = new ArrayList<>();
        opciones.add(new MenuItem(Constants.MENU_IMPORTADOR,      "Importador",         Constants.DESC_IMPORTADOR));
        opciones.add(new MenuItem(Constants.MENU_VEHICULO,        "Vehículo",           Constants.DESC_VEHICULO));
        opciones.add(new MenuItem(Constants.MENU_IMPORTACION,     "Importación",        Constants.DESC_IMPORTACION));
        opciones.add(new MenuItem(Constants.MENU_BODEGA,          "Bodega",             Constants.DESC_BODEGA));
        opciones.add(new MenuItem(Constants.MENU_SECCION,         "Sección",            Constants.DESC_SECCION));
        opciones.add(new MenuItem(Constants.MENU_MOVIMIENTO,      "Movimiento",         Constants.DESC_MOVIMIENTO));
        opciones.add(new MenuItem(Constants.MENU_TRANSPORTE,      "Transporte",         Constants.DESC_TRANSPORTE));
        opciones.add(new MenuItem(Constants.MENU_REPARACION,      "Reparación",         Constants.DESC_REPARACION));
        opciones.add(new MenuItem(Constants.MENU_TALLER,          "Taller",             Constants.DESC_TALLER));
        opciones.add(new MenuItem(Constants.MENU_VENTA,           "Venta",              Constants.DESC_VENTA));
        opciones.add(new MenuItem(Constants.MENU_DESPERFECTO,     "Desperfecto",        Constants.DESC_DESPERFECTO));
        opciones.add(new MenuItem(Constants.MENU_PERSONAL,        "Personal Interno",   Constants.DESC_PERSONAL));
        opciones.add(new MenuItem(Constants.MENU_MARCA,           "Marca",              Constants.DESC_MARCA));
        opciones.add(new MenuItem(Constants.MENU_TIPO_TRANSPORTE, "Tipo de Transporte", Constants.DESC_TIPO_TRANSPORTE));
        opciones.add(new MenuItem(Constants.MENU_TIPO_VEHICULO,   "Tipo de Vehículo",   Constants.DESC_TIPO_VEHICULO));
        return opciones;
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

    @Override
    protected void onDestroy() {
        drawerLayout   = null;
        lvOpcionesMenu = null;
        tvUsername     = null;
        btnLogout      = null;
        super.onDestroy();
    }
}