package com.ues.sistema_pdm1.activities.vehiculo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Seccion;
import com.ues.sistema_pdm1.models.TipoVehiculo;
import com.ues.sistema_pdm1.models.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class VehiculoFormDialog extends DialogFragment {

    public interface OnSaveListener {
        void onSave(Vehiculo vehiculo);
    }

    private OnSaveListener listener;
    private Spinner spTipo, spSeccion, spModelo, spImportacion, spAnio, spEstado;
    private EditText etVin, etColor;
    private TextView tvTitulo;

    private GenericDAO<TipoVehiculo> tipoDAO;
    private GenericDAO<Seccion> seccionDAO;
    private GenericDAO<Modelo> modeloDAO;
    private GenericDAO<Importacion> importacionDAO;

    public static VehiculoFormDialog newInstance(Vehiculo vehiculo) {
        VehiculoFormDialog dialog = new VehiculoFormDialog();
        Bundle args = new Bundle();
        if (vehiculo != null) {
            args.putInt("ID", vehiculo.getId());
            args.putInt("ID_TIPO", vehiculo.getIdTipoVehiculo());
            args.putInt("ID_SECCION", vehiculo.getIdSeccion());
            args.putInt("ID_MODELO", vehiculo.getIdModelo());
            args.putInt("ID_IMPORTACION", vehiculo.getIdImportacion());
            args.putString("VIN", vehiculo.getVin());
            args.putInt("ANIO", vehiculo.getAnio());
            args.putString("COLOR", vehiculo.getColorVehiculo());
            args.putString("ESTADO", vehiculo.getEstadoVehiculo());
        }
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_MinWidth);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_vehiculo, null);
        vincularVistas(view);
        inicializarDAOs();
        cargarCatalogos();

        Bundle args = getArguments();
        boolean esEdicion = args != null && args.getInt("ID") > 0;
        int id = esEdicion ? args.getInt("ID") : 0;

        if (esEdicion) {
            tvTitulo.setText(R.string.modal_titulo_actualizar);
            setDatosEdicion(args);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).setView(view).create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        view.findViewById(R.id.btn_vehiculo_guardar).setOnClickListener(v -> guardar(id, dialog));
        
        Button btnAccion = view.findViewById(R.id.btn_vehiculo_limpiar);
        if (esEdicion) {
            btnAccion.setText(R.string.btn_cancelar);
            btnAccion.setOnClickListener(v -> dialog.dismiss());
        } else {
            btnAccion.setOnClickListener(v -> limpiarCampos());
        }

        return dialog;
    }

    private void vincularVistas(View v) {
        tvTitulo = v.findViewById(R.id.tv_modal_titulo);
        spTipo = v.findViewById(R.id.sp_vehiculo_tipo);
        spSeccion = v.findViewById(R.id.sp_vehiculo_seccion);
        spModelo = v.findViewById(R.id.sp_vehiculo_modelo);
        spImportacion = v.findViewById(R.id.sp_vehiculo_importacion);
        spAnio = v.findViewById(R.id.sp_vehiculo_anio);
        spEstado = v.findViewById(R.id.sp_vehiculo_estado);
        etVin = v.findViewById(R.id.et_vehiculo_vin);
        etColor = v.findViewById(R.id.et_vehiculo_color);
    }

    private void inicializarDAOs() {
        tipoDAO = new GenericDAO<>(getActivity(), TipoVehiculo.class, "tipo_vehiculo");
        seccionDAO = new GenericDAO<>(getActivity(), Seccion.class, "seccion");
        modeloDAO = new GenericDAO<>(getActivity(), Modelo.class, "modelo");
        importacionDAO = new GenericDAO<>(getActivity(), Importacion.class, "importacion");
    }

    private void cargarCatalogos() {
        try {
            setupSpinner(spTipo, tipoDAO.obtenerTodos());
            setupSpinner(spSeccion, seccionDAO.obtenerTodos());
            setupSpinner(spModelo, modeloDAO.obtenerTodos());
            setupSpinner(spImportacion, importacionDAO.obtenerTodos());

            List<Integer> anios = new ArrayList<>();
            for (int i = 2021; i <= 2026; i++) anios.add(i);
            ArrayAdapter<Integer> anioAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_text_black, anios);
            anioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAnio.setAdapter(anioAdapter);

            String[] estados = {"en bodega", "en reparacion", "listo para venta", "vendido"};
            ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_text_black, estados);
            estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEstado.setAdapter(estadoAdapter);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error catálogos", Toast.LENGTH_SHORT).show();
        }
    }

    private <T> void setupSpinner(Spinner sp, List<T> items) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_text_black, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    private void setDatosEdicion(Bundle args) {
        etVin.setText(args.getString("VIN"));
        etVin.setEnabled(false);
        etVin.setTextColor(Color.BLACK);

        spModelo.setEnabled(false);
        spAnio.setEnabled(false);
        spTipo.setEnabled(false);
        spImportacion.setEnabled(false);

        etColor.setText(args.getString("COLOR"));
        seleccionarEnSpinner(spTipo, args.getInt("ID_TIPO"));
        seleccionarEnSpinner(spSeccion, args.getInt("ID_SECCION"));
        seleccionarEnSpinner(spModelo, args.getInt("ID_MODELO"));
        seleccionarEnSpinner(spImportacion, args.getInt("ID_IMPORTACION"));

        int anio = args.getInt("ANIO");
        for (int i = 0; i < spAnio.getCount(); i++) {
            if (spAnio.getItemAtPosition(i).equals(anio)) { spAnio.setSelection(i); break; }
        }
        String est = args.getString("ESTADO");
        for (int i = 0; i < spEstado.getCount(); i++) {
            if (spEstado.getItemAtPosition(i).toString().equals(est)) { spEstado.setSelection(i); break; }
        }
    }

    private void seleccionarEnSpinner(Spinner sp, int id) {
        for (int i = 0; i < sp.getCount(); i++) {
            Object item = sp.getItemAtPosition(i);
            try {
                int itemId = (int) item.getClass().getMethod("getId").invoke(item);
                if (itemId == id) { sp.setSelection(i); break; }
            } catch (Exception ignored) {}
        }
    }

    private void guardar(int id, AlertDialog dialog) {
        String vin = etVin.getText().toString().trim().toUpperCase();
        String color = etColor.getText().toString().trim();

        if (vin.isEmpty()) { etVin.setError("Requerido"); return; }
        if (vin.length() < 5) { etVin.setError("VIN demasiado corto"); return; }
        if (color.isEmpty()) { etColor.setError("Especifique color"); return; }

        if (listener != null) {
            TipoVehiculo t = (TipoVehiculo) spTipo.getSelectedItem();
            Seccion s = (Seccion) spSeccion.getSelectedItem();
            Modelo m = (Modelo) spModelo.getSelectedItem();
            Importacion imp = (Importacion) spImportacion.getSelectedItem();
            int a = (Integer) spAnio.getSelectedItem();
            String est = spEstado.getSelectedItem().toString();
            Vehiculo v = new Vehiculo(id, imp.getId(), m.getId(), t.getId(), s.getId(), vin, a, color, est);
            listener.onSave(v);
        }
        dialog.dismiss();
    }

    private void limpiarCampos() {
        etVin.setText(""); etColor.setText("");
        spTipo.setSelection(0); spSeccion.setSelection(0); spModelo.setSelection(0);
        spImportacion.setSelection(0); spAnio.setSelection(0); spEstado.setSelection(0);
    }
}
