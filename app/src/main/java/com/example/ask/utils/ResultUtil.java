package com.example.ask.utils;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/*如果返回结果码里面有300，则代表登录过期，需要重新登录*/
public class ResultUtil {
    public static SharedPreferences sp = MyApplication.getContext().getSharedPreferences("main", Context.MODE_PRIVATE);
    public static void goLogin()  {
            /*先使跳过登录失效*/

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("login",false);
            editor.commit();
    }
    public static String getUsername(){
        return sp.getString("username","");
    }
}
