package com.ues.sistema_pdm1.activities.bodega;

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
import com.ues.sistema_pdm1.models.Distrito;

public class BodegaViewDialog extends DialogFragment {

    public static BodegaViewDialog newInstance(Bodega bodega) {
        BodegaViewDialog dialog = new BodegaViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID_BODEGA", bodega.getId());
        args.putInt("ID_DISTRITO", bodega.getIdDistrito());
        args.putString("NOMBRE_BODEGA", bodega.getNombreBodega());
        args.putString("DIRECCION_BODEGA", bodega.getDireccionBodega());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bodega_view, null);

        TextView tvId = view.findViewById(R.id.tv_id_bodega);
        TextView tvDistrito = view.findViewById(R.id.tv_distrito_bodega);
        TextView tvNombre = view.findViewById(R.id.tv_nombre_bodega);
        TextView tvDireccion = view.findViewById(R.id.tv_direccion_bodega);
        Button btnCerrar = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID_BODEGA"));
        tvNombre.setText(args.getString("NOMBRE_BODEGA"));
        tvDireccion.setText(args.getString("DIRECCION_BODEGA"));

        try {
            GenericDAO<Distrito> dao = new GenericDAO<>(requireContext(), Distrito.class, "DISTRITO");
            Distrito d = dao.obtenerPorId(args.getInt("ID_DISTRITO"));
            tvDistrito.setText(d != null ? d.getNombreDistrito() : "N/A");
        } catch (Exception e) {
            tvDistrito.setText("Error");
        }

        // Acción para cerrar el diálogo
        btnCerrar.setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(requireActivity()).setTitle("Detalle").setView(view).create();
    }
}