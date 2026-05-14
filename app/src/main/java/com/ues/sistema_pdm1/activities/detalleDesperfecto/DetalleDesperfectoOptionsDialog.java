package com.ues.sistema_pdm1.activities.detalleDesperfecto;

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
import com.ues.sistema_pdm1.models.DetalleDesperfecto;

public class DetalleDesperfectoOptionsDialog extends DialogFragment {

    private Runnable onVer, onEditar, onEliminar;
    private String titulo;

    public static DetalleDesperfectoOptionsDialog newInstance(DetalleDesperfecto d) {
        DetalleDesperfectoOptionsDialog dialog = new DetalleDesperfectoOptionsDialog();
        Bundle args = new Bundle();
        // Usamos la descripción como título en el modal de opciones
        args.putString("TITULO", d.getDescripcionDetalle());
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
        if (getArguments() != null) {
            titulo = getArguments().getString("TITULO");
        }

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_options, null);

        // ID correcto según dialog_options.xml es txt_opciones_crud
        TextView tvTitulo = view.findViewById(R.id.txt_opciones_crud);
        if (tvTitulo != null && titulo != null) {
            tvTitulo.setText(titulo);
        }

        Button btnVer      = view.findViewById(R.id.btn_ver);
        Button btnEditar   = view.findViewById(R.id.btn_editar);
        Button btnEliminar = view.findViewById(R.id.btn_eliminar);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
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
