package com.ues.sistema_pdm1.activities.seccion;

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
import com.ues.sistema_pdm1.models.Seccion;

public class SeccionViewDialog extends DialogFragment {

    public static SeccionViewDialog newInstance(Seccion seccion) {
        SeccionViewDialog dialog = new SeccionViewDialog();
        Bundle args = new Bundle();
        args.putInt("ID_SECCION", seccion.getId());
        args.putInt("ID_BODEGA", seccion.getIdBodega());
        args.putInt("NIVEL", seccion.getNivel());
        args.putInt("CAPACIDAD_MAXIMA", seccion.getCapacidadMaxima());
        args.putInt("CAPACIDAD_ACTUAL", seccion.getCapacidadActual());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_seccion_view, null);

        TextView tvId = view.findViewById(R.id.tv_id_seccion);
        TextView tvBodega = view.findViewById(R.id.tv_bodega_seccion);
        TextView tvNivel = view.findViewById(R.id.tv_nivel_seccion);
        TextView tvCapacidad = view.findViewById(R.id.tv_capacidad_seccion);
        Button btnCerrar = view.findViewById(R.id.btn_cerrar);

        Bundle args = requireArguments();
        tvId.setText("ID: " + args.getInt("ID_SECCION"));
        tvNivel.setText("Nivel " + args.getInt("NIVEL"));
        tvCapacidad.setText(args.getInt("CAPACIDAD_ACTUAL") + " / " + args.getInt("CAPACIDAD_MAXIMA") + " vehículos");

        try {
            GenericDAO<Bodega> dao = new GenericDAO<>(requireContext(), Bodega.class, "BODEGA");
            // Se corrigió el espacio en el nombre de la llave "ID_BODEGA"
            Bodega b = dao.obtenerPorId(args.getInt("ID_BODEGA"));
            tvBodega.setText(b != null ? b.getNombreBodega() : "N/A");
        } catch (Exception e) {
            tvBodega.setText("Error al cargar bodega");
        }

        // Acción para cerrar el diálogo
        btnCerrar.setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(requireActivity()).setTitle("Detalle de Sección").setView(view).create();
    }
}
