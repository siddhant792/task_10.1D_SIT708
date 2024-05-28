package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {

    DBHelper dbHelper;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        String id = getIntent().getStringExtra("id");
        dbHelper = new DBHelper(this);
        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello this is my profile");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
        String[] user = fetchUserData(id);
        TextView textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewGreeting.setText("Hello, " + user[0]);
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        imageViewProfile.setImageBitmap(bitmap);

        findViewById(R.id.btnUpgrade).setOnClickListener(v -> {
            startActivity(new Intent(this, Upgrade.class));
        });

        TextView tvTotalQues = findViewById(R.id.tvTotalQues);
        TextView tvAnsweredCorrectly = findViewById(R.id.tvAnsweredCorrectly);
        TextView tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);
        tvTotalQues.setText(getTotalRowCount());
        tvAnsweredCorrectly.setText(getCountOfEqualCorrectUserAnswered());
        tvIncorrectAnswers.setText(getCountOfNotEqualCorrectUserAnswered());

        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, History.class));
        });
    }

    public String[] fetchUserData(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {userId};
        Cursor cursor = db.query(
                "my_table",
                new String[]{"username", "interest", "image"},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String[] userData = null;
        if (cursor.moveToFirst()) {
            userData = new String[2];
            userData[0] = cursor.getString(cursor.getColumnIndex("username"));
            userData[1] = cursor.getString(cursor.getColumnIndex("interest"));
        }
        byte[] imageData = cursor.getBlob(cursor.getColumnIndex("image"));
        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        cursor.close();
        db.close();

        return userData;
    }

    public String getTotalRowCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM history", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count + "";
    }

    public String getCountOfEqualCorrectUserAnswered() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM history WHERE correct = userAnswered", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count + "";
    }

    public String getCountOfNotEqualCorrectUserAnswered() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM history WHERE correct != userAnswered", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count + "";
    }
}