package com.example.personalizedlearningexperience;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class YourInterests extends AppCompatActivity {
    private ListView listViewTopics;
    private List<String> selectedTopics = new ArrayList<>();

    DBHelper dbHelper;
    private static final int MAX_SELECTED_TOPICS = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_interests);
        dbHelper = new DBHelper(this);
        List<String> topics = getTopics();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, topics);

        listViewTopics = findViewById(R.id.listViewTopics);
        listViewTopics.setAdapter(adapter);
        listViewTopics.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listViewTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String topic = topics.get(position);
                if (listViewTopics.isItemChecked(position)) {
                    if (selectedTopics.size() >= MAX_SELECTED_TOPICS) {
                        Toast.makeText(YourInterests.this, "You can select maximum " + MAX_SELECTED_TOPICS + " topics", Toast.LENGTH_SHORT).show();
                        listViewTopics.setItemChecked(position, false);
                    } else {
                        selectedTopics.add(topic);
                    }
                } else {
                    selectedTopics.remove(topic);
                }
            }
        });

        String[] data = getIntent().getStringArrayExtra("data");

        findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String interest = arrayListToString(selectedTopics);
                updateUserInterest(data[0], interest);
            }
        });
    }

    public static String arrayListToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void updateUserInterest(String userId, String newInterest) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("interest", newInterest);
        String selection = "id = ?";
        String[] selectionArgs = {userId};
        db.update("my_table", values, selection, selectionArgs);
        Toast.makeText(this, "Interests has been added!", Toast.LENGTH_SHORT).show();
        db.close();

        Intent i = new Intent(YourInterests.this, Dashboard.class);
        i.putExtra("id", userId);
        startActivity(i);
    }

    private List<String> getTopics() {
        List<String> topics = new ArrayList<>();
        topics.add("Data Structures");
        topics.add("Algorithms");
        topics.add("Web Development");
        topics.add("JavaScript");
        topics.add("HTML5");
        topics.add("CSS3");
        topics.add("Responsive Web Design");
        topics.add("Backend Development");
        topics.add("Node.js");
        topics.add("Express.js");
        topics.add("Database Management Systems");
        topics.add("SQL");
        topics.add("NoSQL Databases");
        topics.add("Object-Oriented Programming (OOP)");
        topics.add("Version Control Systems (e.g., Git)");
        topics.add("Software Development Life Cycle (SDLC)");
        topics.add("Agile Methodologies");
        topics.add("DevOps");
        topics.add("Cloud Computing");
        topics.add("Cybersecurity");
        return topics;
    }

}