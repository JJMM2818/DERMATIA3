package com.example.dermatia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
    TableLayout tablaResultados;

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
        tablaResultados = findViewById(R.id.tablaResultados);
        btnVerRecomendaciones = findViewById(R.id.btnVerRecomendaciones);


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
                if(imagenVacia(ivFoto)){
                    Toast.makeText(Servicio.this, "Por favor tome o seleccione una foto", Toast.LENGTH_SHORT).show();
                }else{
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

                        //array de probabilidades
                        float[] probabilidades = outputFeature0.getFloatArray();

                        //Arrays para almecenar los mayores
                        float[] maxValores = {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
                        int[] maxIndices = {-1,-1,-1};
                        String[] palabras = {"acne", "caspa", "dermatitis", "picaduras", "piel sana","rosacea", "sarpullido"};

                        //Encontrar los valores mas grandes y sus indices
                        for(int i = 0; i < probabilidades.length; i++){
                            float valorActual = probabilidades[i];

                            if(valorActual > maxValores[0]){
                                //valores hacia abajo
                                maxValores[2] = maxValores[1];
                                maxIndices[2] = maxIndices[1];
                                maxValores[1] = maxValores[0];
                                maxIndices[1] = maxIndices[0];

                                //actualizar valores mas grandes
                                maxValores[0] = valorActual;
                                maxIndices[0] = i;
                            } else if (valorActual > maxValores[1]){
                                maxValores[2] = maxValores[1];
                                maxIndices[2] = maxIndices[1];

                                maxValores[1] = valorActual;
                                maxIndices[1] = i;
                            } else if (valorActual > maxValores[2]) {
                                maxValores[2] = valorActual;
                                maxIndices[2] = i;

                            }
                        }

                        //Agregar encabezados
                        TableRow headerRow = new TableRow(Servicio.this);
                        headerRow.setGravity(Gravity.CENTER);

                        TextView headerIndex = new TextView(Servicio.this);
                        headerIndex.setText("Categoría");
                        headerIndex.setPadding(8,8,8,8);
                        headerIndex.setTextColor(Color.WHITE);
                        headerIndex.setGravity(Gravity.CENTER);


                        headerRow.addView(headerIndex);

                        TextView headerValue = new TextView(Servicio.this);
                        headerValue.setText("Probabilidad");
                        headerValue.setPadding(8, 8, 8, 8);
                        headerValue.setTextColor(Color.WHITE);
                        headerValue.setGravity(Gravity.CENTER);
                        headerRow.addView(headerValue);


                        tablaResultados.addView(headerRow);

                        //Agregar valores a la tabla
                        for(int i =0; i<3; i++){
                            TableRow row = new TableRow(Servicio.this);
                            row.setGravity(Gravity.CENTER);
                            TextView indicesTextView = new TextView(Servicio.this);
                            indicesTextView.setText(palabras[maxIndices[i]]);
                            indicesTextView.setPadding(8,8,8,8);
                            indicesTextView.setTextColor(Color.WHITE);
                            indicesTextView.setTextSize(18);
                            indicesTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            indicesTextView.setGravity(Gravity.CENTER);
                            row.addView(indicesTextView);

                            TextView valorTextView = new TextView(Servicio.this);
                            //AQUI
                            valorTextView.setText(volverPorcentaje(maxValores[i])+" %");
                            valorTextView.setPadding(8,8,8,8);
                            valorTextView.setTextColor(Color.WHITE);
                            valorTextView.setTextSize(18);
                            valorTextView.setGravity(Gravity.CENTER);
                            valorTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            row.addView(valorTextView);



                            tablaResultados.addView(row);
                        }

                        //boton visible
                        btnVerRecomendaciones.setVisibility(View.VISIBLE);


                        /**
                         float[] probabilidades = outputFeature0.getFloatArray();
                         int indice = obtenerMaximo(outputFeature0.getFloatArray());
                         double probabilidad = (probabilidades[indice]/255)*100;
                         String probabilidadString = String.format("%.2f", probabilidad);

                         Toast.makeText(Servicio.this,"Probabilidad: "+probabilidadString+"%", Toast.LENGTH_SHORT).show();
                         **/



                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {
                        Toast.makeText(Servicio.this, "Error " + e, Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        btnVerRecomendaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultadoTexto = tvResultado.getText().toString();
                Intent intent = new Intent(Servicio.this, Resultado.class);
                intent.putExtra("RESULTADO_TEXTO", resultadoTexto);
                startActivity(intent);
                finish();
            }
        });


    }

    String volverPorcentaje(float numero){
        float operacion =  (numero/255)*100;
        String resultado = String.format("%.2f", operacion);
        return resultado;

    }

    //Verificar si ImageView esta vacio
    boolean imagenVacia(ImageView imageView){
        Drawable drawable = imageView.getDrawable();
        return drawable == null;

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
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                ivFoto.setImageBitmap(bitmap);
                //bitmap = (Bitmap) data.getExtras().get("data");
                //ivFoto.setImageBitmap(bitmap);
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(Servicio.this, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Servicio.this,"Ocurrio un error al capturar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}