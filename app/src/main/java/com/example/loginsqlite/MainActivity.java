package com.example.loginsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText cajaAddusuario,cajaAddcontraseña,cajaAddnombre,cajaAddemail;
    Button btnRegistrar;
    TextView login;

    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         cajaAddusuario=findViewById(R.id.cajaAddusuario);
         cajaAddcontraseña=findViewById(R.id.cajaAddcontraseña);
         cajaAddnombre=findViewById(R.id.cajaAddnombre);
         cajaAddemail=findViewById(R.id.cajaAddemail);
         login=findViewById(R.id.login);
        db=new DBHelper(this);

    btnRegistrar=findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = cajaAddusuario.getText().toString().trim(); // Añadido .trim() para eliminar espacios en blanco
                String password = cajaAddcontraseña.getText().toString().trim(); // Añadido .trim() para eliminar espacios en blanco
                String name = cajaAddnombre.getText().toString();
                String email = cajaAddemail.getText().toString().trim();
                if (user.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) { // Cambiado de 'equals("")' a 'isEmpty()' para una mejor práctica
                    Toast.makeText(MainActivity.this, "Por favor, Ingrese las celdas", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkuser = db.checkusername(user);
                    if (!checkuser) { // Cambiado de 'if (checkuser == true)' a 'if (!checkuser)'
                        Boolean insert = db.insertData(user, password,name,email);
                        if (insert) {
                            Toast.makeText(MainActivity.this, "Registro Exitoso!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Registro Fallido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario Existente, Inicie Seccion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}