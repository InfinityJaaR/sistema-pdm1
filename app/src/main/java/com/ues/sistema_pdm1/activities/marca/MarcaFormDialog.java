package com.ues.sistema_pdm1.activities.marca;

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
import com.ues.sistema_pdm1.models.Marca;

public class MarcaFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Marca marca);
    }

    private OnSaveListener listener;

    public static MarcaFormDialog newInstance(Marca marca) {
        MarcaFormDialog dialog = new MarcaFormDialog();
        Bundle args = new Bundle();
        if (marca != null) {
            args.putInt("ID", marca.getId());
            args.putString("NOMBRE", marca.getNombreMarca());
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
            .inflate(R.layout.dialog_marca_form, null);

        EditText etNombre  = view.findViewById(R.id.et_nombre_marca);
        Button btnGuardar  = view.findViewById(R.id.btn_guardar);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);

        Bundle args   = getArguments();
        boolean esEdicion = args != null && args.getInt("ID") > 0;
        int id        = esEdicion ? args.getInt("ID") : 0;

        if (esEdicion) {
            etNombre.setText(args.getString("NOMBRE"));
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(esEdicion ? getString(R.string.title_editar_marca) : getString(R.string.title_nueva_marca))
            .setView(view)
            .create();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.error_nombre_marca),
                    Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) listener.onSave(new Marca(id, nombre));
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}