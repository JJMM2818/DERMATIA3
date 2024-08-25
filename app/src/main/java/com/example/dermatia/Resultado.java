package com.example.dermatia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Resultado extends AppCompatActivity {
    Button btnVolverAlServicio;
    TextView tvDiagnostico, tvDescripcion, tvTratamiento;
    ImageView ivFotoDeDiagnostico;

    String descripcionAcne = "Afección cutánea que se produce cuando los folículos pilosos se llenan de grasa y células muertas de la piel";
    String resultadoAcne = "Limpie su rostro dos veces al día con un jabon suave que no cause resequedad y complemente usando algún producto para el rostro que contenga ácido salicílico o benzoyl";

    String descripcionCaspa = "Es una afección dermatológica común del cuero cabelludo que se estima afecta al 50% de la poblacion mundial. Consiste en la descamación del cuero cabelludo acompañado de picor";
    String resultadoCaspa = "La caspa no se elimina pero si se controla, utiliza shampoo anticaspa que contengan Ketoconazol o Acido Salicílico, recomendamos shampoo 'DERCOS'";

    String descripcionDermatitis = "La dermatitis es una afección frecuente que causa inflamación, irritación y picazón en la piel. Puede tener muchas causas y tipos, y a munudo implica piel seca o un sarpullido. La dermatitis puedes hacer que la piel se ampolle, exude, forme costras o se descame";
    String resultadoDermatitis = "Aplicar una crema que contenga hidrocortisona al 1% una o dos veces al día hasta que los sintomas se regulen, además se pueden aplicar humectantes después del baño";

    String descripcionPicaduras = "Aunque las mordeduras o picaduras de insectos suelen ser inofensivas, en algunos casos pueden transmitir enfermedades, como la malaria o la enfermedad de Lyme. La mayoría de las picaduras causan escozor o comezón. Algunas pueden desencadenar reacciones alérgicas mortales que requieren asistencia médica";
    String resultadoPicaduras = "La mayoría de las picaduras desaparecen por sí solas. Los tratamientos más comunes se centran en aliviar el dolor o el picor y pueden incluir el uso de analgésicos, compresas de hielo y lociones calmantes";

    String descripcionPielSana = "No detectamos ninguna afección en tu piel :)";
    String resultadoPielSana = "Recuerda usar constantemente bloqueador solar, limpiar tu rostro por la mañana y por la noche, beber agua y mantener tu piel hidratada";

    String descripcionRosacea = "La rosácea es un transtorno cutáneo persistente que causa enrojecimiento, granitos y vasos sanguíneos perceptibles, generalmente en la zona central de la cara";
    String resultadoRosacea = "El tratamiento depende de la gravedad del paciente e incluyen antibióticos, medicamentos antiacné y terapia láser";

    String descripcionSarpullido = "Un sarpullido es un área en la piel que  está irritada o inflamada. Muchos sarpullidos son rojos, dolorosos, irritados y pican. Tienen distintas causas como virus, alergias, bacterias, calor o el contacto con productos quimicamente fuertes.";
    String resultadoSarpullido = "Los sarpullidos suelen desaparecer por cuenta propia pero para disminuir los sintomas se pueden usar cremas humectantes.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String resultadoTexto = getIntent().getStringExtra("RESULTADO_TEXTO").trim();
        btnVolverAlServicio = findViewById(R.id.btnVolverAlServicio);
        tvDiagnostico = findViewById(R.id.tvDianostico);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvTratamiento = findViewById(R.id.tvTratamiento);
        ivFotoDeDiagnostico = findViewById(R.id.ivFotoDeDiagnostico);





    btnVolverAlServicio.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Resultado.this, Servicio.class);
            startActivity(intent);
            finish();
        }
    });

    if(resultadoTexto != null){
        tvDiagnostico.setText(resultadoTexto);
        PonerTextos(resultadoTexto);
    }else{
        Toast.makeText(Resultado.this, "Lo sentimos ha ocurrido un error", Toast.LENGTH_SHORT).show();
    }


    }

void PonerTextos(String resultado){
        switch (resultado){
            case "acne":
                tvDescripcion.setText(descripcionAcne);
                tvTratamiento.setText(resultadoAcne);
                ivFotoDeDiagnostico.setImageResource(R.drawable.acne);
                break;
            case "caspa":
                tvDescripcion.setText(descripcionCaspa);
                tvTratamiento.setText(resultadoCaspa);
                ivFotoDeDiagnostico.setImageResource(R.drawable.caspa);
                break;
            case "dermatitis":
                tvDescripcion.setText(descripcionDermatitis);
                tvTratamiento.setText(resultadoDermatitis);
                ivFotoDeDiagnostico.setImageResource(R.drawable.dermatitis);
                break;
            case "picaduras":
                tvDescripcion.setText(descripcionPicaduras);
                tvTratamiento.setText(resultadoPicaduras);
                ivFotoDeDiagnostico.setImageResource(R.drawable.picaduras);
                break;
            case "piel sana":
                tvDescripcion.setText(descripcionPielSana);
                tvTratamiento.setText(resultadoPielSana);
                ivFotoDeDiagnostico.setImageResource(R.drawable.pielsana);
                break;
            case "rosacea":
                tvDescripcion.setText(descripcionRosacea);
                tvTratamiento.setText(resultadoRosacea);
                ivFotoDeDiagnostico.setImageResource(R.drawable.rosacea);
                break;
            case "sarpullido":
                tvDescripcion.setText(descripcionSarpullido);
                tvTratamiento.setText(resultadoSarpullido);
                ivFotoDeDiagnostico.setImageResource(R.drawable.sarpullido);
                break;
            default:
                tvDiagnostico.setText("Lo sentimos a ocurrido un error "+resultado);
        }

}



}