package com.example.ask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ask.bean.Answer;
import com.example.ask.bean.User;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    TextView register_username,register_password;
    Button confirm;
    String result;//接收返回结果
    String status;
    String msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnuser();
            }
        });
    }

    private void returnuser() {
        if (register_username.getText()!=null&&register_password!=null){
            Intent intent = new Intent();
            /*填写answer信息*/
            User user = new User();
            user.setUsername(register_username.getText().toString());
            user.setPassword(register_password.getText().toString());
            intent.putExtra("registeruser",user);
            setResult(3,intent);
            sendRequest(user);
        }else {
            Toast.makeText(RegisterActivity.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(User user) {
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        FormBody formBody = new FormBody.Builder().add("username",user.getUsername())
                .add("password",user.getPassword()).build();
        final Request request = new Request.Builder().url(UserInfo.url+"/user/new").post(formBody).build();
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
                    e.printStackTrace();
                }
            }
        });
    }

    private void errDispaly(final String s) {//错误提示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayResult(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        status = jsonObject.getString("status");
        msg = jsonObject.getString("msg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status.equals("success")){
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void init() {
        register_username = findViewById(R.id.registername);
        register_password = findViewById(R.id.registerpassword);
        confirm = findViewById(R.id.registerconfirm);
        result = "";
    }
}
