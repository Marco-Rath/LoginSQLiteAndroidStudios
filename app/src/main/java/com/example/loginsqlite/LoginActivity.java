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

public class LoginActivity extends AppCompatActivity {
    EditText username,password;
    Button btnLogin;
    DBHelper db;

    TextView registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=findViewById(R.id.cajaUsuario);
        password=findViewById(R.id.cajaContraseña);
        registrar=findViewById(R.id.registrar);

        btnLogin=findViewById(R.id.btnIniciar);
        db=new DBHelper(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Por favor,Ingrese los campos", Toast.LENGTH_SHORT).show();
                } else {
                    String userName = db.checkusernamepassword(user, pass);

                    if (userName!= null) {
                        String[] userParts = userName.split(",");
                        String userNamePart = userParts[0];
                        int userId = Integer.parseInt(userParts[1]);
                        Toast.makeText(LoginActivity.this, "Inicio Exitoso!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("username", userNamePart); // Pasar el nombre del usuario a HomeActivity
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        username.setText("");
                        password.setText("");
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciades Invalidas!!", Toast.LENGTH_SHORT).show();
                    }
            }



            }
        });


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
}