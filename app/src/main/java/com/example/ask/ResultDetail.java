package com.example.ask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;

public class ResultDetail extends AppCompatActivity {
    private  TextView answerUsername,answerContent,answerTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultdetail);
        init();
        initdata();
    }

    private void initdata() {
        Intent intent = getIntent();
        Answer answer = (Answer) intent.getSerializableExtra("answer");
        answerUsername.setText(answer.getUsername());
        answerContent.setText(answer.getContent());
        answerTime.setText(answer.getPublishTime());
    }

    private void init() {
        answerUsername = findViewById(R.id.answerUsername);
        answerContent = findViewById(R.id.answerContent);
        answerTime = findViewById(R.id.answertime);
    }
}
