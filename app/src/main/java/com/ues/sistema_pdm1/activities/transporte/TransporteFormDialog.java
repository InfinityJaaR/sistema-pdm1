package com.ues.sistema_pdm1.activities.transporte;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.TipoTransporte;
import com.ues.sistema_pdm1.models.Transporte;

import java.util.ArrayList;
import java.util.List;

public class TransporteFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Transporte transporte);
    }

    private OnSaveListener listener;

    public static TransporteFormDialog newInstance(Transporte t) {
        TransporteFormDialog dialog = new TransporteFormDialog();
        Bundle args = new Bundle();
        if (t != null) {
            args.putInt("ID", t.getId());
            args.putInt("ID_TIPO", t.getIdTipoTransporte());
            args.putString("PLACA", t.getPlaca());
            args.putString("DESCRIPCION", t.getDescripcionTransporte());
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
            .inflate(R.layout.dialog_transporte_form, null);

        Spinner spinnerTipo    = view.findViewById(R.id.spinner_tipo_transporte);
        EditText etPlaca       = view.findViewById(R.id.et_transporte_placa);
        EditText etDescripcion = view.findViewById(R.id.et_transporte_descripcion);
        Button btnGuardar      = view.findViewById(R.id.btn_guardar);
        Button btnCancelar     = view.findViewById(R.id.btn_cancelar);

        List<TipoTransporte> tipos = new ArrayList<>();
        try {
            GenericDAO<TipoTransporte> tipoDAO =
                new GenericDAO<>(getContext(), TipoTransporte.class, "tipo_transporte");
            tipos = tipoDAO.obtenerTodos();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar tipos de transporte",
                Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<TipoTransporte> adapterTipo = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        Bundle args     = getArguments();
        boolean esEdicion = args != null && args.getInt("ID") > 0;
        int id          = esEdicion ? args.getInt("ID") : 0;

        if (esEdicion) {
            etPlaca.setText(args.getString("PLACA"));
            etDescripcion.setText(args.getString("DESCRIPCION"));

            int idTipoGuardado = args.getInt("ID_TIPO");
            for (int i = 0; i < tipos.size(); i++) {
                if (tipos.get(i).getId() == idTipoGuardado) {
                    spinnerTipo.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(esEdicion ? "Editar Transporte" : "Nuevo Transporte")
            .setView(view)
            .create();

        btnGuardar.setOnClickListener(v -> {
            String placa       = etPlaca.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (placa.isEmpty()) {
                Toast.makeText(getActivity(), "La placa es obligatoria",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            TipoTransporte tipoSeleccionado = (TipoTransporte) spinnerTipo.getSelectedItem();
            if (tipoSeleccionado == null) {
                Toast.makeText(getActivity(), "Seleccione un tipo de transporte",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onSave(new Transporte(id, tipoSeleccionado.getId(), placa, descripcion));
            }
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
