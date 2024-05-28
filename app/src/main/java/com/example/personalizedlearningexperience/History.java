package com.example.personalizedlearningexperience;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity {

    RecyclerView recyclerViewQuestions;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        dbHelper = new DBHelper(this);
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestions);
        List<List<String>> list = getAllHistory();
        System.out.println(list);
        QuestionAdapter questionAdapter = new QuestionAdapter(list);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuestions.setAdapter(questionAdapter);
    }

    public List<List<String>> getAllHistory() {
        List<List<String>> historyList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM history", null);

        if (cursor.moveToFirst()) {
            do {
                List<String> historyRow = new ArrayList<>();
                historyRow.add(cursor.getString(cursor.getColumnIndex("question")));
                historyRow.add(cursor.getString(cursor.getColumnIndex("answer1")));
                historyRow.add(cursor.getString(cursor.getColumnIndex("answer2")));
                historyRow.add(cursor.getString(cursor.getColumnIndex("answer3")));
                historyRow.add(cursor.getString(cursor.getColumnIndex("answer4")));
                historyRow.add(cursor.getString(cursor.getColumnIndex("correct")));
                historyRow.add(cursor.getString(cursor.getColumnIndex("userAnswered")));
                historyList.add(historyRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.TaskViewHolder> {

        List<List<String>> questionsAndOptions;

        public QuestionAdapter(List<List<String>> questionsAndOptions) {
            this.questionsAndOptions = questionsAndOptions;
        }

        @NonNull
        @Override
        public QuestionAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
            return new QuestionAdapter.TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionAdapter.TaskViewHolder holder, int position) {
            List<String> questions = questionsAndOptions.get(position);
            holder.bind(questions);
        }

        @Override
        public int getItemCount() {
            return questionsAndOptions.size();
        }

        public class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView tv_task_title;
            RadioButton rd_1, rd_2, rd_3, rd_4;
            RadioGroup rd_gp;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_task_title = itemView.findViewById(R.id.tv_task_title);
                rd_1 = itemView.findViewById(R.id.rd_1);
                rd_2 = itemView.findViewById(R.id.rd_2);
                rd_3 = itemView.findViewById(R.id.rd_3);
                rd_4 = itemView.findViewById(R.id.rd_4);
                rd_gp = itemView.findViewById(R.id.rd_gp);
            }

            public void bind( List<String> questions) {
                int pos = getAdapterPosition() + 1;
                tv_task_title.setText(pos + ". " + questions.get(0));
                rd_1.setText(questions.get(1));
                rd_2.setText(questions.get(2));
                rd_3.setText(questions.get(3));
                rd_4.setText(questions.get(4));
                if(questions.get(1).equals(questions.get(5))) {
                    rd_1.setTextColor(Color.GREEN);
                    if(questions.get(5).equals(questions.get(6))) {
                        rd_1.setText(questions.get(1) + " (You answered correctly)");
                    }else {
                        rd_1.setText(questions.get(1) + " (Correct Answer)");
                    }
                }else if(questions.get(2).equals(questions.get(5))) {
                    rd_2.setTextColor(Color.GREEN);
                    if(questions.get(5).equals(questions.get(6))) {
                        rd_2.setText(questions.get(2) + " (You answered correctly)");
                    }else {
                        rd_2.setText(questions.get(2) + " (Correct Answer)");
                    }
                }else if(questions.get(3).equals(questions.get(5))) {
                    rd_3.setTextColor(Color.GREEN);
                    if(questions.get(5).equals(questions.get(6))) {
                        rd_3.setText(questions.get(3) + " (You answered correctly)");
                    }else {
                        rd_3.setText(questions.get(3) + " (Correct Answer)");
                    }
                }else if(questions.get(4).equals(questions.get(5))) {
                    rd_4.setTextColor(Color.GREEN);
                    if(questions.get(5).equals(questions.get(6))) {
                        rd_4.setText(questions.get(4) + " (You answered correctly)");
                    }else {
                        rd_4.setText(questions.get(4) + " (Correct Answer)");
                    }
                }

                if(!questions.get(5).equals(questions.get(6))) {
                    if(questions.get(1).equals(questions.get(6))) {
                        rd_1.setTextColor(Color.RED);
                        rd_1.setText(questions.get(1) + " (Your answer)");
                    }else if(questions.get(2).equals(questions.get(6))) {
                        rd_2.setTextColor(Color.RED);
                        rd_2.setText(questions.get(2) + " (Your answer)");
                    }else if(questions.get(3).equals(questions.get(6))) {
                        rd_2.setTextColor(Color.RED);
                        rd_2.setText(questions.get(3) + " (Your answer)");
                    }else if(questions.get(4).equals(questions.get(6))) {
                        rd_2.setTextColor(Color.RED);
                        rd_2.setText(questions.get(4) + " (Your answer)");
                    }
                }
            }
        }
    }

}