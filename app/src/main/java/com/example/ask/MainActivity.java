package com.example.ask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ask.bean.Answer;
import com.example.ask.bean.Session;
import com.example.ask.bean.User;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button register_button,login_button;
    TextView login_username,login_password;
    String result,status,msg;
    private SharedPreferences sp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在加载布局文件前判断是否登陆过
        sp = getSharedPreferences("main", Context.MODE_PRIVATE);
        if (sp.getBoolean("login",false)){//找不到时返回false
            /*如果sessionid存在，则直接赋值*/
            if (!sp.getString("sessionid","no").equals("no")){
                Session.setSessionId(sp.getString("sessionid","no"));
            }
            startActivity(new Intent(MainActivity.this,MainviewActivity.class));
            finish();
        }


        setContentView(R.layout.activity_main);
        init();
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,RegisterActivity.class),3);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.username = login_username.getText().toString();
                if(login_username.getText().toString()!=null&&login_password.getText().toString()!=null) {
                    sendReq();
                }else {
                    Toast.makeText(MainActivity.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendReq() {
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        FormBody formBody = new FormBody.Builder().add("username",login_username.getText().toString())
                .add("password",login_password.getText().toString()).build();
        final Request request = new Request.Builder().url(UserInfo.url+"/login").post(formBody).build();
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
        JSONObject jsonObject = new JSONObject(result);
        status = jsonObject.getString("status");
        msg = jsonObject.getString("msg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status.equals("success")){
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    // 持久化存储cookie信息
                    Session.setSessionId(cookies.get(0));
                    System.out.println(Session.getSessionId());
                    SharedPreferences.Editor editor = sp.edit();
                    /*若登录成功，则记住状态，下次跳过登录页面*/
                    editor.putBoolean("login",true);
                    editor.putString("username",login_username.getText().toString());
                    editor.putString("sessionid",Session.getSessionId());
                    editor.commit();

                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,MainviewActivity.class));
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, msg+"用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void errDispaly(final String s) {//错误提示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == 3){
            User user = (User) data.getSerializableExtra("registeruser");
            updateData(user);
        }
    }

    private void updateData(User user) {
        login_username.setText(user.getUsername());
        login_password.setText(user.getPassword());
    }

    /*初始化组件*/
    private void init() {
        register_button = findViewById(R.id.register);
        login_button = findViewById(R.id.login);
        login_username = findViewById(R.id.login_userName);
        login_password = findViewById(R.id.password);
    }
}
