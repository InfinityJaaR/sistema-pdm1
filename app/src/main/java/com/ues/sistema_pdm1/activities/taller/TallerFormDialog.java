package com.ues.sistema_pdm1.activities.taller;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Reparacion;
import com.ues.sistema_pdm1.models.Taller;

import java.util.List;

public class TallerFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Taller taller);
    }

    private OnSaveListener listener;

    public static TallerFormDialog newInstance(Taller taller) {
        TallerFormDialog dialog = new TallerFormDialog();
        Bundle args = new Bundle();
        if (taller != null) {
            args.putInt("ID_TALLER",           taller.getId());
            args.putString("NOMBRE_TALLER",    taller.getNombreTaller());
            args.putString("DIRECCION_TALLER", taller.getDireccionTaller());
            args.putString("TELEFONO_TALLER",  taller.getTelefonoTaller());
            args.putInt("AUTORIZADO",          taller.getAutorizado());
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
                .inflate(R.layout.dialog_taller_form, null);

        EditText  etNombre          = view.findViewById(R.id.et_nombre_taller);
        EditText  etDireccion       = view.findViewById(R.id.et_direccion_taller);
        EditText  etTelefono        = view.findViewById(R.id.et_telefono_taller);
        TextView  tvAutorizadoLabel = view.findViewById(R.id.tv_autorizado_label);
        CheckBox  cbAutorizado      = view.findViewById(R.id.cb_autorizado);
        Button    btnGuardar        = view.findViewById(R.id.btn_guardar);
        Button    btnCancelar       = view.findViewById(R.id.btn_cancelar);

        Bundle  args      = getArguments();
        boolean esEdicion = args != null && args.getInt("ID_TALLER") > 0;
        int     id        = esEdicion ? args.getInt("ID_TALLER") : 0;

        etTelefono.addTextChangedListener(new TextWatcher() {
            boolean isFormatting = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;
                String digits = s.toString().replaceAll("[^0-9]", "");
                if (digits.length() > 8) digits = digits.substring(0, 8);
                String formatted = digits.length() > 4
                        ? digits.substring(0, 4) + "-" + digits.substring(4)
                        : digits;
                s.replace(0, s.length(), formatted);
                isFormatting = false;
            }
        });

        if (esEdicion) {
            etNombre.setText(args.getString("NOMBRE_TALLER"));
            etDireccion.setText(args.getString("DIRECCION_TALLER"));
            etTelefono.setText(args.getString("TELEFONO_TALLER"));
            cbAutorizado.setChecked(args.getInt("AUTORIZADO") == 1);

            try {
                GenericDAO<Reparacion> reparacionDAO = new GenericDAO<>(getContext(), Reparacion.class, "reparacion");
                List<Reparacion> reparaciones = reparacionDAO.obtenerPor("ID_TALLER", String.valueOf(id));
                if (!reparaciones.isEmpty()) {
                    tvAutorizadoLabel.setVisibility(View.GONE);
                    cbAutorizado.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                // si falla la consulta dejamos el checkbox visible por seguridad
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(esEdicion
                        ? getString(R.string.title_editar_taller)
                        : getString(R.string.title_nuevo_taller))
                .setView(view)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String nombre     = etNombre.getText().toString().trim();
            String direccion  = etDireccion.getText().toString().trim();
            String telefono   = etTelefono.getText().toString().trim();
            int    autorizado = cbAutorizado.isChecked() ? 1 : 0;

            if (nombre.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.error_nombre_taller), Toast.LENGTH_SHORT).show();
                return;
            }
            if (direccion.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.error_direccion_taller), Toast.LENGTH_SHORT).show();
                return;
            }
            if (telefono.length() < 9) {
                Toast.makeText(getActivity(), getString(R.string.error_telefono_taller), Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null)
                listener.onSave(new Taller(id, nombre, direccion, telefono, autorizado));
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}