package com.ues.sistema_pdm1.activities.transporte;

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
import com.ues.sistema_pdm1.models.TipoTransporte;
import com.ues.sistema_pdm1.models.Transporte;

public class TransporteViewDialog extends DialogFragment {

    public static TransporteViewDialog newInstance(Transporte t) {
        TransporteViewDialog dialog = new TransporteViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID", t.getId());
        args.putInt("ID_TIPO", t.getIdTipoTransporte());
        args.putString("PLACA", t.getPlaca());
        args.putString("DESCRIPCION", t.getDescripcionTransporte());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_transporte_view, null);

        TextView tvId    = view.findViewById(R.id.tv_transporte_id);
        TextView tvTipo  = view.findViewById(R.id.tv_transporte_tipo);
        TextView tvPlaca = view.findViewById(R.id.tv_transporte_placa);
        TextView tvDesc  = view.findViewById(R.id.tv_transporte_descripcion);
        Button btnCerrar = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID"));
        tvPlaca.setText(args.getString("PLACA"));
        tvDesc.setText(args.getString("DESCRIPCION") != null ? args.getString("DESCRIPCION") : "—");

        try {
            GenericDAO<TipoTransporte> tipoDAO =
                new GenericDAO<>(getContext(), TipoTransporte.class, "tipo_transporte");
            TipoTransporte tipo = tipoDAO.obtenerPorId(args.getInt("ID_TIPO"));
            tvTipo.setText(tipo != null ? tipo.getDescripcionTipoTransporte() : "—");
        } catch (Exception e) {
            tvTipo.setText("—");
        }

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle("Detalle de Transporte")
            .setView(view)
            .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}
