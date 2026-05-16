package com.ues.sistema_pdm1.activities.modelo;

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
import com.ues.sistema_pdm1.models.Modelo;

public class ModeloViewDialog extends DialogFragment {

    public static ModeloViewDialog newInstance(Modelo modelo) {
        ModeloViewDialog dialog = new ModeloViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID_MODELO",       modelo.getId());
        args.putInt("ID_MARCA",        modelo.getIdMarca());
        args.putString("NOMBRE_MODELO", modelo.getNombreModelo());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_modelo_view, null);

        TextView tvId     = view.findViewById(R.id.tv_id_modelo);
        TextView tvMarca  = view.findViewById(R.id.tv_marca_modelo);
        TextView tvNombre = view.findViewById(R.id.tv_nombre_modelo);
        Button   btnCerrar = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: "      + args.getInt("ID_MODELO"));
        tvMarca.setText("Marca ID: " + args.getInt("ID_MARCA"));
        tvNombre.setText("Nombre: "  + args.getString("NOMBRE_MODELO"));

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Detalle de Modelo")
                .setView(view)
                .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}