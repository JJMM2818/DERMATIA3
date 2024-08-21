package com.example.dermatia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dermatia.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Servicio extends AppCompatActivity {

    //componentes
    Button btnSeleccionarImagen, btnTomarFoto, btnConsultar, btnVerRecomendaciones;
    TextView tvResultado;
    ImageView ivFoto;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_servicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //permiso camara
        obtenerPermiso();
        String[] labels = new String[8];
        int cnt = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line = bufferedReader.readLine();
            while (line != null) {
                labels[cnt] = line;
                cnt++;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            Toast.makeText(Servicio.this, "Error " + e, Toast.LENGTH_SHORT).show();
        }


        //vincular componentes
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnTomarFoto = findViewById(R.id.btnTomarfoto);
        btnConsultar = findViewById(R.id.btnConsultar);
        tvResultado = findViewById(R.id.tvResultado);
        ivFoto = findViewById(R.id.ivFoto);


        //funcionamiento boton Seleccionar imagen (entrar en la galeria)
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        //funcionamiento boton Tomar foto (camara)
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });

        //funcionamiento boton consultar (IA)
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Model model = Model.newInstance(Servicio.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    //mostrar resultado en el campo de texto
                    tvResultado.setText(labels[obtenerMaximo(outputFeature0.getFloatArray())] + " ");

                    float[] probabilidades = outputFeature0.getFloatArray();
                    int indice = obtenerMaximo(outputFeature0.getFloatArray());
                    double probabilidad = (probabilidades[indice]/255)*100;
                    String probabilidadString = String.format("%.2f", probabilidad);

                    Toast.makeText(Servicio.this,"Probabilidad: "+probabilidadString+"%", Toast.LENGTH_SHORT).show();




                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    Toast.makeText(Servicio.this, "Error " + e, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }



    int obtenerMaximo(float[] floatArray) {
        int max = 0;
        for (int i = 0; i < floatArray.length; i++) {
            if (floatArray[i] > floatArray[max]) max = i;
        }
        return max;
    }

    private void obtenerPermiso() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Servicio.this, new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

    //Verificar permiso camara
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    this.obtenerPermiso();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //subir foto seleccionada o tomada al Image View
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ivFoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 12) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(bitmap);
            //bitmap = (Bitmap) data.getExtras().get("data");
            //ivFoto.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}