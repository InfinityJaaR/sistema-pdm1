package com.ues.sistema_pdm1.activities.bodega;import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.models.Bodega;

public class BodegaOptionsDialog extends DialogFragment {
    private Runnable onVer, onEditar, onEliminar;

    public static BodegaOptionsDialog newInstance(Bodega bodega) {
        BodegaOptionsDialog dialog = new BodegaOptionsDialog();
        Bundle args = new Bundle();
        // Pasamos el nombre para mostrarlo en el encabezado de las opciones
        args.putString("NOMBRE", bodega.getNombreBodega());
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

        // Referencias del layout dialog_options.xml
        Button btnVer      = view.findViewById(R.id.btn_ver);
        Button btnEditar   = view.findViewById(R.id.btn_editar);
        Button btnEliminar = view.findViewById(R.id.btn_eliminar);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setView(view)
                .create();

        // Configuración de clics
        btnVer.setOnClickListener(v -> {
            if (onVer != null) onVer.run();
            dismiss();
        });

        btnEditar.setOnClickListener(v -> {
            if (onEditar != null) onEditar.run();
            dismiss();
        });

        btnEliminar.setOnClickListener(v -> {
            if (onEliminar != null) onEliminar.run();
            dismiss();
        });

        return dialog;
    }
}