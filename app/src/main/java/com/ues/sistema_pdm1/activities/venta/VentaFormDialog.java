package com.ues.sistema_pdm1.activities.venta;

import android.app.DatePickerDialog;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.models.Venta;
import com.ues.sistema_pdm1.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class VentaFormDialog extends DialogFragment {

    private Venta venta;
    private Consumer<Venta> onSaveListener;

    private Spinner spImportador, spVehiculo;
    private EditText etFecha, etPrecio;
    private Button btnCancelar, btnGuardar;

    private List<Importador> importadores;
    private List<Vehiculo> vehiculos;

    public static VentaFormDialog newInstance(Venta venta) {
        VentaFormDialog fragment = new VentaFormDialog();
        fragment.venta = venta;
        return fragment;
    }

    public void setOnSaveListener(Consumer<Venta> listener) {
        this.onSaveListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_venta_form, null);

        vincularVistas(view);
        cargarSpinners();
        configurarDatePicker();

        if (venta != null) {
            prellenarDatos();
        } else {
            // Poner fecha actual por defecto
            Calendar c = Calendar.getInstance();
            String hoy = String.format("%d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            etFecha.setText(hoy);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(venta == null ? R.string.title_nueva_venta : R.string.title_editar_venta)
                .setView(view)
                .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnGuardar.setOnClickListener(v -> guardar(dialog));

        return dialog;
    }

    private void vincularVistas(View v) {
        spImportador = v.findViewById(R.id.sp_venta_importador);
        spVehiculo = v.findViewById(R.id.sp_venta_vehiculo);
        etFecha = v.findViewById(R.id.et_venta_fecha);
        etPrecio = v.findViewById(R.id.et_venta_precio);
        btnCancelar = v.findViewById(R.id.btn_venta_cancelar);
        btnGuardar = v.findViewById(R.id.btn_venta_guardar);
    }

    private void cargarSpinners() {
        GenericDAO<Importador> impDAO = new GenericDAO<>(getContext(), Importador.class, "importador");
        GenericDAO<Vehiculo> vehDAO = new GenericDAO<>(getContext(), Vehiculo.class, "vehiculo");
        List<Vehiculo> todosVehiculos;

        try {
            importadores = impDAO.obtenerTodos();
            if (importadores.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.error_no_importadores), Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.error_cargar_importadores), Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }


        try {
            todosVehiculos = vehDAO.obtenerTodos();
            if (todosVehiculos.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.error_no_vehiculos), Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.error_cargar_vehiculos), Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        vehiculos = new ArrayList<>();

        // REGLA: Solo mostrar vehículos "listos" o "en bodega"
        for (Vehiculo v : todosVehiculos) {
            String estado = v.getEstadoVehiculo();
            if (estado != null && (
                    Constants.ESTADO_VEHICULO_ALMACENADO.equalsIgnoreCase(estado) ||
                    Constants.ESTADO_VEHICULO_LISTO.equalsIgnoreCase(estado) ||
                    (venta != null && v.getId() == venta.getIdVehiculo()))) {
                vehiculos.add(v);
            }
        }

        ArrayAdapter<Importador> impAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, importadores);
        impAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImportador.setAdapter(impAdapter);

        ArrayAdapter<Vehiculo> vehAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, vehiculos);
        vehAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVehiculo.setAdapter(vehAdapter);

        if (vehiculos.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.error_no_vehiculos_listos), Toast.LENGTH_LONG).show();
        }
    }

    private void configurarDatePicker() {
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String fecha = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                etFecha.setText(fecha);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void prellenarDatos() {
        etFecha.setText(venta.getFechaVenta());
        etPrecio.setText(String.valueOf(venta.getPrecio()));

        for (int i = 0; i < importadores.size(); i++) {
            if (importadores.get(i).getId() == venta.getIdImportador()) {
                spImportador.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getId() == venta.getIdVehiculo()) {
                spVehiculo.setSelection(i);
                break;
            }
        }
    }

    private void guardar(AlertDialog dialog) {
        String fecha = etFecha.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        if (fecha.isEmpty() || precioStr.isEmpty() || spImportador.getSelectedItem() == null || spVehiculo.getSelectedItem() == null) {
            Toast.makeText(getContext(), Constants.MSG_CAMPO_REQUERIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                Toast.makeText(getContext(), getString(R.string.error_precio_minimo), Toast.LENGTH_SHORT).show();
                return;
            }

            Importador imp = (Importador) spImportador.getSelectedItem();
            Vehiculo veh = (Vehiculo) spVehiculo.getSelectedItem();

            if (venta == null) venta = new Venta();
            venta.setIdImportador(imp.getId());
            venta.setIdVehiculo(veh.getId());
            venta.setFechaVenta(fecha);
            venta.setPrecio(precio);

            if (onSaveListener != null) {
                onSaveListener.accept(venta);
            }
            dialog.dismiss();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.error_precio_invalido), Toast.LENGTH_SHORT).show();
        }
    }
}