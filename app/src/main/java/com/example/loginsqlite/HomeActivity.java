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

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    TextView etiNombre;
    Spinner comboTransacciones;

    Button btnBuscarFecha,btnHistorial,btnAceptar,btnSaldo,btnBuscar;
    EditText cajaId,cajaMonto,cajaFecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        etiNombre = findViewById(R.id.etiNombre);
        //find component for buttom
        btnBuscarFecha = findViewById(R.id.btnBuscarFecha);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnSaldo=findViewById(R.id.btnSaldo);
        btnBuscar=findViewById(R.id.btnBuscar);
        //find component for Spinner
        comboTransacciones = findViewById(R.id.comboTransacciones);

        //find component for EditText
        cajaId = findViewById(R.id.cajaId);
        cajaMonto = findViewById(R.id.cajaMonto);
        cajaFecha = findViewById(R.id.cajaFecha);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        int userId = intent.getIntExtra("userId", -1);

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
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transactionHistory = getTransactionHistory();
                if (!transactionHistory.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("Historial de Transacciones");
                    builder.setMessage(transactionHistory);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Código para ejecutar cuando se presiona el botón OK
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(HomeActivity.this, "No hay transacciones disponibles.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    btnSaldo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DBHelper dbHelper = new DBHelper(HomeActivity.this);
            int userId = Integer.parseInt(cajaId.getText().toString());
            double saldo = dbHelper.getUserBalance(userId);
            String saldoStr = String.format("%.2f", saldo); // Formatea el saldo con dos decimales
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Saldo");
            builder.setMessage("Tu saldo es: " + saldoStr);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Código para ejecutar cuando se presiona el botón OK
                }
            });
            builder.show();
        }
    });

        btnBuscarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Selecciona una fecha");
                final MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long aLong) {
                        String fechaSeleccionada = materialDatePicker.getHeaderText();
                        // Formatear la fecha a YYYY-MM-DD
                        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String fechaFormateada = formatoFecha.format(aLong);
                        // Mostrar la fecha formateada en el EditText
                        cajaFecha.setText(fechaFormateada);

                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

btnBuscar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String fechaSeleccionada = cajaFecha.getText().toString();
        int userId = Integer.parseInt(cajaId.getText().toString());
        if (!fechaSeleccionada.isEmpty() && userId!= -1) {
            DBHelper dbHelper = new DBHelper(HomeActivity.this);
            String resultado = dbHelper.buscarTransaccionesPorFechaYUsuario(fechaSeleccionada, userId);
            if (!resultado.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Transacciones por Fecha y Usuario");
                builder.setMessage(resultado);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Código para ejecutar cuando se presiona el botón OK
                    }
                });
                builder.show();
            } else {
                Toast.makeText(HomeActivity.this, "No hay transacciones disponibles para la fecha y usuario seleccionados.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(HomeActivity.this, "Por favor, seleccione una fecha y un usuario válido.", Toast.LENGTH_SHORT).show();
        }
    }
});


    }



    public String updateBalance(String type, double amount) {
        DBHelper dbHelper = new DBHelper(this);
        int userId = Integer.parseInt(cajaId.getText().toString());
        String message = "";
        try {
            if (type.equals("Depósito")) {
                dbHelper.depositAmount(userId, amount);
                message = "Depósito Exitoso";
            } else if (type.equals("Retiro")) {
                double currentBalance = dbHelper.getCurrentUserBalance(userId);
                if (amount > currentBalance) {
                    message = "Advertencia: Su saldo es Insuficiente ";
                } else {
                    dbHelper.withdrawAmount(userId, amount);
                    message = "Retiro Exitoso";
                }
            }
        } catch (NumberFormatException e) {
            message = "Monto inválido. Por favor, ingrese un número.";
        } catch (Exception e) {
            message = "Error al realizar la operación";
        }


        return message;
    }


    public String getTransactionHistory() {
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getAllTransactions();
        StringBuilder transactionHistory = new StringBuilder();
        // Definir el formato de la tabla
        String tableFormat = "%-10s %-10s %-10s %-10s %-10s\n";
        // Agregar el encabezado de la tabla
        transactionHistory.append(String.format(tableFormat, "Tipo", "Monto", "Fecha", "Usuario", "ID"));
        while (cursor.moveToNext()) {
            int typeIndex = cursor.getColumnIndex("Type");
            int amountIndex = cursor.getColumnIndex("Amount");
            int dateIndex = cursor.getColumnIndex("Date");
            int userIdIndex = cursor.getColumnIndex("user_id");
            if (typeIndex!= -1 && amountIndex!= -1 && dateIndex!= -1 && userIdIndex!= -1) {
                String type = cursor.getString(typeIndex);
                double amount = cursor.getDouble(amountIndex);
                String date = cursor.getString(dateIndex);
                String userId = cursor.getString(userIdIndex);
                String lineFormat = "%-10s %-10s %-10s %-10s %-10s\n";
                // Obtener el nombre del usuario usando el userId
                String userName = dbHelper.getUserNameById(userId);

                // Formatear la línea de la transacción
                transactionHistory.append(String.format(tableFormat, type, amount, date, userName, userId));
            }
        }
        cursor.close();
        return transactionHistory.toString();
    }








}
