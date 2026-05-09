package com.ues.sistema_pdm1.activities.marca;

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
import com.ues.sistema_pdm1.models.Marca;

public class MarcaViewDialog extends DialogFragment {

    public static MarcaViewDialog newInstance(Marca marca) {
        MarcaViewDialog dialog = new MarcaViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID", marca.getId());
        args.putString("NOMBRE", marca.getNombreMarca());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_marca_view, null);

        TextView tvId     = view.findViewById(R.id.tv_id_marca);
        TextView tvNombre = view.findViewById(R.id.tv_nombre_marca);
        Button btnCerrar  = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID"));
        tvNombre.setText(args.getString("NOMBRE"));

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle("Detalle de Marca")
            .setView(view)
            .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}