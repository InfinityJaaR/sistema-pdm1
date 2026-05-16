package com.ues.sistema_pdm1.activities.venta;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.Importador;
import com.ues.sistema_pdm1.models.Vehiculo;
import com.ues.sistema_pdm1.models.Venta;

public class VentaViewDialog extends DialogFragment {

    private Venta venta;

    public static VentaViewDialog newInstance(Venta venta) {
        VentaViewDialog fragment = new VentaViewDialog();
        fragment.venta = venta;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_venta_view, null);

        TextView tvImportador = view.findViewById(R.id.tv_view_venta_importador);
        TextView tvVehiculo = view.findViewById(R.id.tv_view_venta_vehiculo);
        TextView tvFecha = view.findViewById(R.id.tv_view_venta_fecha);
        TextView tvPrecio = view.findViewById(R.id.tv_view_venta_precio);
        Button btnCerrar = view.findViewById(R.id.btn_venta_view_cerrar);

        if (venta != null) {
            Importador imp = null;
            Vehiculo veh = null;

            // Cargar nombres descriptivos
            GenericDAO<Importador> impDAO = new GenericDAO<>(getContext(), Importador.class, "importador");
            GenericDAO<Vehiculo> vehDAO = new GenericDAO<>(getContext(), Vehiculo.class, "vehiculo");

            try {
                imp = impDAO.obtenerPorId(venta.getIdImportador());
                veh = vehDAO.obtenerPorId(venta.getIdVehiculo());
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvImportador.setText(imp != null ? imp.toString() : "ID: " + venta.getIdImportador());
            tvVehiculo.setText(veh != null ? veh.toString() : "ID: " + venta.getIdVehiculo());
            tvFecha.setText(venta.getFechaVenta());
            tvPrecio.setText(String.format("$%.2f", venta.getPrecio()));
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Detalle de Venta #" + (venta != null ? venta.getId() : ""))
                .setView(view)
                .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
