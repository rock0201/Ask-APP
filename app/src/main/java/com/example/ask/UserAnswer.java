package com.example.ask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;
import com.example.ask.utils.ResultUtil;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserAnswer extends AppCompatActivity {
    private TextView userAnsquetitle,userAnsqueusername,userAnsquecontent,userAnsquestiontime,
            userAnswerUsername,userAnswertime;
    private EditText useranswerContent;
    private Button userAnsbutton,userAnsDelete;
    private Question question;
    private Answer answer;
    private String result,status,msg;
    OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
    OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useranswer);
        init();
        initAns();
        initQue();

        initData();//将获得的数据绑定到控件上
        userAnsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useranswerContent!=null) {
                    alertModi();
                }else {
                    Toast.makeText(UserAnswer.this, "回答不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        userAnsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDelete();
            }
        });
    }

    private void alertDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserAnswer.this);
        builder.setTitle("Warning!");
        builder.setMessage("确定删除该回答吗？");
        builder.setPositiveButton("是的，我想好了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAnswer();
            }
        });
        builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void deleteAnswer() {
        final Request request = new Request.Builder().url(UserInfo.url+"/answer/delete/"+answer.getId()).delete().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errDispaly(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                try {
                    disdeleteplayResult(result);
                } catch (JSONException e) {

                }
            }
        });
    }
    private void disdeleteplayResult(String result) throws JSONException {
        org.json.JSONObject jsonObject = new org.json.JSONObject(result);
        status = jsonObject.getString("status");
        msg = jsonObject.getString("msg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status.equals("success")){
                    Toast.makeText(UserAnswer.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(UserAnswer.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void alertModi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserAnswer.this);
        builder.setTitle("Warning!");
        builder.setMessage("确定修改该回答吗？");
        builder.setPositiveButton("是的，我想好了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                answer.setContent(useranswerContent.getText().toString());
                modifyResult();
            }
        });
        builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void modifyResult() {
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        String jsonStr = JSONObject.toJSONString(answer);//json数据.
        //System.out.println(jsonStr);
        RequestBody body = RequestBody.create(jsonType, jsonStr);
        final Request request = new Request.Builder().url(UserInfo.url+"/answer/update").put(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errDispaly(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                try {
                    dismodiplayResult(result);
                } catch (JSONException e) {

                }
            }
        });
    }
    private void errDispaly(final String s) {//错误提示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserAnswer.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dismodiplayResult(String result) throws JSONException {
        org.json.JSONObject jsonObject = new org.json.JSONObject(result);
        status = jsonObject.getString("status");
        msg = jsonObject.getString("msg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status.equals("success")){
                    Toast.makeText(UserAnswer.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(UserAnswer.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showAlert(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new android.app.AlertDialog.Builder(UserAnswer.this)
                        .setTitle("Warning:")
                        .setMessage("您的登录已过期，请重新登录")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(UserAnswer.this, MainActivity.class));
                                finish();
                            }
                        })
                        .create().show();
            }
        });
    }


    private void initData() {
        userAnsquetitle.setText(question.getTitle());
        userAnsqueusername.setText(question.getUsername());
        userAnsquecontent.setText(question.getContent());
        userAnsquestiontime.setText(question.getPublishTime());
        userAnswerUsername.setText(answer.getUsername());
        userAnswertime.setText(answer.getPublishTime());
        useranswerContent.setText(answer.getContent());
      //  useranswerContent.setFocusableInTouchMode(false);//设置不可编辑
    }

    private void initAns() {
        Intent intent = getIntent();
        answer = (Answer) intent.getSerializableExtra("useranswer");
    }

    private void initQue() {
        final Request request = new Request.Builder().url(UserInfo.url+"/question/get/answer/"+ answer.getId()).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errDispaly(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                System.out.println(result);
                displayResult(result);
            }
        });
    }

    private void displayResult(final String result) {
        question = JSON.parseObject(result,Question.class);
       // final Question question = (Question) JSON.parse(result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (question!=null) {
                    userAnsquetitle.setText(question.getTitle());
                    userAnsqueusername.setText(question.getUsername());
                    userAnsquecontent.setText(question.getContent());
                    userAnsquestiontime.setText(question.getPublishTime());
                }
            }
        });
    }

    private void init() {
        userAnsquetitle = findViewById(R.id.userAnsquetitle);
        userAnsqueusername = findViewById(R.id.userAnsqueusername);
        userAnsquecontent = findViewById(R.id.userAnsquecontent);
        userAnsquestiontime = findViewById(R.id.userAnsquestiontime);
        userAnswerUsername = findViewById(R.id.useranswerUsername);
        userAnswertime = findViewById(R.id.useranswertime);
        useranswerContent = findViewById(R.id.useranswerContent);
        userAnsbutton = findViewById(R.id.userAnsbutton);
        userAnsDelete = findViewById(R.id.userAnsDelete);
        question = new Question();
        //answer = new Answer();
    }
}
