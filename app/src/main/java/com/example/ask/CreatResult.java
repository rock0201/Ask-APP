package com.example.ask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;
import com.example.ask.utils.ResultUtil;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
* 创建回答页面*/
public class CreatResult extends AppCompatActivity {
    private EditText creatAnscontent;
    private Button creatAnsconfir;
    private int qid;
    private Answer answer = new Answer();
    private String result,status,msg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creatanswer);
        init();
        creatAnsconfir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult();
            }
        });

    }

    private void returnResult() {
        if (creatAnscontent.getText()!=null){
            /*填写answer信息*/
            answer.setContent(creatAnscontent.getText().toString());
            answer.setQid(qid);
            answer.setUsername(ResultUtil.getUsername());
            sendReq();
        }else {
            Toast.makeText(CreatResult.this, "回答不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendReq() {
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        String jsonStr = JSONObject.toJSONString(answer);//json数据.
        //System.out.println(jsonStr);
        RequestBody body = RequestBody.create(jsonType, jsonStr);
        final Request request = new Request.Builder().url(UserInfo.url+"/answer/new").post(body).build();
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
                    Toast.makeText(CreatResult.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(CreatResult.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void errDispaly(final String s) {//错误提示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreatResult.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void init() {
        creatAnscontent = findViewById(R.id.creatanscontent);
        creatAnsconfir = findViewById(R.id.creatansconfir);
        Intent intent = getIntent();
        qid = intent.getIntExtra("qid",1);
    }
}
