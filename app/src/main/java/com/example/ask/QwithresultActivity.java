package com.example.ask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ask.adapters.QuestionAdapter;
import com.example.ask.adapters.ResultAdapter;
import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*用于展示问题所对应的回答页面*/
public class QwithresultActivity extends AppCompatActivity {
    private TextView qtitle,qusername,qcontent,qtime;
    private RecyclerView recyclerView;
    private ResultAdapter resultAdapter;
    private List<Answer> answers = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton addResult;
    private Question question;
    String result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwithresult);
        init();
        initque();
        initresults();
        resultAdapter = new ResultAdapter(answers);
        recyclerView.setLayoutManager(new LinearLayoutManager(QwithresultActivity.this));
        //添加点击事件
        resultAdapter.setmOnItemClickListener(new ResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goresults(position);//进入结果页面
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(resultAdapter);


        //添加回答
        addResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatResult();
            }
        });

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

    private void creatResult() {
        Intent intent = new Intent(QwithresultActivity.this,CreatResult.class);
        intent.putExtra("qid",question.getId());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 2){
            Answer answer = (Answer) data.getSerializableExtra("createanswer");
            updateData(answer);
        }
    }

    private void updateData(Answer answer) {
        answers.add(answer);
        getNewAns();//更新列表
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
            new AlertDialog.Builder(QwithresultActivity.this)
                    .setTitle("Warning:")
                    .setMessage("您的登录已过期，请重新登录")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(QwithresultActivity.this, MainActivity.class));
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
                Toast.makeText(QwithresultActivity.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initresults() {
        refreash();
    }

    private void initque() {
        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("question");
        System.out.println(question.getTitle());
        qtitle.setText(question.getTitle());
        qusername.setText(question.getUsername());
        qcontent.setText(question.getContent());
        qtime.setText(question.getPublishTime());
    }

    private void init() {
        qtitle = (TextView) findViewById(R.id.quetitle);
        qusername = (TextView)findViewById(R.id.queusername);
        qcontent =(TextView) findViewById(R.id.quecontent);
        qtime = findViewById(R.id.questiontime);
        recyclerView = findViewById(R.id.resultres);
        swipeRefreshLayout = findViewById(R.id.resultRefresh);
        addResult = findViewById(R.id.addResult);
    }
    private void goresults(int position){
        Intent intent = new Intent(QwithresultActivity.this,ResultDetail.class);
        intent.putExtra("answer",  answers.get(position));
        startActivity(intent);
    }
}
