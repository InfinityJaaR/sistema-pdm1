package com.ues.sistema_pdm1.activities.venta;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VentaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Módulo Venta — En desarrollo");
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
    }
}
