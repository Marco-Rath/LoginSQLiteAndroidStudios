package com.example.loginsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME="Minib.db";

    public DBHelper( Context context ) {
        super(context, "Minib.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE usuarios (\n" +
            "    user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    Username TEXT NOT NULL,\n" +
            "    Password TEXT NOT NULL,\n" +
            "    Name TEXT NOT NULL,\n" +
            "    Email TEXT NOT NULL,\n" +
            "    Saldo REAL NOT NULL DEFAULT 0\n" +
            ");");
        db.execSQL("CREATE TABLE transactions (\n" +
                "    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    user_id INTEGER NOT NULL,\n" +
                "    Type TEXT NOT NULL,\n" +
                "    Amount REAL NOT NULL,\n" +
                "    Date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY(user_id) REFERENCES usuarios(user_id)\n" +
                ");");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS usuarios");
    db.execSQL("DROP TABLE IF EXISTS transactions");
    }


    public Boolean insertData(String Username, String Password, String Name, String Email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username", Username);
        contentValues.put("Password", Password);
        contentValues.put("Name", Name);
        contentValues.put("Email", Email);
        long result = db.insert("usuarios", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Boolean checkusername(String Username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE Username=?", new String[]{Username});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Cerrar el cursor aquí
        return exists;
    }


    public String checkusernamepassword(String Username, String Password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE (Username=? OR Email=?) AND Password=?", new String[]{Username, Username, Password});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst(); // Asegúrate de mover el cursor al primer registro
            int nameIndex = cursor.getColumnIndex("Name"); // Obtener el índice de la columna "Name"
            int idIndex = cursor.getColumnIndex("user_id");
            if (nameIndex!= -1 && idIndex!= -1) { // Verificar que el índice no sea -1
                String name = cursor.getString(nameIndex); // Obtener el nombre del usuario
                int userId = cursor.getInt(idIndex); // Obtener el id del usuario
                cursor.close(); // Cerrar el cursor aquí
                return name + "," + userId; // Devolver el nombre del usuario
            } else {
                // Manejar el caso en que la columna "Name" o "user_id" no existan
                return null;
            }
        } else {
            return null; // Devolver null si no se encuentra el usuario
        }
    }

    public void addAmountToUserBalance(int userId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Saldo", amount);
        db.update("usuarios", values, "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
    }
    public void depositAmount(int userId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Obtener el saldo actual del usuario
        Cursor cursor = db.rawQuery("SELECT Saldo FROM usuarios WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            int saldoIndex = cursor.getColumnIndex("Saldo");
            if (saldoIndex!= -1) {
                double currentBalance = cursor.getDouble(saldoIndex);
                // Sumar el monto del saldo actual
                double newBalance = currentBalance + amount;
                ContentValues values = new ContentValues();
                values.put("Saldo", newBalance);
                db.update("usuarios", values, "user_id=?", new String[]{String.valueOf(userId)});

                // Insertar la transacción de depósito en la tabla transactions
                ContentValues transactionValues = new ContentValues();
                transactionValues.put("user_id", userId);
                transactionValues.put("Type", "Depósito");
                transactionValues.put("Amount", amount);
                long result = db.insert("transactions", null, transactionValues);
                if (result == -1) {
                    // Manejar el caso de error
                } else {
                    // Transacción insertada correctamente
                }
            } else {
                // Manejar el caso en que la columna "Saldo" no se encuentra
                throw new IllegalArgumentException("La columna 'Saldo' no se encuentra en la tabla 'usuarios'");
            }
        }
        db.close();
    }




    public void withdrawAmountFromUserBalance(int userId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Obtener el saldo actual del usuario
        Cursor cursor = db.rawQuery("SELECT Saldo FROM usuarios WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            int saldoIndex = cursor.getColumnIndex("Saldo");
            if (saldoIndex!= -1) {
                double currentBalance = cursor.getDouble(saldoIndex);
                // Restar el monto del saldo actual
                double newBalance = currentBalance - amount;
                ContentValues values = new ContentValues();
                values.put("Saldo", newBalance);
                db.update("usuarios", values, "user_id=?", new String[]{String.valueOf(userId)});
            } else {
                // Manejar el caso en que la columna "Saldo" no se encuentra
                throw new IllegalArgumentException("La columna 'Saldo' no se encuentra en la tabla 'usuarios'");
            }
        }
        db.close();
    }


    public void withdrawAmount(int userId, double amount) {
        // Retirar el monto del saldo del usuario
        withdrawAmountFromUserBalance(userId, amount);

        // Insertar la transacción de retiro en la tabla transactions
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("Type", "Retiro");
        values.put("Amount", amount);
        // No necesitamos especificar la fecha, ya que el campo Date tiene un valor predeterminado
        long result = db.insert("transactions", null, values);
        if (result == -1) {
            // Manejar el caso de error
        } else {
            // Transacción insertada correctamente
        }
        db.close();
    }



    public Cursor getAllTransactionsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE user_id=?", new String[]{String.valueOf(userId)});
        return cursor;
    }
    public String getUserNameById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Name FROM usuarios WHERE user_id=?", new String[]{userId});
        if (cursor.moveToFirst()) { // Asegúrate de mover el cursor al primer registro
            int nameIndex = cursor.getColumnIndex("Name"); // Obtener el índice de la columna "Name"
            if (nameIndex!= -1) { // Verificar que el índice no sea -1
                String name = cursor.getString(nameIndex); // Obtener el nombre del usuario
                cursor.close(); // Cerrar el cursor aquí
                return name; // Devolver el nombre del usuario
            } else {
                // Manejar el caso en que la columna "Name" no exista
                return null;
            }
        } else {
            return null; // Devolver null si no se encuentra el usuario
        }
    }

    public double getUserBalance(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Saldo FROM usuarios WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            int saldoIndex = cursor.getColumnIndex("Saldo");
            if (saldoIndex!= -1) {
                return cursor.getDouble(saldoIndex);
            }
        }
        return 0; // Devuelve 0 si no se encuentra el usuario o si el saldo no está disponible
    }
    public double getCurrentUserBalance(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Saldo FROM usuarios WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            int saldoIndex = cursor.getColumnIndex("Saldo");
            if (saldoIndex!= -1) {
                return cursor.getDouble(saldoIndex);
            }
        }
        return 0; // Devuelve 0 si no se encuentra el usuario o si el saldo no está disponible
    }


    public String buscarTransaccionesPorFechaYUsuario(String fechaSeleccionada, int userId) {
        String query = "SELECT * FROM transactions INNER JOIN usuarios ON transactions.user_id = usuarios.user_id WHERE strftime('%Y-%m-%d', transactions.Date) =? AND usuarios.user_id =?";
        Cursor cursor = this.getReadableDatabase().rawQuery(query, new String[]{fechaSeleccionada, String.valueOf(userId)});
        StringBuilder resultado = new StringBuilder();
        if (cursor.moveToFirst()) {
            String tableFormat = "%-10s %-10s %-10s %-10s %-10s\n";
            resultado.append(String.format(tableFormat, "Tipo", "Monto", "Fecha", "Usuario", "ID"));
            while (cursor.moveToNext()) {
                int typeIndex = cursor.getColumnIndex("Type");
                int amountIndex = cursor.getColumnIndex("Amount");
                int dateIndex = cursor.getColumnIndex("Date");
                int userIdIndex = cursor.getColumnIndex("user_id");
                if (typeIndex!= -1 && amountIndex!= -1 && dateIndex!= -1 && userIdIndex!= -1) {
                    String type = cursor.getString(typeIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String date = cursor.getString(dateIndex);
                    String user_idFromCursor = cursor.getString(userIdIndex);
                    String userName = this.getUserNameById(user_idFromCursor);
                    String lineFormat = "%-10s %-10s %-10s %-10s %-10s\n";
                    resultado.append(String.format(lineFormat, type, amount, date, userName, user_idFromCursor));
                }
            }
        }
        cursor.close();
        return resultado.toString();
    }
    public String transferAmount(int tuUserId, int otroUserId, double monto) {
        SQLiteDatabase db = this.getWritableDatabase();
        String message = "Transferencia realizada con éxito.";
        boolean transferExitosa = true; // Asumimos que la transferencia será exitosa hasta que se demuestre lo contrario

        try {
            // Iniciar una transacción
            db.beginTransaction();

            // Actualizar el saldo del usuario 'tuUserId' restando el monto
            Cursor cursor = db.rawQuery("SELECT Saldo FROM usuarios WHERE user_id=?", new String[]{String.valueOf(tuUserId)});
            if (cursor.moveToFirst()) {
                double saldoActual = cursor.getDouble(0);
                if (saldoActual < monto) {
                    transferExitosa = false; // La transferencia no es exitosa si el saldo es insuficiente
                    message = "Error: Saldo insuficiente.";
                } else {
                    ContentValues values1 = new ContentValues();
                    values1.put("Saldo", saldoActual - monto);
                    db.update("usuarios", values1, "user_id=?", new String[]{String.valueOf(tuUserId)});
                }
            }

            // Actualizar el saldo del usuario 'otroUserId' sumando el monto
            Cursor cursor2 = db.rawQuery("SELECT Saldo FROM usuarios WHERE user_id=?", new String[]{String.valueOf(otroUserId)});
            if (cursor2.moveToFirst()) {
                double saldoActual = cursor2.getDouble(0);
                ContentValues values2 = new ContentValues();
                values2.put("Saldo", saldoActual + monto);
                db.update("usuarios", values2, "user_id=?", new String[]{String.valueOf(otroUserId)});
            }

            // Insertar la transacción en la tabla 'transactions'
            ContentValues transactionValues = new ContentValues();
            transactionValues.put("user_id", tuUserId);
            transactionValues.put("Type", "Transferencia");
            transactionValues.put("Amount", -monto); // El monto es negativo para indicar una salida
            db.insert("transactions", null, transactionValues);

            // Insertar la transacción para el otro usuario
            ContentValues transactionValues2 = new ContentValues();
            transactionValues2.put("user_id", otroUserId);
            transactionValues2.put("Type", "Transferencia");
            transactionValues2.put("Amount", monto); // El monto es positivo para indicar una entrada
            db.insert("transactions", null, transactionValues2);

            // Finalizar la transacción
            if (transferExitosa) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
            message = "Error al realizar la transferencia.";
        } finally {
            // Finalizar la transacción, ya sea con éxito o con error
            db.endTransaction();
        }
        return message;
    }






}
