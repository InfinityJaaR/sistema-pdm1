package com.ues.sistema_pdm1.activities.movimiento;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class MovimientoViewDialog extends DialogFragment {

    public static MovimientoViewDialog newInstance(Movimiento m) {
        MovimientoViewDialog dialog = new MovimientoViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID",            m.getId());
        args.putInt("ID_TRANSPORTE", m.getIdTransporte());
        args.putInt("ID_PERSONAL",   m.getIdPersonal());
        args.putInt("ID_VEHICULO",   m.getIdVehiculo());
        args.putInt("ID_BODEGA",     m.getIdBodega());
        args.putString("TIPO",       m.getTipoMovimiento());
        args.putString("FECHA",      m.getFechaMovimiento());
        args.putString("MOTIVO",     m.getMotivo());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_movimiento_view, null);

        TextView tvId          = view.findViewById(R.id.tv_movimiento_id);
        TextView tvTipo        = view.findViewById(R.id.tv_movimiento_tipo);
        TextView tvFecha       = view.findViewById(R.id.tv_movimiento_fecha);
        TextView tvTransporte  = view.findViewById(R.id.tv_movimiento_transporte);
        TextView tvPersonal    = view.findViewById(R.id.tv_movimiento_personal);
        TextView tvVehiculo    = view.findViewById(R.id.tv_movimiento_vehiculo);
        TextView tvBodega      = view.findViewById(R.id.tv_movimiento_bodega);
        TextView tvMotivo      = view.findViewById(R.id.tv_movimiento_motivo);
        Button   btnCerrar     = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID"));
        tvTipo.setText(args.getString("TIPO"));
        tvFecha.setText(args.getString("FECHA"));
        tvMotivo.setText(args.getString("MOTIVO") != null ? args.getString("MOTIVO") : "—");

        try {
            GenericDAO<Transporte> transporteDAO =
                new GenericDAO<>(getContext(), Transporte.class, "transporte");
            Transporte t = transporteDAO.obtenerPorId(args.getInt("ID_TRANSPORTE"));
            tvTransporte.setText(t != null ? t.getPlaca() : "—");
        } catch (Exception e) {
            tvTransporte.setText("—");
        }

        try {
            GenericDAO<PersonalInterno> personalDAO =
                new GenericDAO<>(getContext(), PersonalInterno.class, "personal_interno");
            PersonalInterno p = personalDAO.obtenerPorId(args.getInt("ID_PERSONAL"));
            tvPersonal.setText(p != null ? p.getNombrePersonal() + " " + p.getApellidoPersonal() : "—");
        } catch (Exception e) {
            tvPersonal.setText("—");
        }

        try {
            GenericDAO<Vehiculo> vehiculoDAO =
                new GenericDAO<>(getContext(), Vehiculo.class, "vehiculo");
            Vehiculo v = vehiculoDAO.obtenerPorId(args.getInt("ID_VEHICULO"));
            tvVehiculo.setText(v != null ? v.getVin() : "—");
        } catch (Exception e) {
            tvVehiculo.setText("—");
        }

        try {
            GenericDAO<Bodega> bodegaDAO =
                new GenericDAO<>(getContext(), Bodega.class, "bodega");
            Bodega b = bodegaDAO.obtenerPorId(args.getInt("ID_BODEGA"));
            tvBodega.setText(b != null ? b.getNombreBodega() : "—");
        } catch (Exception e) {
            tvBodega.setText("—");
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle("Detalle de Movimiento")
            .setView(view)
            .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
