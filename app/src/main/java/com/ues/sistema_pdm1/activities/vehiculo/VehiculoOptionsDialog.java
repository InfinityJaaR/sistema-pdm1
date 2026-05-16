package com.ues.sistema_pdm1.activities.vehiculo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.models.Vehiculo;

public class VehiculoOptionsDialog extends DialogFragment {

    private Runnable onVer, onEditar, onEliminar;

    public static VehiculoOptionsDialog newInstance(Vehiculo vehiculo) {
        VehiculoOptionsDialog dialog = new VehiculoOptionsDialog();
        Bundle args = new Bundle();
        args.putString("VIN", vehiculo.getVin());
        dialog.setArguments(args);
        return dialog;
    }

    public void setCallbacks(Runnable ver, Runnable editar, Runnable eliminar) {
        this.onVer      = ver;
        this.onEditar   = editar;
        this.onEliminar = eliminar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_options, null);

        // Ya no ocultamos el TextView txt_opciones_crud. 
        // El layout dialog_options.xml ya tiene este TextView con el texto "Opciones" centrado.

        Button btnVer      = view.findViewById(R.id.btn_ver);
        Button btnEditar   = view.findViewById(R.id.btn_editar);
        Button btnEliminar = view.findViewById(R.id.btn_eliminar);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setView(view)
                .create();

        btnVer.setOnClickListener(v -> {
            dialog.dismiss();
            if (onVer != null) onVer.run();
        });
        btnEditar.setOnClickListener(v -> {
            dialog.dismiss();
            if (onEditar != null) onEditar.run();
        });
        btnEliminar.setOnClickListener(v -> {
            dialog.dismiss();
            if (onEliminar != null) onEliminar.run();
        });

        return dialog;
    }
}
