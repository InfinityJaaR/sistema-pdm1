package com.ues.sistema_pdm1.activities.vehiculo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.ues.sistema_pdm1.models.Bodega;
import com.ues.sistema_pdm1.models.Importacion;
import com.ues.sistema_pdm1.models.Modelo;
import com.ues.sistema_pdm1.models.Seccion;
import com.ues.sistema_pdm1.models.TipoVehiculo;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
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
    private GenericDAO<Bodega> bodegaDAO;
    private GenericDAO<Vehiculo> vehiculoDAO;

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

        if (!esEdicion) {
            etVin.setText("");
            etColor.setText("");
        } else {
            tvTitulo.setText(R.string.modal_titulo_actualizar);
            setDatosEdicion(args);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        view.findViewById(R.id.btn_vehiculo_guardar).setOnClickListener(v -> guardar(id, dialog));
        
        // Botón Cancelar (antes limpiar)
        Button btnCancelar = view.findViewById(R.id.btn_vehiculo_limpiar);
        btnCancelar.setText(R.string.btn_cancelar);
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

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
        etVin.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Constants.LONGITUD_VIN)});
    }

    private void inicializarDAOs() {
        tipoDAO = new GenericDAO<>(getActivity(), TipoVehiculo.class, "tipo_vehiculo");
        seccionDAO = new GenericDAO<>(getActivity(), Seccion.class, "seccion");
        modeloDAO = new GenericDAO<>(getActivity(), Modelo.class, "modelo");
        importacionDAO = new GenericDAO<>(getActivity(), Importacion.class, "importacion");
        bodegaDAO = new GenericDAO<>(getActivity(), Bodega.class, "bodega");
        vehiculoDAO = new GenericDAO<>(getActivity(), Vehiculo.class, "vehiculo");
    }

    private void cargarCatalogos() {
        try {
            setupSpinner(spTipo, tipoDAO.obtenerTodos());
            setupSpinner(spModelo, modeloDAO.obtenerTodos());
            setupSpinner(spImportacion, importacionDAO.obtenerTodos());

            List<Seccion> secciones = seccionDAO.obtenerTodos();
            for (Seccion s : secciones) {
                Bodega b = bodegaDAO.obtenerPorId(s.getIdBodega());
                if (b != null) s.setNombreBodega(b.getNombreBodega());
            }
            setupSpinner(spSeccion, secciones);

            List<String> anios = new ArrayList<>();
            anios.add("Seleccione Año");
            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            int anioMinimo = anioActual - 5;
            for (int i = anioMinimo; i <= anioActual; i++) {
                anios.add(String.valueOf(i));
            }
            ArrayAdapter<String> anioAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_text_black, anios);
            anioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAnio.setAdapter(anioAdapter);

            List<String> estados = new ArrayList<>();
            estados.add("Seleccione Estado");
            estados.add(Constants.ESTADO_VEHICULO_ALMACENADO);
            estados.add(Constants.ESTADO_VEHICULO_EN_REPARACION);
            estados.add(Constants.ESTADO_VEHICULO_LISTO);
            estados.add(Constants.ESTADO_VEHICULO_VENDIDO);
            ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_text_black, estados);
            estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEstado.setAdapter(estadoAdapter);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error catálogos", Toast.LENGTH_SHORT).show();
        }
    }

    private <T> void setupSpinner(Spinner sp, List<T> items) {
        List<Object> listaConPrompt = new ArrayList<>();
        listaConPrompt.add("Seleccione una opción...");
        listaConPrompt.addAll(items);
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_text_black, listaConPrompt);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    private void setDatosEdicion(Bundle args) {
        etVin.setText(args.getString("VIN"));
        etColor.setText(args.getString("COLOR"));
        seleccionarEnSpinner(spTipo, args.getInt("ID_TIPO"));
        seleccionarEnSpinner(spSeccion, args.getInt("ID_SECCION"));
        seleccionarEnSpinner(spModelo, args.getInt("ID_MODELO"));
        seleccionarEnSpinner(spImportacion, args.getInt("ID_IMPORTACION"));

        String anio = String.valueOf(args.getInt("ANIO"));
        for (int i = 0; i < spAnio.getCount(); i++) {
            if (spAnio.getItemAtPosition(i).toString().equals(anio)) { spAnio.setSelection(i); break; }
        }

        String est = args.getString("ESTADO");
        for (int i = 0; i < spEstado.getCount(); i++) {
            if (spEstado.getItemAtPosition(i).toString().equals(est)) { spEstado.setSelection(i); break; }
        }
    }

    private void seleccionarEnSpinner(Spinner sp, int id) {
        for (int i = 0; i < sp.getCount(); i++) {
            Object item = sp.getItemAtPosition(i);
            if (item instanceof String) continue;
            try {
                int itemId = (int) item.getClass().getMethod("getId").invoke(item);
                if (itemId == id) { sp.setSelection(i); break; }
            } catch (Exception ignored) {}
        }
    }

    private boolean vinYaExiste(String vin, int idActual) {
        try {
            List<Vehiculo> lista = vehiculoDAO.obtenerTodos();
            for (Vehiculo v : lista) {
                if (v.getVin().equalsIgnoreCase(vin) && v.getId() != idActual) return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private void guardar(int id, AlertDialog dialog) {
        String vin = etVin.getText().toString().trim().toUpperCase();
        String color = etColor.getText().toString().trim();

        if (vin.isEmpty() || vin.length() != Constants.LONGITUD_VIN) { 
            etVin.setError(Constants.MSG_DATOS_INVALIDOS); 
            return; 
        }
        if (vinYaExiste(vin, id)) { etVin.setError("VIN duplicado"); return; }
        if (color.isEmpty()) { etColor.setError(Constants.MSG_CAMPO_REQUERIDO); return; }
        
        if (spTipo.getSelectedItemPosition() == 0 || spSeccion.getSelectedItemPosition() == 0 ||
                spAnio.getSelectedItemPosition() == 0 || spEstado.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Complete los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            try {
                TipoVehiculo t = (TipoVehiculo) spTipo.getSelectedItem();
                Seccion s = (Seccion) spSeccion.getSelectedItem();
                Modelo m = (Modelo) spModelo.getSelectedItem();
                Importacion imp = (Importacion) spImportacion.getSelectedItem();
                int a = Integer.parseInt(spAnio.getSelectedItem().toString());
                String est = spEstado.getSelectedItem().toString();

                Vehiculo v = new Vehiculo(id, imp.getId(), m.getId(), t.getId(), s.getId(), vin, a, color, est);
                listener.onSave(v);
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
