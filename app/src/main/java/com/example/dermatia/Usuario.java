package com.example.dermatia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Usuario extends AppCompatActivity {

    //componentes
    TextInputEditText etCorreo;
    TextInputEditText etContraseña;
    MaterialButton btnRegistrar;
    MaterialButton btnAcceder;

    //Firebase autenticacion
    FirebaseAuth auth;
    //variables
    private String correo;
    private String contraseña;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //vincular componentes
        etCorreo = findViewById(R.id.etCorreo);
        etContraseña = findViewById(R.id.etContraseña);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnAcceder = findViewById(R.id.btnAcceder);
        auth = FirebaseAuth.getInstance();

        //funcionamiento boton acceder
        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = etCorreo.getText().toString().trim();
                contraseña = etContraseña.getText().toString().trim();
                if(correo.isEmpty() || contraseña.isEmpty()){
                    Toast.makeText(Usuario.this, "Por favor complete los espacios", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Usuario.this, "Inicio correcto", Toast.LENGTH_SHORT).show();
                                irHome();
                            }
                            else{
                                Toast.makeText(Usuario.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        //funcionamiento boton Registrar

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irRegistro();
            }
        });

    }
    //Al Home si hay sesion abierta

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuario = auth.getCurrentUser();
        if(usuario != null){
            irHome();
        }
    }

    private void irHome() {
        Intent intent = new Intent(Usuario.this, Home.class);
        startActivity(intent);


    }

    private void irRegistro(){
        Intent intent = new Intent(Usuario.this, Registro.class);
        startActivity(intent);

    }
}