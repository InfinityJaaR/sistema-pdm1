package com.ues.sistema_pdm1.activities.movimiento;

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
import com.ues.sistema_pdm1.models.Movimiento;
import com.ues.sistema_pdm1.models.PersonalInterno;
import com.ues.sistema_pdm1.models.Transporte;
import com.ues.sistema_pdm1.models.Vehiculo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovimientoFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Movimiento movimiento);
    }

    private OnSaveListener listener;

    public static MovimientoFormDialog newInstance(Movimiento m) {
        MovimientoFormDialog dialog = new MovimientoFormDialog();
        Bundle args = new Bundle();
        if (m != null) {
            args.putInt("ID",            m.getId());
            args.putInt("ID_TRANSPORTE", m.getIdTransporte());
            args.putInt("ID_PERSONAL",   m.getIdPersonal());
            args.putInt("ID_VEHICULO",   m.getIdVehiculo());
            args.putInt("ID_BODEGA",     m.getIdBodega());
            args.putString("TIPO",       m.getTipoMovimiento());
            args.putString("FECHA",      m.getFechaMovimiento());
            args.putString("MOTIVO",     m.getMotivo());
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
            .inflate(R.layout.dialog_movimiento_form, null);

        Spinner  spinnerTransporte  = view.findViewById(R.id.spinner_transporte);
        Spinner  spinnerPersonal    = view.findViewById(R.id.spinner_personal);
        Spinner  spinnerVehiculo    = view.findViewById(R.id.spinner_vehiculo);
        Spinner  spinnerBodega      = view.findViewById(R.id.spinner_bodega);
        Spinner  spinnerTipo        = view.findViewById(R.id.spinner_tipo_movimiento);
        EditText etFecha            = view.findViewById(R.id.et_movimiento_fecha);
        EditText etMotivo           = view.findViewById(R.id.et_movimiento_motivo);
        Button   btnGuardar         = view.findViewById(R.id.btn_guardar);
        Button   btnCancelar        = view.findViewById(R.id.btn_cancelar);

        List<Transporte>    transportes = new ArrayList<>();
        List<PersonalInterno> personales = new ArrayList<>();
        List<Vehiculo>      vehiculos   = new ArrayList<>();
        List<Bodega>        bodegas     = new ArrayList<>();

        try {
            transportes = new GenericDAO<>(getContext(), Transporte.class, "transporte").obtenerTodos();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar transportes", Toast.LENGTH_SHORT).show();
        }
        try {
            personales = new GenericDAO<>(getContext(), PersonalInterno.class, "personal_interno").obtenerTodos();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar personal", Toast.LENGTH_SHORT).show();
        }
        try {
            vehiculos = new GenericDAO<>(getContext(), Vehiculo.class, "vehiculo").obtenerTodos();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar vehículos", Toast.LENGTH_SHORT).show();
        }
        try {
            bodegas = new GenericDAO<>(getContext(), Bodega.class, "bodega").obtenerTodos();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar bodegas", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Transporte> adpTransporte = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, transportes);
        adpTransporte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransporte.setAdapter(adpTransporte);

        ArrayAdapter<PersonalInterno> adpPersonal = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, personales);
        adpPersonal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPersonal.setAdapter(adpPersonal);

        ArrayAdapter<Vehiculo> adpVehiculo = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, vehiculos);
        adpVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehiculo.setAdapter(adpVehiculo);

        ArrayAdapter<Bodega> adpBodega = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, bodegas);
        adpBodega.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBodega.setAdapter(adpBodega);

        List<String> tipos = Arrays.asList("Entrada", "Salida");
        ArrayAdapter<String> adpTipo = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, tipos);
        adpTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adpTipo);

        Bundle args      = getArguments();
        boolean esEdicion = args != null && args.getInt("ID") > 0;
        int id           = esEdicion ? args.getInt("ID") : 0;

        if (esEdicion) {
            etFecha.setText(args.getString("FECHA"));
            etMotivo.setText(args.getString("MOTIVO"));

            int idTransporte = args.getInt("ID_TRANSPORTE");
            for (int i = 0; i < transportes.size(); i++) {
                if (transportes.get(i).getId() == idTransporte) {
                    spinnerTransporte.setSelection(i); break;
                }
            }
            int idPersonal = args.getInt("ID_PERSONAL");
            for (int i = 0; i < personales.size(); i++) {
                if (personales.get(i).getId() == idPersonal) {
                    spinnerPersonal.setSelection(i); break;
                }
            }
            int idVehiculo = args.getInt("ID_VEHICULO");
            for (int i = 0; i < vehiculos.size(); i++) {
                if (vehiculos.get(i).getId() == idVehiculo) {
                    spinnerVehiculo.setSelection(i); break;
                }
            }
            int idBodega = args.getInt("ID_BODEGA");
            for (int i = 0; i < bodegas.size(); i++) {
                if (bodegas.get(i).getId() == idBodega) {
                    spinnerBodega.setSelection(i); break;
                }
            }
            String tipoGuardado = args.getString("TIPO", "Entrada");
            spinnerTipo.setSelection(tipos.indexOf(tipoGuardado) >= 0
                ? tipos.indexOf(tipoGuardado) : 0);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(esEdicion ? "Editar Movimiento" : "Nuevo Movimiento")
            .setView(view)
            .create();

        btnGuardar.setOnClickListener(v -> {
            String fecha  = etFecha.getText().toString().trim();
            String motivo = etMotivo.getText().toString().trim();

            if (fecha.isEmpty()) {
                Toast.makeText(getActivity(), "La fecha es obligatoria", Toast.LENGTH_SHORT).show();
                return;
            }
            if (motivo.isEmpty()) {
                Toast.makeText(getActivity(), "El motivo es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            Transporte    transp = (Transporte)    spinnerTransporte.getSelectedItem();
            PersonalInterno pers = (PersonalInterno) spinnerPersonal.getSelectedItem();
            Vehiculo        veh  = (Vehiculo)      spinnerVehiculo.getSelectedItem();
            Bodega          bod  = (Bodega)        spinnerBodega.getSelectedItem();
            String          tipo = (String)        spinnerTipo.getSelectedItem();

            if (transp == null || pers == null || veh == null || bod == null) {
                Toast.makeText(getActivity(), "Seleccione todos los campos requeridos",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onSave(new Movimiento(
                    id, transp.getId(), pers.getId(), veh.getId(), bod.getId(),
                    tipo, fecha, motivo));
            }
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
