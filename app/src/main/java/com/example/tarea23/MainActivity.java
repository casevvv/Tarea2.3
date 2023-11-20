package com.example.tarea23;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tarea23.config.photograh;
import com.example.tarea23.config.sqlConection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btn_takePhoto, btn_viewImages, btn_save;
    EditText multiline_txt_description;
    ImageView imgView;
    private static final int IMAGE_CAPTURE = 1;
    private byte[] fotoTomada;
    static final int Peticion_AccesoCamara = 101;
    static final int Peticion_TomarFoto = 102;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_takePhoto = (Button) findViewById(R.id.btn_takePhoto);
        btn_viewImages = (Button) findViewById(R.id.btn_views_images);
        btn_save = (Button) findViewById(R.id.btn_save);

        imgView = (ImageView) findViewById(R.id.img);
        multiline_txt_description = (EditText) findViewById(R.id.et_desc);

        //Setear el manejador de eventos click
        btn_takePhoto.setOnClickListener(btn_id);
        btn_viewImages.setOnClickListener(btn_id);
        btn_save.setOnClickListener(btn_id);
    }

    //Manejador de eventos click para la interfaz View para cada btn
    View.OnClickListener btn_id = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Variable tipo clase para almacenar inf de otra clase
            Class<?> actividad = null;
            if (v.getId()==R.id.btn_takePhoto)
                Permisos();
            if (v.getId()==R.id.btn_views_images)
                actividad = view_images.class;
            if(v.getId()==R.id.btn_save)
                addPhoto();
            if (actividad != null)
                nextActivity(actividad);
        }
    };
    //Metodo para disparar hacia la siguiente actividad
    public void nextActivity(Class<?> actividad){
        Intent intent = new Intent(getApplicationContext(),actividad);
        startActivity(intent);
    }

    private void addPhoto() {
        try {
            if (currentPhotoPath!= null) {
                // Llamar la conexion
                sqlConection Conexion = new sqlConection(this, photograh.namedb, null, 1);
                // Escribir
                SQLiteDatabase db = Conexion.getWritableDatabase();
                // Contenedor de valores
                ContentValues Valores = new ContentValues();
                // Obtener el archivo de imagen como un array de bytes
                byte[] photoBytes = getPhotoAsBytes(currentPhotoPath);
                // Se llenan con los valores ingresados en los campos
                Valores.put(photograh.description, multiline_txt_description.getText().toString());
                Valores.put(photograh.photo, photoBytes);

                Long Result = db.insert(photograh.table, photograh.id, Valores);

                Toast.makeText(this, "Los datos se registraron correctamente", Toast.LENGTH_LONG).show();
                db.close();
            } else {
                Toast.makeText(this, "Debes tomar una foto antes de guardar.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception exception) {
            Toast.makeText( this, "Error", Toast.LENGTH_LONG).show();
        }
    }
    private byte[] getPhotoAsBytes(String filePath) {
        File file = new File(filePath);
        byte[] bytesArray = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); // Lee el archivo en bytes
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

    private void Permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{   Manifest.permission.CAMERA},
                    Peticion_AccesoCamara);
        }
        else
        {
            //Tomar foto
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Peticion_AccesoCamara){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Tomar foto
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getApplicationContext(), "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //Proveedor de contenido- Provaider(Ofrece mecanismos para compartir archivos entre app
                //
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.tarea23.fileprovider", /*Se obtiene del build gradle Module(Ayuda a definir que pertenece a esta aplicación)*/
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Peticion_TomarFoto);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //Añade a formato JPEG
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Accede al directorio de las imagenes
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Crea un formato jpg
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //Capturar url de la img
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Peticion_TomarFoto && resultCode == RESULT_OK){
            try {
                File foto = new File(currentPhotoPath);
                imgView.setImageURI(Uri.fromFile(foto));

            }catch (Exception ex) {
                ex.toString();
            }

        }

    }
}