package com.ues.sistema_pdm1.activities.reparacion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Reparacion;
import com.ues.sistema_pdm1.models.Taller;
import com.ues.sistema_pdm1.models.Vehiculo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReparacionFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Reparacion reparacion);
    }

    private OnSaveListener listener;
    private List<Vehiculo> vehiculos = new ArrayList<>();
    private List<Taller>   talleres  = new ArrayList<>();

    private Vehiculo vehiculoSeleccionado = null;
    private Taller   tallerSeleccionado   = null;

    public static ReparacionFormDialog newInstance(Reparacion reparacion) {
        ReparacionFormDialog dialog = new ReparacionFormDialog();
        Bundle args = new Bundle();
        if (reparacion != null) {
            args.putInt("ID_REPARACION",            reparacion.getId());
            args.putInt("ID_VEHICULO",              reparacion.getIdVehiculo());
            args.putInt("ID_TALLER",                reparacion.getIdTaller());
            args.putString("FECHA_INICIO",          reparacion.getFechaInicio());
            args.putString("FECHA_FIN",             reparacion.getFechaFin());
            args.putString("DESCRIPCION_TRABAJO",   reparacion.getDescripcionTrabajo());
            args.putInt("APTO_PARA_VENTA",          reparacion.getAptoParaVenta());
            args.putInt("REQUIERE_OTRA_REPARACION", reparacion.getRequiereOtraReparacion());
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
                .inflate(R.layout.dialog_reparacion_form, null);

        AutoCompleteTextView actvVehiculo    = view.findViewById(R.id.actv_vehiculo);
        AutoCompleteTextView actvTaller      = view.findViewById(R.id.actv_taller);
        Button               btnFechaInicio  = view.findViewById(R.id.btn_fecha_inicio);
        Button               btnFechaFin     = view.findViewById(R.id.btn_fecha_fin);
        EditText             etDescripcion   = view.findViewById(R.id.et_descripcion_trabajo);
        CheckBox             cbAptoParaVenta = view.findViewById(R.id.cb_apto_para_venta);
        CheckBox             cbRequiere      = view.findViewById(R.id.cb_requiere_otra_reparacion);
        Button               btnGuardar      = view.findViewById(R.id.btn_guardar);
        Button               btnCancelar     = view.findViewById(R.id.btn_cancelar);

        Bundle args       = getArguments();
        boolean esEdicion = args != null && args.getInt("ID_REPARACION") > 0;
        int id            = esEdicion ? args.getInt("ID_REPARACION") : 0;

        final String[] fechaInicioHolder = {""};
        final String[] fechaFinHolder    = {""};

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        btnFechaInicio.setOnClickListener(v ->
                new DatePickerDialog(requireContext(),
                        (dp, year, month, day) -> {
                            String fecha = String.format("%04d-%02d-%02d", year, month + 1, day);
                            fechaInicioHolder[0] = fecha;
                            btnFechaInicio.setText(fecha);
                            if (!fechaFinHolder[0].isEmpty() && fechaFinHolder[0].compareTo(fecha) < 0) {
                                fechaFinHolder[0] = "";
                                btnFechaFin.setText(getString(R.string.btn_seleccionar));
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show()
        );

        btnFechaFin.setOnClickListener(v -> {
            DatePickerDialog dpFin = new DatePickerDialog(requireContext(),
                    (dp, year, month, day) -> {
                        String fecha = String.format("%04d-%02d-%02d", year, month + 1, day);
                        fechaFinHolder[0] = fecha;
                        btnFechaFin.setText(fecha);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            if (!fechaInicioHolder[0].isEmpty()) {
                try {
                    Date fechaMin = sdf.parse(fechaInicioHolder[0]);
                    if (fechaMin != null)
                        dpFin.getDatePicker().setMinDate(fechaMin.getTime());
                } catch (Exception ignored) {}
            }
            dpFin.show();
        });

        try {
            GenericDAO<Vehiculo> vehiculoDAO = new GenericDAO<>(getContext(), Vehiculo.class, "vehiculo");
            GenericDAO<Taller>   tallerDAO   = new GenericDAO<>(getContext(), Taller.class, "taller");

            List<Vehiculo> todos = vehiculoDAO.obtenerTodos();
            vehiculos = new ArrayList<>();
            for (Vehiculo v : todos) {
                String estado = v.getEstadoVehiculo();
                if ("en reparacion".equals(estado) || "en bodega".equals(estado))
                    vehiculos.add(v);
            }

            List<Taller> todosTalleres = tallerDAO.obtenerTodos();
            talleres = new ArrayList<>();
            for (Taller t : todosTalleres) {
                if (t.getAutorizado() == 1)
                    talleres.add(t);
            }

            ArrayAdapter<Vehiculo> adapterVehiculo = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_dropdown_item_1line, vehiculos);
            actvVehiculo.setAdapter(adapterVehiculo);

            ArrayAdapter<Taller> adapterTaller = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_dropdown_item_1line, talleres);
            actvTaller.setAdapter(adapterTaller);

            actvVehiculo.setOnItemClickListener((parent, v, position, id2) ->
                    vehiculoSeleccionado = (Vehiculo) parent.getItemAtPosition(position));

            actvTaller.setOnItemClickListener((parent, v, position, id2) ->
                    tallerSeleccionado = (Taller) parent.getItemAtPosition(position));

            if (esEdicion) {
                int idVehiculo = args.getInt("ID_VEHICULO");
                int idTaller   = args.getInt("ID_TALLER");

                for (Vehiculo v : todos) {
                    if (v.getId() == idVehiculo) {
                        vehiculoSeleccionado = v;
                        actvVehiculo.setText(v.toString(), false);
                        break;
                    }
                }
                for (Taller t : talleres) {
                    if (t.getId() == idTaller) {
                        tallerSeleccionado = t;
                        actvTaller.setText(t.toString(), false);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.error_cargar_reparaciones), Toast.LENGTH_SHORT).show();
        }

        if (esEdicion) {
            fechaInicioHolder[0] = args.getString("FECHA_INICIO");
            fechaFinHolder[0]    = args.getString("FECHA_FIN");
            btnFechaInicio.setText(fechaInicioHolder[0]);
            btnFechaFin.setText(fechaFinHolder[0]);
            etDescripcion.setText(args.getString("DESCRIPCION_TRABAJO"));
            cbAptoParaVenta.setChecked(args.getInt("APTO_PARA_VENTA") == 1);
            cbRequiere.setChecked(args.getInt("REQUIERE_OTRA_REPARACION") == 1);
            cbAptoParaVenta.setEnabled(true);
            cbRequiere.setEnabled(true);
        } else {
            cbAptoParaVenta.setChecked(false);
            cbRequiere.setChecked(false);
            cbAptoParaVenta.setEnabled(false);
            cbRequiere.setEnabled(false);
            cbAptoParaVenta.setVisibility(View.GONE);
            cbRequiere.setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(esEdicion
                        ? getString(R.string.title_editar_reparacion)
                        : getString(R.string.title_nueva_reparacion))
                .setView(view)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String fechaInicio     = fechaInicioHolder[0];
            String fechaFin        = fechaFinHolder[0];
            String descripcion     = etDescripcion.getText().toString().trim();
            int aptoParaVenta      = cbAptoParaVenta.isChecked() ? 1 : 0;
            int requiereReparacion = cbRequiere.isChecked() ? 1 : 0;

            if (vehiculoSeleccionado == null) {
                Toast.makeText(getActivity(), getString(R.string.error_seleccione_vehiculo), Toast.LENGTH_SHORT).show();
                return;
            }
            if (tallerSeleccionado == null) {
                Toast.makeText(getActivity(), getString(R.string.error_seleccione_taller), Toast.LENGTH_SHORT).show();
                return;
            }
            if (fechaInicio.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.error_seleccione_fecha_inicio), Toast.LENGTH_SHORT).show();
                return;
            }
            if (descripcion.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.error_descripcion_trabajo), Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null)
                listener.onSave(new Reparacion(
                        id,
                        tallerSeleccionado.getId(),
                        vehiculoSeleccionado.getId(),
                        fechaInicio, fechaFin,
                        descripcion,
                        aptoParaVenta,
                        requiereReparacion
                ));
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}