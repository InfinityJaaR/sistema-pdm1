package com.ues.sistema_pdm1.activities.taller;

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
import com.ues.sistema_pdm1.models.Taller;

public class TallerViewDialog extends DialogFragment {

    public static TallerViewDialog newInstance(Taller taller) {
        TallerViewDialog dialog = new TallerViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID_TALLER",          taller.getId());
        args.putString("NOMBRE_TALLER",   taller.getNombreTaller());
        args.putString("DIRECCION_TALLER",taller.getDireccionTaller());
        args.putString("TELEFONO_TALLER", taller.getTelefonoTaller());
        args.putInt("AUTORIZADO",         taller.getAutorizado());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_taller_view, null);

        TextView tvId         = view.findViewById(R.id.tv_id_taller);
        TextView tvNombre     = view.findViewById(R.id.tv_nombre_taller);
        TextView tvDireccion  = view.findViewById(R.id.tv_direccion_taller);
        TextView tvTelefono   = view.findViewById(R.id.tv_telefono_taller);
        TextView tvAutorizado = view.findViewById(R.id.tv_autorizado_taller);
        Button   btnCerrar    = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID_TALLER"));
        tvNombre.setText(args.getString("NOMBRE_TALLER"));
        tvDireccion.setText(args.getString("DIRECCION_TALLER"));
        tvTelefono.setText(args.getString("TELEFONO_TALLER"));
        tvAutorizado.setText(args.getInt("AUTORIZADO") == 1
                ? getString(R.string.label_autorizado)
                : getString(R.string.label_no_autorizado));

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.title_detalle_taller))
                .setView(view)
                .create();

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}