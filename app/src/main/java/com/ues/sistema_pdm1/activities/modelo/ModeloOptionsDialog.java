package com.ues.sistema_pdm1.activities.modelo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.models.Modelo;

public class ModeloOptionsDialog extends DialogFragment {

    private Runnable onVer, onEditar, onEliminar;

    public static ModeloOptionsDialog newInstance(Modelo modelo) {
        ModeloOptionsDialog dialog = new ModeloOptionsDialog();
        Bundle args = new Bundle();
        args.putString("NOMBRE", modelo.getNombreModelo());
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