package com.ues.sistema_pdm1.activities.seccion;

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
import com.ues.sistema_pdm1.activities.bodega.BodegaFormDialog;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Seccion;
import com.ues.sistema_pdm1.models.Bodega;

import java.sql.SQLException;
import java.util.List;

public class SeccionFormDialog extends DialogFragment {
    public interface OnSaveListener {
        void onSave(Seccion seccion);
    }

    private SeccionFormDialog.OnSaveListener listener;
    private GenericDAO<Bodega> bodegaDAO;
    private List<Bodega> listaBodegas;

    public static SeccionFormDialog newInstance(Seccion seccion) {
        SeccionFormDialog dialog = new SeccionFormDialog();
        Bundle args = new Bundle();
        if (seccion != null) {
            args.putInt("ID_SECCION", seccion.getId());
            args.putInt("ID_BODEGA", seccion.getIdBodega());
            args.putInt("NIVEL", seccion.getNivel());
            args.putInt("CAPACIDAD_MAXIMA", seccion.getCapacidadMaxima());
            args.putInt("CAPACIDAD_ACTUAL", seccion.getCapacidadActual());
        }
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnSaveListener(SeccionFormDialog.OnSaveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_seccion_form, null);

        Spinner spBodega = view.findViewById(R.id.sp_id_bodega);
        EditText etNivel = view.findViewById(R.id.et_nivel_seccion);
        EditText etCapacidadMaxima = view.findViewById(R.id.et_capacidad_max);
        EditText etCapacidadActual = view.findViewById(R.id.et_capacidad_actual);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);

        // Cargar Distritos en el Spinner
        bodegaDAO = new GenericDAO<>(requireContext(), Bodega.class, "BODEGA");
        try {
            listaBodegas = bodegaDAO.obtenerTodos();
        } catch (SQLException e) {
            listaBodegas = new java.util.ArrayList<>();
        }
        ArrayAdapter<Bodega> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listaBodegas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBodega.setAdapter(adapter);

        Bundle args = getArguments();
        boolean esEdicion = args != null && args.getInt("ID_SECCION") > 0;

        final int id = esEdicion ? args.getInt("ID_SECCION") : 0;

        if (esEdicion) {
            etNivel.setText(String.valueOf(args.getInt("NIVEL")));
            etCapacidadMaxima.setText(String.valueOf(args.getInt("CAPACIDAD_MAXIMA")));
            etCapacidadActual.setText(String.valueOf(args.getInt("CAPACIDAD_ACTUAL")));

            int idBodega = args.getInt("ID_BODEGA");
            for (int i = 0; i < listaBodegas.size(); i++) {
                if (listaBodegas.get(i).getId() == idBodega) {
                    spBodega.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(esEdicion ? "Editar Seccion" : "Nueva Seccion")
                .setView(view)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String nivel = etNivel.getText().toString().trim();
            String capMaxStr = etCapacidadMaxima.getText().toString().trim();
            String capActualStr = etCapacidadActual.getText().toString().trim();
            Bodega bodega = (Bodega) spBodega.getSelectedItem();

            if (bodega == null) {
                Toast.makeText(getActivity(), "Seleccione una bodega", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nivel.isEmpty()) {
                etNivel.setError("Campo requerido");
                return;
            }

            int capT = capMaxStr.isEmpty() ? 0 : Integer.parseInt(capMaxStr);
            int capA = capActualStr.isEmpty() ? 0 : Integer.parseInt(capActualStr);
            int nivelInt = nivel.isEmpty() ? 0 : Integer.parseInt(nivel);

            // Validación de capacidad máxima
            if (capT > 50) {
                etCapacidadMaxima.setError("La capacidad máxima no puede ser superior a 50");
                etCapacidadMaxima.requestFocus();
                return;
            }

            // Validación lógica: capacidad actual no puede ser mayor a la total
            if (capA > capT) {
                etCapacidadActual.setError("La capacidad actual no puede exceder la capacidad total");
                etCapacidadActual.requestFocus();
                return;
            }

            if (nivelInt < 1 || nivelInt > 3){
                etNivel.setError("El nivel debe estar en entre 1 y 3");
                etNivel.requestFocus();
                return;
            }

            if (listener != null) {
                Seccion seccion = new Seccion(id, bodega.getId(), nivelInt, capT, capA);
                listener.onSave(seccion);
            }
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
