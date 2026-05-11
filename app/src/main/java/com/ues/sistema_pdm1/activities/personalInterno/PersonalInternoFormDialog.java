package com.ues.sistema_pdm1.activities.personalInterno;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.models.PersonalInterno;

public class PersonalInternoFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(PersonalInterno personal);
    }

    private OnSaveListener listener;

    public static PersonalInternoFormDialog newInstance(PersonalInterno p) {
        PersonalInternoFormDialog dialog = new PersonalInternoFormDialog();
        Bundle args = new Bundle();
        if (p != null) {
            args.putInt("ID", p.getId());
            args.putString("NOMBRE", p.getNombrePersonal());
            args.putString("APELLIDO", p.getApellidoPersonal());
            args.putString("CARGO", p.getCargo());
        }
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_personal_interno_form, null);

        EditText etNombre   = view.findViewById(R.id.et_personal_nombre);
        EditText etApellido = view.findViewById(R.id.et_personal_apellido);
        EditText etCargo    = view.findViewById(R.id.et_personal_cargo);
        Button btnGuardar   = view.findViewById(R.id.btn_guardar);
        Button btnCancelar  = view.findViewById(R.id.btn_cancelar);

        Bundle args     = getArguments();
        boolean esEdicion = args != null && args.getInt("ID") > 0;
        int id          = esEdicion ? args.getInt("ID") : 0;

        if (esEdicion) {
            etNombre.setText(args.getString("NOMBRE"));
            etApellido.setText(args.getString("APELLIDO"));
            etCargo.setText(args.getString("CARGO"));
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(esEdicion ? "Editar Personal" : "Nuevo Personal")
            .setView(view)
            .create();

        btnGuardar.setOnClickListener(v -> {
            String nombre   = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String cargo    = etCargo.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || cargo.isEmpty()) {
                Toast.makeText(getActivity(), "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) listener.onSave(new PersonalInterno(id, nombre, apellido, cargo));
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}