package com.example.personalizedlearningexperience;

import android.content.Intent;
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

public class ViewResult extends AppCompatActivity {

    RecyclerView recyclerViewResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        String id = getIntent().getStringExtra("id");
        setContentView(R.layout.activity_view_result);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);
        String[] ques = getIntent().getStringArrayExtra("ques");
        findViewById(R.id.buttonBackToMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewResult.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        List<String[]> list = new ArrayList<>();
        int i = 1;
        for (String que : ques) {
            String[] sp = que.split("@", 2);
            list.add(new String[]{i + ". " + sp[0], sp[1]});
            i++;
        }
        ResultAdapter questionAdapter = new ResultAdapter(list);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(questionAdapter);
    }

    private class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.TaskViewHolder> {

        List<String[]> questionsAnswers;

        public ResultAdapter(List<String[]> questionsAnswers) {
            this.questionsAnswers = questionsAnswers;
        }

        @NonNull
        @Override
        public ResultAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
            return new ResultAdapter.TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultAdapter.TaskViewHolder holder, int position) {
            String[] questions = questionsAnswers.get(position);
            holder.bind(questions);
        }

        @Override
        public int getItemCount() {
            return questionsAnswers.size();
        }

        public class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView tv_question, tv_answer;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_question = itemView.findViewById(R.id.tv_question);
                tv_answer = itemView.findViewById(R.id.tv_answer);
            }

            public void bind(String[] questions) {
                tv_question.setText(questions[0]);
                tv_answer.setText(questions[1]);
            }
        }
    }
}