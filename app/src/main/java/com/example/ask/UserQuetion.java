package com.example.ask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ask.adapters.ResultAdapter;
import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;
import com.example.ask.bean.User;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;
import com.example.ask.utils.ResultUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserQuetion extends AppCompatActivity {
    private TextView userqueTitle,userqueContent,userquetime;
    private RecyclerView recyclerView;
    private Button delete;
    private ResultAdapter resultAdapter;
    private List<Answer> answers = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String result,status,msg;
    private Question question;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userque);
        init();
        getData();
        initresults();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(UserQuetion.this));
        resultAdapter = new ResultAdapter(answers);
        //添加点击事件
        resultAdapter.setmOnItemClickListener(new ResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goresults(position);//进入结果页面
                //Toast.makeText(QwithresultActivity.this, "position:"+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(resultAdapter);

        //下拉刷新的事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreash();
            }
        });
    }

    private void refreash() {
        getNewAns();//刷新列表
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getNewAns() {
        /*
         * 执行更新操作
         * */
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        final Request request = new Request.Builder().url(UserInfo.url+"/answer/get/question/"+question.getId()).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errDispaly(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                try {
                    displayResult(result);
                } catch (JSONException e) {

                }
            }
        });
    }

    private void showAlert(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new android.app.AlertDialog.Builder(UserQuetion.this)
                        .setTitle("Warning:")
                        .setMessage("您的登录已过期，请重新登录")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(UserQuetion.this, MainActivity.class));
                                finish();
                            }
                        })
                        .create().show();
            }
        });
    }

    private void displayResult(final String result) throws JSONException {
        Object object = JSON.parse(result);
        if (object instanceof JSONObject){
            ResultUtil.goLogin();//返回登录页面
            showAlert();
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    answers = JSON.parseArray(result,Answer.class);
                    if (answers!=null) {
                        resultAdapter.setAnswers(answers);//将来要改成answers；
                        recyclerView.setAdapter(resultAdapter);
                    }
                }
            });
        }
    }
    private void errDispaly(final String s) {//错误提示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserQuetion.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goresults(int position){
        Intent intent = new Intent(UserQuetion.this,ResultDetail.class);
        intent.putExtra("answer",  answers.get(position));
        startActivity(intent);
    }
    private void initresults() {
        refreash();
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserQuetion.this);
        builder.setTitle("Warning!");
        builder.setMessage("确定删除该问题吗？");
        builder.setPositiveButton("是的，我想好了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();
            }
        });
        builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void delete() {
        /*
         * 执行更新操作
         * */
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        final Request request = new Request.Builder().url(UserInfo.url+"/question/delete/"+question.getId()).delete().build();
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
                    Toast.makeText(UserQuetion.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(UserQuetion.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("userquestion");
        userqueTitle.setText(question.getTitle());
        userqueContent.setText(question.getContent());
        userquetime.setText(question.getPublishTime());
    }

    private void init() {
        userqueTitle = findViewById(R.id.userquetitle);
        userqueContent = findViewById(R.id.userquecontent);
        userquetime = findViewById(R.id.userquestiontime);
        delete = findViewById(R.id.userdelete);
        recyclerView = findViewById(R.id.userresultres);
        swipeRefreshLayout = findViewById(R.id.userresultRefresh);
    }
}
