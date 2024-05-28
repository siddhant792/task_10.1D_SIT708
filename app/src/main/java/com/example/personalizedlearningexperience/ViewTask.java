package com.example.personalizedlearningexperience;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.android.volley.toolbox.JsonObjectRequest;

public class ViewTask extends AppCompatActivity implements OnRadioButtonClickListener{

    TextView tv_task_title, tv_task_description;
    private RequestQueue requestQueue;
    HashMap<String, List<String>> questionsAndOptions;
    HashMap<String, String> questionsAndCorrectAnswers;
    String desc;
    RecyclerView recyclerViewQuestions;
    Button buttonSubmitQues;

    HashMap<String, String> checkedOptions;

    DBHelper dbHelper;

    List<List<String>> convertedList;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_task);
        tv_task_title = findViewById(R.id.tv_task_title);
        tv_task_description = findViewById(R.id.tv_task_description);
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestions);
        buttonSubmitQues = findViewById(R.id.buttonSubmitQues);
        checkedOptions = new LinkedHashMap<>();
        dbHelper = new DBHelper(this);
        questionsAndOptions = new LinkedHashMap<>();
        questionsAndCorrectAnswers = new LinkedHashMap<>();
        String title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        desc = getIntent().getStringExtra("desc");
        tv_task_title.setText(title);
        tv_task_description.setText(desc);
        requestQueue = Volley.newRequestQueue(this);

//        getQuizData("http://10.0.2.2:5000/getQuiz?topic=" + encodeString(title));
        mockData();
        convertedList = getConvertedList();
        QuestionAdapter questionAdapter = new QuestionAdapter(convertedList, this);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuestions.setAdapter(questionAdapter);
        buttonSubmitQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkCompletion()) {
                    Toast.makeText(ViewTask.this, "Please attempt all the questions", Toast.LENGTH_SHORT).show();
                }else {
                    String[] quesAns = new String[questionsAndCorrectAnswers.size()];
                    int i = 0;
                    for (Map.Entry<String, String> entry : questionsAndCorrectAnswers.entrySet()) {
                        quesAns[i++] = entry.getKey() + "@" + entry.getValue();
                    }
                    // saving data to db
                    makeQuestionHistory();
                    Intent intent = new Intent(ViewTask.this, ViewResult.class);
                    intent.putExtra("id", id);
                    intent.putExtra("ques", quesAns);
                    startActivity(intent);
                }
            }
        });
    }

    public void makeQuestionHistory() {
        System.out.println(checkedOptions);
        for (Map.Entry<String, String> entry : questionsAndCorrectAnswers.entrySet()) {
            String ques = entry.getKey();
            String correctAns = entry.getValue();
            String userAns = checkedOptions.get(ques);
            String o1 = questionsAndOptions.get(ques).get(0);
            String o2 = questionsAndOptions.get(ques).get(1);
            String o3 = questionsAndOptions.get(ques).get(2);
            String o4 = questionsAndOptions.get(ques).get(3);
            long saved = insertHistory(ques, o1, o2, o3, o4, correctAns, userAns);
        }
    }

    public long insertHistory(String question, String answer1, String answer2, String answer3, String answer4, String correct, String userAnswered) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question);
        values.put("answer1", answer1);
        values.put("answer2", answer2);
        values.put("answer3", answer3);
        values.put("answer4", answer4);
        values.put("correct", correct);
        values.put("userAnswered", userAnswered);
        return db.insert("history", null, values);
    }

    public boolean checkCompletion() {
        for (Map.Entry<String, String> entry : questionsAndCorrectAnswers.entrySet()) {
            if(entry.getValue().isEmpty()) return false;
        }
        return true;
    }

    public List<List<String>> getConvertedList() {
        List<List<String>> combinedList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : questionsAndOptions.entrySet()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(entry.getKey());
            tempList.addAll(entry.getValue());
            combinedList.add(tempList);
        }
        return combinedList;
    }

    @Override
    public void onRadioButtonClicked(int position, String radioButtonLabel) {
        checkedOptions.put(convertedList.get(position).get(0), radioButtonLabel);
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.TaskViewHolder> {

        List<List<String>> questionsAndOptions;
        private OnRadioButtonClickListener radioButtonClickListener;

        public QuestionAdapter(List<List<String>> questionsAndOptions, OnRadioButtonClickListener radioButtonClickListener) {
            this.questionsAndOptions = questionsAndOptions;
            this.radioButtonClickListener = radioButtonClickListener;
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

                rd_gp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = group.findViewById(checkedId);
                        if (radioButton != null) {
                            String radioButtonLabel = radioButton.getText().toString();
                            radioButtonClickListener.onRadioButtonClicked(getAdapterPosition(), radioButtonLabel);
                        }
                    }
                });
            }

            public void bind( List<String> questions) {
                int pos = getAdapterPosition() + 1;
                tv_task_title.setText(pos + ". " + questions.get(0));
                rd_1.setText(questions.get(1));
                rd_2.setText(questions.get(2));
                rd_3.setText(questions.get(3));
                rd_4.setText(questions.get(4));
            }
        }
    }

    public static String encodeString(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mockData() {
        // Fourth question on Data Structures
        List<String> options1DS = new ArrayList<>();
        options1DS.add("A linked list is a linear data structure.");
        options1DS.add("A linked list consists of a sequence of nodes, where each node contains data and a reference to the next node in the sequence.");
        options1DS.add("In a singly linked list, each node has a reference only to the next node in the sequence.");
        options1DS.add("In a doubly linked list, each node has references to both the next and previous nodes in the sequence.");
        questionsAndOptions.put("What is a linked list?", options1DS);
        questionsAndCorrectAnswers.put("What is a linked list?", "A linked list consists of a sequence of nodes, where each node contains data and a reference to the next node in the sequence.");

        // Fifth question on Data Structures
        List<String> options2DS = new ArrayList<>();
        options2DS.add("A stack is a data structure that follows the Last In, First Out (LIFO) principle.");
        options2DS.add("A stack supports two main operations: push (to add an element) and pop (to remove the top element).");
        options2DS.add("In a stack, elements are added and removed from the same end, known as the top of the stack.");
        options2DS.add("Stacks are commonly used in recursive algorithms and expression evaluation.");
        questionsAndOptions.put("What is a stack?", options2DS);
        questionsAndCorrectAnswers.put("What is a stack?", "A stack is a data structure that follows the Last In, First Out (LIFO) principle.");

        // Sixth question on Algorithms
        List<String> options1Algo = new ArrayList<>();
        options1Algo.add("An algorithm is a step-by-step procedure or set of rules for solving a problem.");
        options1Algo.add("Algorithms can be expressed in natural language, pseudocode, or programming language.");
        options1Algo.add("Algorithm complexity analysis measures the performance of an algorithm in terms of time and space.");
        options1Algo.add("Common algorithm design techniques include greedy algorithms, dynamic programming, and divide and conquer.");
        questionsAndOptions.put("What is an algorithm?", options1Algo);
        questionsAndCorrectAnswers.put("What is an algorithm?", "An algorithm is a step-by-step procedure or set of rules for solving a problem.");
    }


    public void getQuizData(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray quizArray = response.getJSONArray("quiz");

                            for (int i = 0; i < quizArray.length(); i++) {
                                JSONObject quizObject = quizArray.getJSONObject(i);
                                String question = quizObject.getString("question");
                                char correctAnswer = quizObject.getString("correct_answer").charAt(0);
                                JSONArray optionsArray = quizObject.getJSONArray("options");

                                List<String> options = new ArrayList<>();
                                for (int j = 0; j < optionsArray.length(); j++) {
                                    options.add(optionsArray.getString(j));
                                }

                                questionsAndOptions.put(question, options);
                                questionsAndCorrectAnswers.put(question, options.get(correctAnswer - 'A'));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}