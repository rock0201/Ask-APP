package com.example.ask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.ask.bean.Question;
import com.example.ask.bean.Session;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;
import com.example.ask.utils.ResultUtil;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
* 用于创建问题*/
public class CreatQuestion extends AppCompatActivity {
    private TextView creatQueTitle,creatQueContent;
    private Button creatQueConfir;
    String status,msg,result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creatquestion);
        init();
        returnData();
    }

    private void returnData() {
        creatQueConfir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creatQueTitle.getText()!=null&&creatQueContent!=null){
                    /*填写question信息*/
                    Question question = new Question();
                    question.setTitle(creatQueTitle.getText().toString());
                    question.setUsername(UserInfo.username);
                    question.setContent(creatQueContent.getText().toString());
                    question.setUsername(ResultUtil.getUsername());
                    goReq(question);
                }else {
                    Toast.makeText(CreatQuestion.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goReq(Question question) {
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        String jsonStr = JSONObject.toJSONString(question);//json数据.
        //System.out.println(jsonStr);
        RequestBody body = RequestBody.create(jsonType, jsonStr);
       /* FormBody formBody = new FormBody.Builder().add("content",question.getContent())
                .add("title",question.getTitle())
                .add("username",question.getUsername()).build();*/
        final Request request = new Request.Builder().url(UserInfo.url+"/question/new").post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errDispaly(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                try {
                    displayResult(result,response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void displayResult(String result, final Response response) throws JSONException {
        org.json.JSONObject jsonObject = new org.json.JSONObject(result);
        status = jsonObject.getString("status");
        msg = jsonObject.getString("msg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status.equals("success")){
                    Toast.makeText(CreatQuestion.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(CreatQuestion.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void errDispaly(final String s) {//错误提示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreatQuestion.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        creatQueTitle = findViewById(R.id.creatquetitle);
        creatQueContent = findViewById(R.id.creatquecontent);
        creatQueConfir = findViewById(R.id.creatqueconfir);
    }
}
