package com.example.personalizedlearningexperience;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        findViewById(R.id.textViewNeedAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Registration.class);
                startActivity(i);
            }
        });
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = checkCredentials(editTextUsername.getText().toString(), editTextPassword.getText().toString());
                if (id == null || id.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, Dashboard.class);
                    i.putExtra("id", id);
                    startActivity(i);
                }
            }
        });
    }

    public String checkCredentials(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(
                "my_table",
                new String[]{"id"},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String userId = null;
        if (cursor.moveToFirst()) {
            userId = cursor.getString(cursor.getColumnIndex("id"));
        }
        cursor.close();
        db.close();

        return userId;
    }
}