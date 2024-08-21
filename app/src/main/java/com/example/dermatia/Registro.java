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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {

    //componentes
    TextInputEditText etCorreoRegistro;
    TextInputEditText etContraseñaRegistro;
    TextInputEditText etContraseñaRegistroConfirmar;
    MaterialButton btnRegistrarUsuario, btnVolverAlInicio;

    //Firebase
    FirebaseAuth auth;

    //variables
    private String correo, contraseña, contraseñaConfirmada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etContraseñaRegistro = findViewById(R.id.etContraseñaRegistro);
        etContraseñaRegistroConfirmar = findViewById(R.id.etContraseñaRegistroConfirmar);
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);
        btnVolverAlInicio = findViewById(R.id.btnVolverAlInicio);

        auth = FirebaseAuth.getInstance();

        //funcionamiento boton registrar usuario
        btnRegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = etCorreoRegistro.getText().toString().trim();
                contraseña = etContraseñaRegistro.getText().toString().trim();
                contraseñaConfirmada = etContraseñaRegistroConfirmar.getText().toString().trim();

                if(correo.isEmpty() || contraseña.isEmpty() || contraseñaConfirmada.isEmpty()){
                    Toast.makeText(Registro.this,"Por favor complete los espacios",Toast.LENGTH_SHORT).show();
                } else if (!contraseña.equals(contraseñaConfirmada)) {
                    Toast.makeText(Registro.this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                } else if (contraseñaConfirmada.length() <6) {
                    Toast.makeText(Registro.this,"La contraseña debe tener mas de 6 caracteres",Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Registro.this,"Se creo la cuenta correctamente",Toast.LENGTH_SHORT).show();
                                irLogin();
                            }
                            else{
                                Toast.makeText(Registro.this,"La cuenta ya existe",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnVolverAlInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irLogin();
            }
        });
    }

    private void irLogin() {
        Intent intent = new Intent(Registro.this, Usuario.class);
        startActivity(intent);
        finish();
    }

}