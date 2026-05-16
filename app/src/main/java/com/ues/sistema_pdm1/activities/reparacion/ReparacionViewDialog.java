package com.ues.sistema_pdm1.activities.reparacion;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.models.Reparacion;

public class ReparacionViewDialog extends DialogFragment {

    public static ReparacionViewDialog newInstance(Reparacion reparacion) {
        ReparacionViewDialog dialog = new ReparacionViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID_REPARACION",            reparacion.getId());
        args.putInt("ID_VEHICULO",              reparacion.getIdVehiculo());
        args.putInt("ID_TALLER",                reparacion.getIdTaller());
        args.putString("FECHA_INICIO",          reparacion.getFechaInicio());
        args.putString("FECHA_FIN",             reparacion.getFechaFin());
        args.putString("DESCRIPCION_TRABAJO",   reparacion.getDescripcionTrabajo());
        args.putInt("APTO_PARA_VENTA",          reparacion.getAptoParaVenta());
        args.putInt("REQUIERE_OTRA_REPARACION", reparacion.getRequiereOtraReparacion());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_reparacion_view, null);

        TextView tvId           = view.findViewById(R.id.tv_id_reparacion);
        TextView tvVehiculo     = view.findViewById(R.id.tv_vehiculo_reparacion);
        TextView tvTaller       = view.findViewById(R.id.tv_taller_reparacion);
        TextView tvFechaInicio  = view.findViewById(R.id.tv_fecha_inicio_reparacion);
        TextView tvFechaFin     = view.findViewById(R.id.tv_fecha_fin_reparacion);
        TextView tvDescripcion  = view.findViewById(R.id.tv_descripcion_reparacion);
        TextView tvAptoVenta    = view.findViewById(R.id.tv_apto_venta_reparacion);
        TextView tvRequiereRep  = view.findViewById(R.id.tv_requiere_reparacion);
        Button   btnCerrar      = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: "            + args.getInt("ID_REPARACION"));
        tvVehiculo.setText("Vehículo ID: " + args.getInt("ID_VEHICULO"));
        tvTaller.setText("Taller ID: "     + args.getInt("ID_TALLER"));
        tvFechaInicio.setText("Fecha inicio: " + args.getString("FECHA_INICIO"));
        tvFechaFin.setText("Fecha fin: "       + args.getString("FECHA_FIN"));
        tvDescripcion.setText("Descripción: "  + args.getString("DESCRIPCION_TRABAJO"));
        tvAptoVenta.setText(args.getInt("APTO_PARA_VENTA") == 1
                ? "✔ Apto para venta" : "✘ No apto para venta");
        tvRequiereRep.setText(args.getInt("REQUIERE_OTRA_REPARACION") == 1
                ? "⚠ Requiere otra reparación" : "✔ No requiere otra reparación");

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Detalle de Reparación")
                .setView(view)
                .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}