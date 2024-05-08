package com.example.loginsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    TextView etiNombre;
    Spinner spinner_transaction_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        etiNombre = findViewById(R.id.etiNombre);


        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        if (userName != null) {
            etiNombre.setText(userName);
        } else {
            etiNombre.setText("Usuario no encontrado");


        }
    }
}