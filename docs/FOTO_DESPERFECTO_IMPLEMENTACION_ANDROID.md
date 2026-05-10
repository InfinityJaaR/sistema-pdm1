# Foto Desperfecto — Guía de Implementación Android
## PDM115 · Para: Yami · Grupo 02 · UES
**Fecha:** Mayo 2026

---

## Contexto antes de empezar

Este módulo **no tiene Activity propia**. Todo vive dentro de `DetalleDesperfectoFormDialog`.
El modelo real en el proyecto es:

```java
// models/FotoDesperfecto.java — campos reales
private int id;
private int idDetalleDesperfecto;   // FK a DETALLE_DESPERFECTO
private String rutaImagen;          // ruta absoluta del archivo en el dispositivo
private String fechaToma;           // "yyyy-MM-dd"
```

> ⚠️ El prompt de referencia usa `ruta_archivo` — en el proyecto real el campo
> se llama `rutaImagen` → columna BD `RUTA_IMAGEN`. Usa siempre el nombre del
> campo Java del modelo, no inventes columnas nuevas.

**Configuración del proyecto relevante para esta guía:**
- `minSdk = 24` (Android 7.0)
- `targetSdk = 36` (Android 16)
- Java 11
- Sin dependencias externas (Glide, Picasso, etc. no están disponibles)

---

## Sección 1: Dónde guardar las fotos

### Almacenamiento interno de la app (recomendado)

```
/data/data/com.ues.sistema_pdm1/files/fotos/
    detalle_1_1716000000000.jpg
    detalle_1_1716000001234.jpg
    detalle_2_1716000005678.jpg
```

**Por qué almacenamiento interno:**
- `getFilesDir()` — privado a la app, no necesita permisos de escritura
- No aparece en la galería del usuario
- Se borra automáticamente al desinstalar la app
- Compatible con minSdk 24 sin permisos adicionales para escritura

**Qué guardar en la base de datos:**
La ruta **absoluta** del archivo, obtenida con `file.getAbsolutePath()`.

```
BD FOTO_DESPERFECTO:
  RUTA_IMAGEN = "/data/data/com.ues.sistema_pdm1/files/fotos/detalle_1_1716000000000.jpg"
```

Al leer de BD, pasar esa ruta directamente a `BitmapFactory.decodeFile(rutaImagen)`.

---

## Sección 2: Permisos necesarios

### Qué necesitas en AndroidManifest.xml

```xml
<!-- Cámara — siempre necesario -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />

<!-- Galería — solo para seleccionar imágenes existentes -->
<!-- API 24-32: usa READ_EXTERNAL_STORAGE -->
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<!-- API 33+: usa READ_MEDIA_IMAGES -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- WRITE_EXTERNAL_STORAGE NO es necesario si usas getFilesDir() -->
```

### Verificar permiso en runtime (obligatorio desde Android 6.0 / API 23)

Como `minSdk = 24`, **todos los usuarios del proyecto necesitan runtime permission**.
Los permisos peligrosos (`CAMERA`, `READ_EXTERNAL_STORAGE`, `READ_MEDIA_IMAGES`)
hay que pedirlos en código, no solo en el Manifest.

```java
// En DetalleDesperfectoFormDialog — verificar antes de abrir cámara
private boolean tieneCamaraPermiso() {
    return ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED;
}

// Verificar permiso de galería (compatible con API 24-36)
private boolean tieneGaleriaPermiso() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED;
    } else {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }
}
```

---

## Sección 3: ActivityResultLauncher — API moderna (sin deprecated)

Como `targetSdk = 36`, el método `startActivityForResult()` muestra advertencias
de deprecated. Usar `ActivityResultLauncher` (AndroidX Activity 1.2+, ya incluido
en el proyecto).

Los launchers se **declaran como campos** y se **registran en `onCreate()`** del Fragment
(no en `onCreateDialog()` ni en `onViewCreated()`).

### Estructura en DetalleDesperfectoFormDialog.java

```java
public class DetalleDesperfectoFormDialog extends DialogFragment {

    // ─── Campos de foto ───────────────────────────────────────────────
    private GenericDAO<FotoDesperfecto> fotoDAO;
    private List<FotoDesperfecto> listaFotos = new ArrayList<>();
    private ListView lvFotos;
    private Button btnAgregarFoto;
    private int idDetalleActual = 0; // 0 = nuevo detalle, >0 = editar

    // ─── Launchers registrados en onCreate() ─────────────────────────

    private ActivityResultLauncher<Void> launcherCamara;
    private ActivityResultLauncher<String> launcherGaleria;
    private ActivityResultLauncher<String[]> launcherPermisoCamara;
    private ActivityResultLauncher<String> launcherPermisoGaleria;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fotoDAO = new GenericDAO<>(requireContext(), FotoDesperfecto.class, "foto_desperfecto");

        // Launcher: capturar foto con cámara
        launcherCamara = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    guardarFoto(bitmap);
                }
            }
        );

        // Launcher: seleccionar de galería
        launcherGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    procesarImagenDeUri(uri);
                }
            }
        );

        // Launcher: pedir permiso de cámara
        launcherPermisoCamara = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permisos -> {
                if (Boolean.TRUE.equals(permisos.get(Manifest.permission.CAMERA))) {
                    launcherCamara.launch(null);
                } else {
                    Toast.makeText(requireContext(),
                        "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            }
        );

        // Launcher: pedir permiso de galería
        launcherPermisoGaleria = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            concedido -> {
                if (concedido) {
                    launcherGaleria.launch("image/*");
                } else {
                    Toast.makeText(requireContext(),
                        "Permiso de galería denegado", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
```

---

## Sección 4: Flujo completo de operaciones

### 4.1 Botón "Agregar Foto" → elegir fuente

```java
private void abrirSelectorFuente() {
    String[] opciones = {"Cámara", "Galería"};
    new AlertDialog.Builder(requireContext())
        .setTitle("Agregar foto")
        .setItems(opciones, (dialog, which) -> {
            if (which == 0) abrirCamara();
            else abrirGaleria();
        })
        .show();
}

private void abrirCamara() {
    if (tieneCamaraPermiso()) {
        launcherCamara.launch(null);
    } else {
        launcherPermisoCamara.launch(new String[]{Manifest.permission.CAMERA});
    }
}

private void abrirGaleria() {
    if (tieneGaleriaPermiso()) {
        launcherGaleria.launch("image/*");
    } else {
        String permiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;
        launcherPermisoGaleria.launch(permiso);
    }
}
```

### 4.2 Guardar foto desde Bitmap (resultado de cámara)

```java
private void guardarFoto(Bitmap bitmap) {
    // 1. Crear carpeta si no existe
    File carpeta = new File(requireContext().getFilesDir(), "fotos");
    if (!carpeta.exists()) carpeta.mkdirs();

    // 2. Nombre único con timestamp
    String nombreArchivo = "detalle_" + idDetalleActual + "_"
        + System.currentTimeMillis() + ".jpg";
    File archivo = new File(carpeta, nombreArchivo);

    // 3. Comprimir y guardar a disco
    try (FileOutputStream fos = new FileOutputStream(archivo)) {
        // Escalar a máximo 1024x1024 antes de guardar
        Bitmap escalado = escalarBitmap(bitmap, 1024, 1024);
        escalado.compress(Bitmap.CompressFormat.JPEG, 85, fos);
        fos.flush();
    } catch (IOException e) {
        Toast.makeText(requireContext(),
            "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        return;
    }

    // 4. Insertar en BD con ruta absoluta y fecha de hoy
    String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(new Date());
    FotoDesperfecto foto = new FotoDesperfecto(
        0, idDetalleActual, archivo.getAbsolutePath(), fecha);
    try {
        fotoDAO.insertar(foto);
        cargarFotos(); // refresca ListView
    } catch (Exception e) {
        Toast.makeText(requireContext(),
            "Error al registrar foto", Toast.LENGTH_SHORT).show();
    }
}
```

### 4.3 Guardar foto desde Uri (resultado de galería)

```java
private void procesarImagenDeUri(Uri uri) {
    try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
            requireActivity().getContentResolver(), uri);
        guardarFoto(bitmap);
    } catch (IOException e) {
        Toast.makeText(requireContext(),
            "Error al leer imagen", Toast.LENGTH_SHORT).show();
    }
}
```

### 4.4 Escalar Bitmap antes de guardar (evitar OOM)

```java
private Bitmap escalarBitmap(Bitmap original, int maxAncho, int maxAlto) {
    int ancho = original.getWidth();
    int alto  = original.getHeight();

    if (ancho <= maxAncho && alto <= maxAlto) return original;

    float escala = Math.min((float) maxAncho / ancho, (float) maxAlto / alto);
    int nuevoAncho = Math.round(ancho * escala);
    int nuevoAlto  = Math.round(alto  * escala);

    return Bitmap.createScaledBitmap(original, nuevoAncho, nuevoAlto, true);
}
```

### 4.5 Ver foto — Dialog de previsualización grande

```java
private void abrirVistaFoto(FotoDesperfecto foto) {
    File archivo = new File(foto.getRutaImagen());
    if (!archivo.exists()) {
        Toast.makeText(requireContext(),
            "Archivo no encontrado", Toast.LENGTH_SHORT).show();
        return;
    }

    Bitmap bitmap = BitmapFactory.decodeFile(foto.getRutaImagen());
    if (bitmap == null) {
        Toast.makeText(requireContext(),
            "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
        return;
    }

    ImageView imageView = new ImageView(requireContext());
    imageView.setImageBitmap(bitmap);
    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    imageView.setAdjustViewBounds(true);

    new AlertDialog.Builder(requireContext())
        .setTitle("Foto " + foto.getFechaToma())
        .setView(imageView)
        .setNegativeButton("Cerrar", null)
        .setPositiveButton("Eliminar", (d, w) -> confirmarEliminar(foto))
        .show();
}
```

### 4.6 Eliminar foto

```java
private void confirmarEliminar(FotoDesperfecto foto) {
    new AlertDialog.Builder(requireContext())
        .setTitle("Eliminar foto")
        .setMessage("¿Descartar esta foto? Esta acción no se puede deshacer.")
        .setPositiveButton("Sí, eliminar", (d, w) -> eliminarFoto(foto))
        .setNegativeButton("Cancelar", null)
        .show();
}

private void eliminarFoto(FotoDesperfecto foto) {
    // 1. Borrar archivo físico primero
    File archivo = new File(foto.getRutaImagen());
    if (archivo.exists()) archivo.delete();

    // 2. Borrar registro de BD
    try {
        fotoDAO.eliminar(foto.getId());
        cargarFotos();
        Toast.makeText(requireContext(), "Foto eliminada", Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
        Toast.makeText(requireContext(),
            "Error al eliminar foto", Toast.LENGTH_SHORT).show();
    }
}
```

---

## Sección 5: Cargar fotos en el ListView

```java
private void cargarFotos() {
    if (idDetalleActual == 0) {
        // Detalle nuevo aún no tiene ID — lista vacía
        listaFotos = new ArrayList<>();
    } else {
        try {
            listaFotos = fotoDAO.obtenerPor(
                "ID_DETALLE_DESPERFECTO", String.valueOf(idDetalleActual));
        } catch (Exception e) {
            listaFotos = new ArrayList<>();
        }
    }
    actualizarAdapterFotos();
}

private void actualizarAdapterFotos() {
    ArrayAdapter<FotoDesperfecto> adapter = new ArrayAdapter<FotoDesperfecto>(
        requireContext(), android.R.layout.simple_list_item_1, listaFotos) {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Usa el layout simple para mostrar "Foto [fecha]"
            View vista = super.getView(position, convertView, parent);
            FotoDesperfecto foto = getItem(position);
            if (foto != null) {
                ((TextView) vista.findViewById(android.R.id.text1))
                    .setText("Foto — " + foto.getFechaToma());
            }
            return vista;
        }
    };
    lvFotos.setAdapter(adapter);

    // Tap en ítem → ver foto grande
    lvFotos.setOnItemClickListener((parent, view, pos, id) ->
        abrirVistaFoto(listaFotos.get(pos)));
}
```

> Para mostrar previews miniatura en cada ítem en lugar de texto, crear
> `FotoDesperfectoAdapter extends ArrayAdapter<FotoDesperfecto>` con un layout
> `item_foto_desperfecto.xml` que tenga un `ImageView` pequeño (80×80dp).
> El Adapter carga el Bitmap con `BitmapFactory.decodeFile()` en `getView()`.
> Limitar el tamaño del preview al decodificar con `BitmapFactory.Options.inSampleSize`
> para no saturar memoria.

---

## Sección 6: Integrar en DetalleDesperfectoFormDialog

### Cómo recibir el idDetalle

El FormDialog se usa para insertar (nuevo) y editar (existente).
Para las fotos, necesitas saber el ID del detalle al que pertenecen.

**Caso Editar:** el detalle ya existe, su ID se pasa como argumento al abrir el dialog.

```java
// En DetalleDesperfectoActivity, al abrir para editar:
DetalleDesperfectoFormDialog dialog = DetalleDesperfectoFormDialog.newInstance(detalle);
// El dialog lee detalle.getId() → asigna a idDetalleActual → carga fotos
```

**Caso Insertar:** el detalle no existe aún, así que no hay fotos disponibles
hasta que se guarde. Estrategia recomendada:

```
Al guardar el formulario por primera vez:
1. Insertar el DetalleDesperfecto → obtener el ID generado
2. Con ese ID, insertar las fotos que el usuario agregó en memoria temporal
3. Mostrar confirmación

Alternativa más simple para el proyecto:
→ Deshabilitar la sección de fotos al insertar
→ Habilitarla solo al editar (cuando ya existe el ID)
→ Mostrar mensaje: "Guarda el detalle primero para agregar fotos"
```

### Obtener el ID después de insertar

```java
// GenericDAO.insertar() no devuelve el ID directamente
// Buscar el registro recién insertado para obtener su ID:
private int obtenerUltimoIdDetalle() {
    List<DetalleDesperfecto> todos = detalleDAO.obtenerTodos();
    if (todos.isEmpty()) return 0;
    return todos.get(todos.size() - 1).getId();
}
```

---

## Sección 7: Manejo de ciclo de vida

### Problema: fotos huérfanas

Si el usuario agrega fotos y luego cierra el dialog sin guardar el detalle,
las fotos quedaron guardadas en disco y en BD pero apuntan a un detalle
que nunca se creó (o se creó con ID temporal).

**Solución recomendada para el proyecto:**
Usar una lista temporal en memoria mientras el dialog está abierto.
Solo insertar en BD cuando se confirma guardar el detalle.

```java
// Lista temporal de Bitmaps pendientes de guardar (no en BD aún)
private List<Bitmap> fotosPendientes = new ArrayList<>();
private List<String> rutasPendientes = new ArrayList<>();

// Al agregar foto: guardar archivo en disco pero NO en BD todavía
private void guardarFotoTemporal(Bitmap bitmap) {
    // Guardar archivo en disco con nombre temporal
    File archivo = guardarArchivoEnDisco(bitmap, "temp_" + System.currentTimeMillis());
    rutasPendientes.add(archivo.getAbsolutePath());
    fotosPendientes.add(bitmap);
    mostrarFotosPendientes(); // actualizar UI
}

// Al confirmar guardar detalle:
private void confirmarGuardarConFotos(int idDetalleNuevo) {
    String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    for (String ruta : rutasPendientes) {
        // Renombrar archivo con el ID real
        FotoDesperfecto foto = new FotoDesperfecto(0, idDetalleNuevo, ruta, fecha);
        fotoDAO.insertar(foto);
    }
    rutasPendientes.clear();
}

// Al cancelar: borrar archivos temporales
@Override
public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    for (String ruta : rutasPendientes) {
        new File(ruta).delete(); // limpiar archivos no guardados
    }
}
```

---

## Sección 8: Validaciones

```java
// Antes de guardar foto — verificar que el archivo no esté vacío
private boolean validarBitmap(Bitmap bitmap) {
    if (bitmap == null) {
        Toast.makeText(requireContext(),
            "No se obtuvo imagen válida", Toast.LENGTH_SHORT).show();
        return false;
    }
    if (bitmap.getWidth() < 10 || bitmap.getHeight() < 10) {
        Toast.makeText(requireContext(),
            "Imagen demasiado pequeña", Toast.LENGTH_SHORT).show();
        return false;
    }
    return true;
}

// Verificar espacio disponible antes de guardar (opcional pero recomendado)
private boolean hayEspacioSuficiente() {
    StatFs stat = new StatFs(requireContext().getFilesDir().getPath());
    long bytesLibres = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    return bytesLibres > 5 * 1024 * 1024; // al menos 5 MB libres
}
```

---

## Sección 9: Estructura de archivos a crear

```
activities/detalleDesperfecto/
    DetalleDesperfectoActivity.java         ← Activity principal (lista de detalles)
    DetalleDesperfectoFormDialog.java       ← Formulario + sección de fotos
    DetalleDesperfectoOptionsDialog.java    ← Ver / Editar / Eliminar
    DetalleDesperfectoViewDialog.java       ← Solo lectura del detalle
    FotoDesperfectoAdapter.java             ← Adapter para el ListView de fotos (opcional)

res/layout/
    activity_detalle_desperfecto.xml
    item_detalle_desperfecto.xml
    dialog_detalle_desperfecto_form.xml     ← incluye sección de fotos al final
    dialog_detalle_desperfecto_view.xml
    item_foto_desperfecto.xml               ← fila del ListView de fotos (ImageView + fecha)
```

### Estructura del layout dialog_detalle_desperfecto_form.xml

```xml
<!-- Esquema conceptual del layout del formulario -->
<ScrollView>
  <LinearLayout vertical>

    <!-- Campos del detalle -->
    <Spinner id="spinnerVehiculo" />
    <Spinner id="spinnerTipoDesperfecto" />
    <EditText id="etDescripcion" hint="Descripción del desperfecto" />
    <Spinner id="spinnerEstado" />   <!-- pendiente / en reparacion / resuelto -->

    <!-- Separador -->
    <TextView text="Fotos del desperfecto" style="subtítulo" />

    <!-- ListView de fotos (altura fija, scroll interior) -->
    <ListView id="lvFotos" android:layout_height="200dp" />

    <!-- Botón agregar foto -->
    <Button id="btnAgregarFoto" text="+ Agregar foto" />

    <!-- Botón guardar -->
    <Button id="btnGuardar" text="Guardar" />

  </LinearLayout>
</ScrollView>
```

---

## Sección 10: Resumen de consideraciones para targetSdk 36

| Aspecto | Solución para este proyecto |
|---------|---------------------------|
| `startActivityForResult` deprecated | Usar `ActivityResultLauncher` (ver Sección 3) |
| Permisos de cámara | Runtime permission con `RequestMultiplePermissions` |
| Galería en API 24-32 | `READ_EXTERNAL_STORAGE` |
| Galería en API 33+ | `READ_MEDIA_IMAGES` |
| Escritura de archivos | `getFilesDir()` — sin permiso necesario |
| Bitmap grande → OOM | `escalarBitmap()` antes de guardar (ver Sección 4.4) |
| `getBitmap()` de Uri deprecated en API 28+ | Sigue siendo funcional; alternativa: `ImageDecoder.createSource()` para API 28+ |
| Archivos privados vs públicos | Usar `getFilesDir()` (privado) — evitar `getExternalFilesDir()` |