package com.ues.sistema_pdm1.activities.bodega;

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
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.models.Distrito;

import java.sql.SQLException;
import java.util.List;

public class BodegaFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Bodega bodega);
    }

    private OnSaveListener listener;
    private GenericDAO<Distrito> distritoDAO;
    private List<Distrito> listaDistritos;

    public static BodegaFormDialog newInstance(Bodega bodega) {
        BodegaFormDialog dialog = new BodegaFormDialog();
        Bundle args = new Bundle();
        if (bodega != null) {
            args.putInt("ID_BODEGA", bodega.getId());
            args.putInt("ID_DISTRITO", bodega.getIdDistrito());
            args.putString("NOMBRE_BODEGA", bodega.getNombreBodega());
            args.putString("DIRECCION_BODEGA", bodega.getDireccionBodega());
            args.putInt("CAPACIDAD_TOTAL", bodega.getCapacidadTotal());
            args.putInt("CAPACIDAD_ACTUAL", bodega.getCapacidadActual());
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
                .inflate(R.layout.dialog_bodega_form, null);

        Spinner spDistrito = view.findViewById(R.id.sp_id_distrito);
        EditText etNombre = view.findViewById(R.id.et_nombre_bodega);
        EditText etDireccion = view.findViewById(R.id.et_direccion_bodega);
        EditText etCapacidadTotal = view.findViewById(R.id.et_capacidad_total);
        EditText etCapacidadActual = view.findViewById(R.id.et_capacidad_actual);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);

        // Cargar Distritos en el Spinner
        distritoDAO = new GenericDAO<>(requireContext(), Distrito.class, "DISTRITO");
        try {
            listaDistritos = distritoDAO.obtenerTodos();
        } catch (SQLException e) {
            listaDistritos = new java.util.ArrayList<>();
        }
        ArrayAdapter<Distrito> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listaDistritos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrito.setAdapter(adapter);

        Bundle args = getArguments();
        boolean esEdicion = args != null && args.getInt("ID_BODEGA") > 0;

        final int id = esEdicion ? args.getInt("ID_BODEGA") : 0;

        if (esEdicion) {
            etNombre.setText(args.getString("NOMBRE_BODEGA"));
            etDireccion.setText(args.getString("DIRECCION_BODEGA"));
            etCapacidadTotal.setText(String.valueOf(args.getInt("CAPACIDAD_TOTAL")));
            etCapacidadActual.setText(String.valueOf(args.getInt("CAPACIDAD_ACTUAL")));

            int idDistrito = args.getInt("ID_DISTRITO");
            for (int i = 0; i < listaDistritos.size(); i++) {
                if (listaDistritos.get(i).getId() == idDistrito) {
                    spDistrito.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(esEdicion ? "Editar Bodega" : "Nueva Bodega")
                .setView(view)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String capTotalStr = etCapacidadTotal.getText().toString().trim();
            String capActualStr = etCapacidadActual.getText().toString().trim();
            Distrito distrito = (Distrito) spDistrito.getSelectedItem();

            if (distrito == null) {
                Toast.makeText(getActivity(), "Seleccione un distrito", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nombre.isEmpty()) {
                etNombre.setError("Campo requerido");
                return;
            }
            if (direccion.isEmpty()) {
                etDireccion.setError("Campo requerido");
                return;
            }

            int capT = capTotalStr.isEmpty() ? 0 : Integer.parseInt(capTotalStr);
            int capA = capActualStr.isEmpty() ? 0 : Integer.parseInt(capActualStr);

            // Validación de capacidad máxima
            if (capT > 150) {
                etCapacidadTotal.setError("La capacidad máxima no puede ser superior a 150");
                etCapacidadTotal.requestFocus();
                return;
            }

            // Validación lógica: capacidad actual no puede ser mayor a la total
            if (capA > capT) {
                etCapacidadActual.setError("La capacidad actual no puede exceder la capacidad total");
                etCapacidadActual.requestFocus();
                return;
            }

            if (listener != null) {
                Bodega bodega = new Bodega(id, distrito.getId(), nombre, direccion, capT, capA);
                listener.onSave(bodega);
            }
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
