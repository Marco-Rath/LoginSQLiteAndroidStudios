package com.example.loginsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    TextView etiNombre;
    Spinner comboTransacciones;

    Button btnBuscarFecha,btnHistorial,btnAceptar;
    EditText cajaId,cajaMonto,cajaFecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        etiNombre = findViewById(R.id.etiNombre);

        btnBuscarFecha=findViewById(R.id.btnBuscarFecha);
        btnHistorial=findViewById(R.id.btnHistorial);
        btnAceptar=findViewById(R.id.btnAceptar);
        comboTransacciones=findViewById(R.id.comboTransacciones);

        cajaId=findViewById(R.id.cajaId);
        cajaMonto=findViewById(R.id.cajaMonto);
        cajaFecha=findViewById(R.id.cajaFecha);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        int userId = intent.getIntExtra("userId",-1);

        if (userName != null) {
            etiNombre.setText(userName);
            cajaId.setText(String.valueOf(userId));

        } else {
            etiNombre.setText("Usuario no encontrado");
            cajaId.setText("");

        }

        comboTransacciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(HomeActivity.this, "Selecciono " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(HomeActivity.this, "Seleccione la Transferencia", Toast.LENGTH_SHORT).show();
            }
        });












    }
}