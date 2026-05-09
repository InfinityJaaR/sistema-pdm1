package com.ues.sistema_pdm1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.models.MenuItem;

import java.util.List;

public class MenuItemAdapter extends ArrayAdapter<MenuItem> {

    private final Context context;
    private final List<MenuItem> items;

    public MenuItemAdapter(Context context, List<MenuItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItem item = items.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.item_menu, parent, false);
        }

        TextView tvNombre      = convertView.findViewById(R.id.tv_nombre_opcion);
        TextView tvDescripcion = convertView.findViewById(R.id.tv_descripcion_opcion);

        tvNombre.setText(item.getNombre());
        tvDescripcion.setText(item.getDescripcion());

        return convertView;
    }
}