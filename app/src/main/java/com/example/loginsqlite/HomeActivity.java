package com.example.loginsqlite;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import androidx.appcompat.app.AlertDialog;
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


    btnAceptar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String type = comboTransacciones.getSelectedItem().toString();
            double amount = Double.parseDouble(cajaMonto.getText().toString());
            String message = updateBalance(type, amount);
            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    });
       /* btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(HomeActivity.this);
                Cursor cursor = dbHelper.getAllTransactions();
                StringBuilder transactionHistory = new StringBuilder();
                while (cursor.moveToNext()) {
                    String type = cursor.getString(cursor.getColumnIndex("Type"));
                    double amount = cursor.getDouble(cursor.getColumnIndex("Amount"));
                    String date = cursor.getString(cursor.getColumnIndex("Date"));
                    String userId = cursor.getString(cursor.getColumnIndex("user_id"));
                    // Aquí puedes obtener el nombre del usuario usando el userId
                    String userName = dbHelper.getUserNameById(userId);
                    transactionHistory.append("Tipo: ").append(type).append(", Monto: ").append(amount).append(", Fecha: ").append(date).append(", Usuario: ").append(userName).append("\n");
            }
                cursor.close();
                // Crear y mostrar el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Historial de Transacciones");
                builder.setMessage(transactionHistory.toString());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Código para ejecutar cuando se presiona el botón OK
                    }
                });
                builder.show();
        });
*/


    }



    public String updateBalance(String type, double amount) {
        DBHelper dbHelper = new DBHelper(this);
        int userId = Integer.parseInt(cajaId.getText().toString());
        String message = "";
        try {
            if (type.equals("Depósito")) {
                dbHelper.addAmountToUserBalance(userId, amount);
                message = "Depósito Exitoso";
            } else if (type.equals("Retiro")) {
                dbHelper.withdrawAmountFromUserBalance(userId, amount);
                message = "Retiro Exitoso";
            }
        } catch (Exception e) {
            // Manejar la excepción, por ejemplo, mostrando un mensaje de error
            message = "Error al realizar la operación";
        }
        return message;
    }

}
