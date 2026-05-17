package com.ues.sistema_pdm1.activities.modelo;

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
import com.ues.sistema_pdm1.models.Modelo;

public class ModeloFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Modelo modelo);
    }

    private OnSaveListener listener;

    public static ModeloFormDialog newInstance(Modelo modelo, int idMarca) {
        ModeloFormDialog dialog = new ModeloFormDialog();
        Bundle args = new Bundle();
        args.putInt("ID_MARCA", idMarca);
        if (modelo != null) {
            args.putInt("ID_MODELO",        modelo.getId());
            args.putString("NOMBRE_MODELO", modelo.getNombreModelo());
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
                .inflate(R.layout.dialog_modelo_form, null);

        EditText etNombre    = view.findViewById(R.id.et_nombre_modelo);
        Button   btnGuardar  = view.findViewById(R.id.btn_guardar);
        Button   btnCancelar = view.findViewById(R.id.btn_cancelar);

        Bundle  args      = getArguments();
        boolean esEdicion = args != null && args.getInt("ID_MODELO") > 0;
        int     id        = esEdicion ? args.getInt("ID_MODELO") : 0;
        int     idMarca   = args != null ? args.getInt("ID_MARCA") : 0;

        if (esEdicion)
            etNombre.setText(args.getString("NOMBRE_MODELO"));

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(esEdicion
                        ? getString(R.string.title_editar_modelo)
                        : getString(R.string.title_nuevo_modelo))
                .setView(view)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.error_nombre_modelo), Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null)
                listener.onSave(new Modelo(id, idMarca, nombre));
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}