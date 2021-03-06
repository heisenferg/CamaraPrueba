package com.example.camaraprueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
public static int CAPTURA_IMAGEN=1;
    private Uri mContentUri;
    public final int CAPTURA=0;
    private final static int GRABAR_VIDEO = 1;

    Button cameraButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton=findViewById(R.id.buttonCamara);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerFoto();
            }
        });

        requestPermissions(new String[]{"android.permission.CAMERA"},CAPTURA);
       requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},CAPTURA);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void hacerFoto() {
        Intent hacerFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (hacerFotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(hacerFotoIntent, CAPTURA_IMAGEN);
        }
    }




    public void AnadirAGaleria(Uri contentUri){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURA_IMAGEN && resultCode == RESULT_OK) {
            ImageView v = (ImageView) findViewById(R.id.imageView);
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            v.setImageBitmap(imagen);
        }

        if(requestCode==GRABAR_VIDEO && resultCode==RESULT_OK) {
            mContentUri = data.getData();
            AnadirAGaleria(mContentUri);
            VideoView videoView = (VideoView) findViewById(R.id.videoView);
            videoView.setVideoURI(data.getData());
            videoView.start();
        }

    }


    public String mRutaDefinitiva;

    public File crearFichero() throws IOException {
        String tiempo=new
                SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombre_fichero="JPEG_"+tiempo+"_";
        File directorio_almacenaje=
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(nombre_fichero,".jpg",directorio_almacenaje);
        mRutaDefinitiva=image.getAbsolutePath();
        return image;
    }

    public void hacerFoto2(View view) {
        Intent hacerFotoIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
//Crear un fichero y pas??rselo como extra a la actividad
            File fich_foto=null;
            try {
                fich_foto=crearFichero();
            }
            catch(IOException e){
//acci??n a realizar si no he podido crear el fichero
                e.printStackTrace();
            }
            if(fich_foto!=null){
                Uri foto_uri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        fich_foto);
                hacerFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, foto_uri);
                startActivityForResult(hacerFotoIntent,CAPTURA_IMAGEN);
            }
        }
    }

    public void comenzarGrabacion(View view){
        // Creaci??n del intent
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // El v??deo se grabar?? en calidad baja (0)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // Limitamos la duraci??n de la grabaci??n a 5 segundos
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        // Nos aseguramos de que haya una aplicaci??n que pueda manejar el intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Lanzamos el intent
            startActivityForResult(intent, GRABAR_VIDEO);
        }
    }





}