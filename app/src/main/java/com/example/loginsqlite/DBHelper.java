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
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE Username=? AND Password=?", new String[]{Username, Password});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst(); // Mover el cursor al primer registro
            int nameIndex = cursor.getColumnIndex("Name"); // Obtener el índice de la columna "Name"
            if (nameIndex!= -1) { // Verificar que el índice no sea -1
                String name = cursor.getString(nameIndex); // Obtener el nombre del usuario
                cursor.close(); // Cerrar el cursor aquí
                return name; // Devolver el nombre del usuario
            } else {
                // Manejar el caso en que la columna "Name" no existe
                return null;
            }
        } else {
            return null; // Devolver null si no se encuentra el usuario
        }
    }





}
