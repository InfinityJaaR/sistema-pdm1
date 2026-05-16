package com.ues.sistema_pdm1.activities.vehiculo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Seccion;
import com.ues.sistema_pdm1.models.TipoVehiculo;
import com.ues.sistema_pdm1.models.Vehiculo;

public class VehiculoViewDialog extends DialogFragment {

    private TextView tvVin, tvModelo, tvTipo, tvAnio, tvColor, tvSeccion, tvEstado, tvImportacion;

    public static VehiculoViewDialog newInstance(Vehiculo vehiculo) {
        VehiculoViewDialog dialog = new VehiculoViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID", vehiculo.getId());
        args.putInt("ID_TIPO", vehiculo.getIdTipoVehiculo());
        args.putInt("ID_SECCION", vehiculo.getIdSeccion());
        args.putInt("ID_MODELO", vehiculo.getIdModelo());
        args.putInt("ID_IMPORTACION", vehiculo.getIdImportacion());
        args.putString("VIN", vehiculo.getVin());
        args.putInt("ANIO", vehiculo.getAnio());
        args.putString("COLOR", vehiculo.getColorVehiculo());
        args.putString("ESTADO", vehiculo.getEstadoVehiculo());
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos un estilo que no tenga título para el diálogo
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_MinWidth);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_vehiculo_view, null);

        vincularVistas(view);
        cargarDatos();

        Button btnCerrar = view.findViewById(R.id.btn_cerrar);
        
        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setView(view)
                .create();

        // Aplicamos fondo transparente para que se vean los bordes redondeados del CardView
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Opcional: animaciones de entrada
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        }

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    private void vincularVistas(View v) {
        tvVin = v.findViewById(R.id.tv_view_vin);
        tvModelo = v.findViewById(R.id.tv_view_modelo);
        tvTipo = v.findViewById(R.id.tv_view_tipo);
        tvAnio = v.findViewById(R.id.tv_view_anio);
        tvColor = v.findViewById(R.id.tv_view_color);
        tvSeccion = v.findViewById(R.id.tv_view_seccion);
        tvEstado = v.findViewById(R.id.tv_view_estado);
        tvImportacion = v.findViewById(R.id.tv_view_importacion);
    }

    private void cargarDatos() {
        Bundle args = getArguments();
        if (args == null) return;

        tvVin.setText(args.getString("VIN"));
        tvAnio.setText(String.valueOf(args.getInt("ANIO")));
        tvColor.setText(args.getString("COLOR"));
        tvEstado.setText(args.getString("ESTADO"));

        try {
            GenericDAO<Modelo> modeloDAO = new GenericDAO<>(getActivity(), Modelo.class, "modelo");
            GenericDAO<TipoVehiculo> tipoDAO = new GenericDAO<>(getActivity(), TipoVehiculo.class, "tipo_vehiculo");
            GenericDAO<Seccion> seccionDAO = new GenericDAO<>(getActivity(), Seccion.class, "seccion");
            GenericDAO<Importacion> importacionDAO = new GenericDAO<>(getActivity(), Importacion.class, "importacion");

            Modelo m = modeloDAO.obtenerPorId(args.getInt("ID_MODELO"));
            TipoVehiculo t = tipoDAO.obtenerPorId(args.getInt("ID_TIPO"));
            Seccion s = seccionDAO.obtenerPorId(args.getInt("ID_SECCION"));
            Importacion i = importacionDAO.obtenerPorId(args.getInt("ID_IMPORTACION"));

            if (m != null) tvModelo.setText(m.getNombreModelo());
            if (t != null) tvTipo.setText(t.getDescripcionTipoVehiculo());
            if (s != null) tvSeccion.setText(s.toString());
            if (i != null) tvImportacion.setText(i.toString());

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error al cargar detalles", Toast.LENGTH_SHORT).show();
        }
    }
}
