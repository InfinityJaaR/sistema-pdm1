package com.ues.sistema_pdm1.activities.personalInterno;

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
import com.ues.sistema_pdm1.models.PersonalInterno;

public class PersonalInternoViewDialog extends DialogFragment {

    public static PersonalInternoViewDialog newInstance(PersonalInterno p) {
        PersonalInternoViewDialog dialog = new PersonalInternoViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID", p.getId());
        args.putString("NOMBRE", p.getNombrePersonal());
        args.putString("APELLIDO", p.getApellidoPersonal());
        args.putString("CARGO", p.getCargo());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_personal_interno_view, null);

        TextView tvId       = view.findViewById(R.id.tv_personal_id);
        TextView tvNombre   = view.findViewById(R.id.tv_personal_nombre);
        TextView tvApellido = view.findViewById(R.id.tv_personal_apellido);
        TextView tvCargo    = view.findViewById(R.id.tv_personal_cargo);
        Button btnCerrar    = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID"));
        tvNombre.setText(args.getString("NOMBRE"));
        tvApellido.setText(args.getString("APELLIDO"));
        tvCargo.setText(args.getString("CARGO"));

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle("Detalle de Personal")
            .setView(view)
            .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}