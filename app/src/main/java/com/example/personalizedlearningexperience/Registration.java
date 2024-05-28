package com.example.personalizedlearningexperience;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class Registration extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    FrameLayout fr_image;
    ImageView imageViewProfile;
    private byte[] imageData;
    EditText editTextUsername, editTextEmail, editTextConfirmEmail, editTextPassword, editTextConfirmPassword, editTextPhoneNumber;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        fr_image = findViewById(R.id.fr_image);
        fr_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        dbHelper = new DBHelper(this);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextConfirmEmail = findViewById(R.id.editTextConfirmEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        findViewById(R.id.buttonCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String email = editTextEmail.getText().toString();
                String confirmEmail = editTextConfirmEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();

                if (username.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(Registration.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (!email.equals(confirmEmail)) {
                    Toast.makeText(Registration.this, "Email and Confirm Email do not match", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Registration.this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                } else {
                    long id = insertUserData(username, email, password, phoneNumber);
                    Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Registration.this, YourInterests.class);
                    i.putExtra("data", new String[]{ String.valueOf(id), username, ""});
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageViewProfile.setImageBitmap(bitmap);

                // Convert bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                imageData = stream.toByteArray();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long insertUserData(String username, String email, String password, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("interest", "");
        if(imageData == null) {
            values.put("image", convertBitmapToByteArray(BitmapFactory.decodeResource(getResources(), R.drawable.profile)));
        }else {
            values.put("image", imageData);
        }
        long id = db.insert("my_table", null, values);
        db.close();
        return id;
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        return stream.toByteArray();
    }
}