package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    HashMap<String, String> topicDescriptions = new HashMap<>();
    TextView textViewGreeting, textViewNotification;

    DBHelper dbHelper;

    RecyclerView recyclerViewItems;

    Bitmap bitmap;

    ImageView imageViewProfile;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_dashboard);
        fillMap();
        id = getIntent().getStringExtra("id");
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        textViewNotification = findViewById(R.id.textViewNotification);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        String[] data = fetchUserData(id);
        imageViewProfile.setImageBitmap(bitmap);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewGreeting.setText("Hello, " + data[0]);
        List<String> interests = stringToArrayList(data[1]);
        textViewNotification.setText("You have " + interests.size()  + " tasks due");
        TaskAdapter urlAdapter = new TaskAdapter(interests);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(urlAdapter);

        findViewById(R.id.layoutUserInfo).setOnClickListener(v -> {
            Intent i = new Intent(this, Profile.class);
            i.putExtra("id", id);
            startActivity(i);
        });
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private List<String> taskList;

        public TaskAdapter(List<String> urlList) {
            this.taskList = urlList;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            String title = taskList.get(position);
            holder.bind(title);
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        public class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView tv_task_title, tv_task_description;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_task_title = itemView.findViewById(R.id.tv_task_title);
                tv_task_description = itemView.findViewById(R.id.tv_task_description);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Dashboard.this, ViewTask.class);
                        i.putExtra("id", id);
                        i.putExtra("title", taskList.get(getAdapterPosition()));
                        i.putExtra("desc", topicDescriptions.get(taskList.get(getAdapterPosition())));
                        startActivity(i);
                    }
                });
            }

            public void bind(String title) {
                tv_task_title.setText(title);
                tv_task_description.setText(topicDescriptions.get(title));
            }
        }
    }



    public static List<String> stringToArrayList(String input) {
        List<String> list = new ArrayList<>();
        if (input != null && !input.isEmpty()) {
            String[] parts = input.split(", ");
            list.addAll(Arrays.asList(parts));
        }
        return list;
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

    public void fillMap() {
        topicDescriptions.put("Data Structures", "Organize and manage data efficiently in computer science.");
        topicDescriptions.put("Algorithms", "Solve computational problems step by step with efficiency.");
        topicDescriptions.put("Web Development", "Build and maintain websites and web applications.");
        topicDescriptions.put("JavaScript", "Create interactive and dynamic content for web pages.");
        topicDescriptions.put("HTML5", "Structure and present content on the World Wide Web.");
        topicDescriptions.put("CSS3", "Style and layout web pages with enhanced design.");
        topicDescriptions.put("Responsive Web Design", "Design web pages that adapt to various screen sizes.");
        topicDescriptions.put("Backend Development", "Develop server-side logic and databases for web applications.");
        topicDescriptions.put("Node.js", "Use JavaScript runtime to build scalable network applications.");
        topicDescriptions.put("Express.js", "Create web applications and APIs quickly and efficiently.");
        topicDescriptions.put("Database Management Systems", "Organize, store, and retrieve data efficiently.");
        topicDescriptions.put("SQL", "Manage and manipulate relational databases.");
        topicDescriptions.put("NoSQL Databases", "Store and retrieve data using non-relational databases.");
        topicDescriptions.put("Object-Oriented Programming (OOP)", "Model real-world entities with classes and objects.");
        topicDescriptions.put("Version Control Systems (e.g., Git)", "Manage changes to source code efficiently.");
        topicDescriptions.put("Software Development Life Cycle (SDLC)", "Plan, develop, test, deploy, and maintain software.");
        topicDescriptions.put("Agile Methodologies", "Iterative approach to software development.");
        topicDescriptions.put("DevOps", "Integrate development and operations to improve efficiency.");
        topicDescriptions.put("Cloud Computing", "Access computing resources over the Internet.");
        topicDescriptions.put("Cybersecurity", "Protect computer systems, networks, and data from theft or damage.");
    }
}