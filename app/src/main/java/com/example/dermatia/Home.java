package com.example.dermatia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    //componentes
    Button btnCerrarSesion, btnSiguiente;
    //firebase

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Instanciar FirebaseAuth
        auth = FirebaseAuth.getInstance();
        //Conexion componentes
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        //funcionamiento boton Cerrar Sesion
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                irLogin();
            }
        });

        //funcionamiento boton Siguiente
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irServicio();

            }
        });

    }

    private void irServicio() {
        Intent intent = new Intent(Home.this, Servicio.class);
        startActivity(intent);

    }

    private void irLogin() {
        Intent intent = new Intent(Home.this, Usuario.class);
        startActivity(intent);

    }
}