package com.ues.sistema_pdm1.activities.detalleDesperfecto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.ues.sistema_pdm1.R;
import com.ues.sistema_pdm1.data.dao.GenericDAO;
import com.ues.sistema_pdm1.models.DetalleDesperfecto;
import com.ues.sistema_pdm1.models.FotoDesperfecto;
import com.ues.sistema_pdm1.models.TipoDesperfecto;
import com.ues.sistema_pdm1.models.Vehiculo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetalleDesperfectoFormDialog extends DialogFragment {

    private Spinner spVehiculo, spTipo;
    private EditText etDescription;
    private LinearLayout llFotos;
    private Button btnGuardar, btnFoto;
    private TextView tvTitulo;

    private GenericDAO<DetalleDesperfecto> detalleDAO;
    private GenericDAO<Vehiculo> vehiculoDAO;
    private GenericDAO<TipoDesperfecto> tipoDAO;
    private GenericDAO<FotoDesperfecto> fotoDAO;

    private DetalleDesperfecto detalleEdicion;
    private boolean esSoloLectura = false;
    private List<FotoDesperfecto> listaFotos = new ArrayList<>();
    private ArrayList<String> rutasTemporales = new ArrayList<>();

    public interface OnDetalleGuardadoListener {
        void onGuardado();
    }

    private OnDetalleGuardadoListener listener;

    public void setOnDetalleGuardadoListener(OnDetalleGuardadoListener listener) {
        this.listener = listener;
    }

    private ActivityResultLauncher<Void> launcherCamara;
    private ActivityResultLauncher<String> launcherGaleria;
    private ActivityResultLauncher<String[]> launcherPermisos;

    public static DetalleDesperfectoFormDialog newInstance() {
        return new DetalleDesperfectoFormDialog();
    }

    public static DetalleDesperfectoFormDialog newInstance(DetalleDesperfecto d, boolean soloLectura) {
        DetalleDesperfectoFormDialog fragment = new DetalleDesperfectoFormDialog();
        Bundle args = new Bundle();
        args.putSerializable("detalle", d);
        args.putBoolean("soloLectura", soloLectura);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_MinWidth);

        if (getArguments() != null) {
            detalleEdicion = (DetalleDesperfecto) getArguments().getSerializable("detalle");
            esSoloLectura = getArguments().getBoolean("soloLectura");
        }

        detalleDAO = new GenericDAO<>(requireContext(), DetalleDesperfecto.class, "detalle_desperfecto");
        vehiculoDAO = new GenericDAO<>(requireContext(), Vehiculo.class, "vehiculo");
        tipoDAO = new GenericDAO<>(requireContext(), TipoDesperfecto.class, "tipo_desperfecto");
        fotoDAO = new GenericDAO<>(requireContext(), FotoDesperfecto.class, "foto_desperfecto");

        configurarLaunchers();

        if (savedInstanceState != null) {
            rutasTemporales = savedInstanceState.getStringArrayList("rutas_guardadas");
            if (rutasTemporales != null && detalleEdicion == null) {
                listaFotos.clear();
                for (String ruta : rutasTemporales) {
                    String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    listaFotos.add(new FotoDesperfecto(0, 0, ruta, fecha));
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("rutas_guardadas", rutasTemporales);
    }

    private void configurarLaunchers() {
        launcherCamara = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap != null) procesarImagenCapturada(bitmap);
        });

        launcherGaleria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) procesarImagenDeGaleria(uri);
        });

        launcherPermisos = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (Boolean.TRUE.equals(result.get(Manifest.permission.CAMERA))) {
                launcherCamara.launch(null);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detalle_desperfecto_form, container, false);

        tvTitulo = view.findViewById(R.id.tv_form_titulo);
        spVehiculo = view.findViewById(R.id.sp_form_vehiculo);
        spTipo = view.findViewById(R.id.sp_form_tipo);
        etDescription = view.findViewById(R.id.et_form_descripcion);
        llFotos = view.findViewById(R.id.ll_form_fotos);
        btnGuardar = view.findViewById(R.id.btn_form_guardar);
        btnFoto = view.findViewById(R.id.btn_form_agregar_foto);
        view.findViewById(R.id.btn_form_cancelar).setOnClickListener(v -> dismiss());

        cargarSpinners();

        if (detalleEdicion != null) {
            cargarFotosDesdeBD();
        } else {
            actualizarListaFotosEnUI();
        }

        btnGuardar.setOnClickListener(v -> guardarDatos());
        btnFoto.setOnClickListener(v -> abrirSelectorFuente());

        if (esSoloLectura) {
            bloquearParaVisualizacion();
        } else if (detalleEdicion != null) {
            prepararParaEdicion();
        }

        return view;
    }

    private void cargarSpinners() {
        try {
            List<Vehiculo> vehiculos = vehiculoDAO.obtenerTodos();
            ArrayAdapter<Vehiculo> vAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_text, vehiculos);
            vAdapter.setDropDownViewResource(R.layout.spinner_item_text);
            spVehiculo.setAdapter(vAdapter);

            List<TipoDesperfecto> tipos = tipoDAO.obtenerTodos();
            ArrayAdapter<TipoDesperfecto> tAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_text, tipos);
            tAdapter.setDropDownViewResource(R.layout.spinner_item_text);
            spTipo.setAdapter(tAdapter);
        } catch (SQLException e) {
            Log.e("FormDialog", "Error spinners: " + e.getMessage());
        }
    }

    private void seleccionarEnSpinner(Spinner spinner, int idBuscado) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Object item = spinner.getItemAtPosition(i);
            if ((item instanceof Vehiculo && ((Vehiculo) item).getId() == idBuscado) ||
                    (item instanceof TipoDesperfecto && ((TipoDesperfecto) item).getId() == idBuscado)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void abrirSelectorFuente() {
        if (listaFotos.size() >= 5) {
            Toast.makeText(requireContext(), "Máximo 5 fotos permitidas", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] opciones = {"Cámara", "Galería"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Agregar Evidencia")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            launcherCamara.launch(null);
                        } else {
                            launcherPermisos.launch(new String[]{Manifest.permission.CAMERA});
                        }
                    } else {
                        launcherGaleria.launch("image/*");
                    }
                }).show();
    }

    private void procesarImagenCapturada(Bitmap bitmap) {
        File archivo = guardarBitmapEnDisco(bitmap);
        if (archivo != null) agregarFotoALista(archivo.getAbsolutePath());
    }

    private void procesarImagenDeGaleria(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
            procesarImagenCapturada(bitmap);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error al leer imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private File guardarBitmapEnDisco(Bitmap bitmap) {
        File carpeta = new File(requireContext().getFilesDir(), "fotos");
        if (!carpeta.exists()) carpeta.mkdirs();
        String nombre = "img_" + System.currentTimeMillis() + ".jpg";
        File archivo = new File(carpeta, nombre);
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            return archivo;
        } catch (IOException e) {
            return null;
        }
    }

    private void agregarFotoALista(String ruta) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        FotoDesperfecto foto = new FotoDesperfecto(0, (detalleEdicion != null ? detalleEdicion.getId() : 0), ruta, fecha);

        if (detalleEdicion == null) {
            rutasTemporales.add(ruta);
            listaFotos.add(foto);
            Toast.makeText(requireContext(), "Foto añadida", Toast.LENGTH_SHORT).show();
        } else {
            try {
                fotoDAO.insertar(foto);
                listaFotos.add(foto);
                Toast.makeText(requireContext(), "Foto guardada", Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                Toast.makeText(getContext(), "Error al guardar foto", Toast.LENGTH_SHORT).show();
            }
        }
        actualizarListaFotosEnUI();
    }

    private void cargarFotosDesdeBD() {
        try {
            listaFotos = fotoDAO.obtenerPor("ID_DETALLE_DESPERFECTO", String.valueOf(detalleEdicion.getId()));
        } catch (Exception e) {
            listaFotos = new ArrayList<>();
        }
        actualizarListaFotosEnUI();
    }

    private void actualizarListaFotosEnUI() {
        llFotos.removeAllViews();
        for (int i = 0; i < listaFotos.size(); i++) {
            final int pos = i;
            FotoDesperfecto f = listaFotos.get(i);
            View itemView = getLayoutInflater().inflate(R.layout.item_foto_desperfecto, llFotos, false);

            ImageView iv = itemView.findViewById(R.id.iv_item_foto);
            TextView tvN = itemView.findViewById(R.id.tv_item_foto_nombre);
            TextView tvF = itemView.findViewById(R.id.tv_item_foto_fecha);

            Bitmap bitmap = BitmapFactory.decodeFile(f.getRutaImagen());
            if (bitmap != null) iv.setImageBitmap(bitmap);

            tvN.setText("Foto #" + (i + 1));
            tvF.setText(f.getFechaToma());

            // Al hacer click, si es solo lectura ve la foto, si no, pregunta para eliminar directamente
            itemView.setOnClickListener(v -> {
                if (esSoloLectura) {
                    verFotoGrande(pos);
                } else {
                    abrirConfirmacionEliminarFoto(pos);
                }
            });

            llFotos.addView(itemView);
        }
    }

    private void abrirConfirmacionEliminarFoto(int position) {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);
        TextView tvMsg = view.findViewById(R.id.confirmacion_text);
        tvMsg.setText("¿Desea eliminar esta foto?");
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();
        view.findViewById(R.id.btn_no).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_si).setOnClickListener(v -> {
            eliminarFoto(position);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void eliminarFoto(int position) {
        FotoDesperfecto f = listaFotos.get(position);
        new File(f.getRutaImagen()).delete();
        if (detalleEdicion != null) {
            try { fotoDAO.eliminar(f.getId()); } catch (SQLException ignored) {}
        } else {
            rutasTemporales.remove(f.getRutaImagen());
        }
        listaFotos.remove(position);
        actualizarListaFotosEnUI();
        Toast.makeText(requireContext(), "Foto eliminada", Toast.LENGTH_SHORT).show();
    }

    private void verFotoGrande(int position) {
        FotoDesperfecto f = listaFotos.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(f.getRutaImagen());
        if (bitmap == null) {
            Toast.makeText(getContext(), "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageBitmap(bitmap);
        imageView.setAdjustViewBounds(true);
        new AlertDialog.Builder(requireContext())
                .setTitle("Evidencia - " + f.getFechaToma())
                .setView(imageView)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void guardarDatos() {
        String descripcion = etDescription.getText().toString().trim();
        if (descripcion.isEmpty()) {
            etDescription.setError("Requerido");
            return;
        }
        if (listaFotos.isEmpty()) {
            Toast.makeText(getContext(), "Agregue al menos una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        Vehiculo v = (Vehiculo) spVehiculo.getSelectedItem();
        TipoDesperfecto t = (TipoDesperfecto) spTipo.getSelectedItem();

        try {
            if (detalleEdicion == null) {
                String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                DetalleDesperfecto nuevo = new DetalleDesperfecto(0, v.getId(), t.getId(), descripcion, fecha);
                long idNuevo = detalleDAO.insertar(nuevo);
                for (String ruta : rutasTemporales) {
                    fotoDAO.insertar(new FotoDesperfecto(0, (int)idNuevo, ruta, fecha));
                }
                Toast.makeText(requireContext(), "Registrado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                detalleEdicion.setDescripcionDetalle(descripcion);
                detalleEdicion.setIdTipoDesperfecto(t.getId());
                detalleDAO.actualizar(detalleEdicion);
                Toast.makeText(requireContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show();
            }
            if (listener != null) listener.onGuardado();
            dismiss();
        } catch (SQLException e) {
            Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void bloquearParaVisualizacion() {
        tvTitulo.setText("Detalle");
        spVehiculo.setEnabled(false); spTipo.setEnabled(false); etDescription.setEnabled(false);
        btnFoto.setVisibility(View.GONE); btnGuardar.setVisibility(View.GONE);
        if (detalleEdicion != null) {
            etDescription.setText(detalleEdicion.getDescripcionDetalle());
            seleccionarEnSpinner(spVehiculo, detalleEdicion.getIdVehiculo());
            seleccionarEnSpinner(spTipo, detalleEdicion.getIdTipoDesperfecto());
        }
    }

    private void prepararParaEdicion() {
        tvTitulo.setText("Editar");
        btnGuardar.setText("ACTUALIZAR");
        spVehiculo.setEnabled(false);
        etDescription.setText(detalleEdicion.getDescripcionDetalle());
        seleccionarEnSpinner(spVehiculo, detalleEdicion.getIdVehiculo());
        seleccionarEnSpinner(spTipo, detalleEdicion.getIdTipoDesperfecto());
    }
}
