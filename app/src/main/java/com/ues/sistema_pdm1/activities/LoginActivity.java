package com.ues.sistema_pdm1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.utils.LlenarBDGpo02;
import com.ues.sistema_pdm1.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etClave;
    private Button btnLogin;
    private ProgressBar pbCargando;
    private TextView tvMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario  = findViewById(R.id.et_usuario);
        etClave    = findViewById(R.id.et_clave);
        btnLogin   = findViewById(R.id.btn_login);
        pbCargando = findViewById(R.id.pb_cargando);
        tvMensaje  = findViewById(R.id.tv_mensaje);

        SessionManager.getInstance().inicializar(getApplicationContext());
        LlenarBDGpo02.llenarDatosIniciales(getApplicationContext());

        if (SessionManager.getInstance().isLoggedIn()) {
            irAMain();
            return;
        }

        btnLogin.setOnClickListener(v -> intentarLogin());
    }

    private void intentarLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String clave   = etClave.getText().toString().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        pbCargando.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        tvMensaje.setVisibility(View.INVISIBLE);

        boolean exitoso = SessionManager.getInstance().login(usuario, clave);

        if (exitoso) {
            Toast.makeText(this, "Bienvenido " + usuario, Toast.LENGTH_SHORT).show();
            irAMain();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            etUsuario.setText("");
            etClave.setText("");
            pbCargando.setVisibility(View.INVISIBLE);
            btnLogin.setEnabled(true);
            etUsuario.requestFocus();
        }
    }

    private void irAMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        etUsuario  = null;
        etClave    = null;
        btnLogin   = null;
        pbCargando = null;
        tvMensaje  = null;
        super.onDestroy();
    }
}