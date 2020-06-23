package com.example.ask.utils;

import com.example.ask.bean.Session;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {
    /**
     * 单例模式
     */
    private static OkHttpUtils okHttpUtils = null;

    private OkHttpUtils() {
    }

    public static OkHttpUtils getInstance() {
        //双层判断，同步锁
        if (okHttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpUtils == null) {
                    okHttpUtils = new OkHttpUtils();
                }
            }
        }
        return okHttpUtils;
    }

    /**
     * 单例模式
     * 封装OkhHttp
     * synchronized同步方法
     */
    private static OkHttpClient okHttpClient = null;

    public static synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            if (Session.getSessionId()!=null) {
                /*
                 * 为所有的请求添加请求头*/
                okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder().
                                addHeader("cookie", Session.getSessionId()).build();
                        return chain.proceed(newRequest);
                    }
                }).build();
            }else {
                okHttpClient = new OkHttpClient();
            }
        }
        return okHttpClient;
    }

    /*普通的get请求*/
    public static void sendGetRequest(String address, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).addHeader("cookie", Session.getSessionId()).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendPostRequest(String address, String jsonString, Callback callback) {
        /*用json来请求*/
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        /*请求体*/
        RequestBody body = RequestBody.create(jsonType, jsonString);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).post(body).build();
    }

}
