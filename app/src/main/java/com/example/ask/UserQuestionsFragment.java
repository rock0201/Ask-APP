package com.example.ask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.ask.adapters.QuestionAdapter;
import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;
import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.OkHttpUtils;
import com.example.ask.utils.ResultUtil;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * 用于展示用户的所有问题
 */
public class UserQuestionsFragment extends Fragment {

    private RecyclerView recyclerView ;
    private QuestionAdapter questionAdapter;
    private List<Question> questions = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout ;
    private String result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_questions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initdata();//初始化数据

        /*设置问题列表*/
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        questionAdapter = new QuestionAdapter(questions);
        //添加点击事件
        questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goresults(position);//进入结果页面
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(questionAdapter);


        //下拉刷新的事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreash();
            }
        });
    }

    private void init() {
        recyclerView = requireActivity().findViewById(R.id.userquesrec);
        swipeRefreshLayout = requireActivity().findViewById(R.id.userquestionRefresh);
    }

    private void initdata() {
        refreash();
    }
    private void goresults(int position){
        Intent intent = new Intent(getActivity(),UserQuetion.class);
        intent.putExtra("userquestion",  questions.get(position));
        startActivity(intent);
    }
    private void refreash() {

        getNewQue();//刷新列表
        swipeRefreshLayout.setRefreshing(false);
    }
    private void getNewQue() {
        /*
         * 执行更新操作
         * */
        /*
         * 使用单例模式*/
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient();
        final Request request = new Request.Builder().url(UserInfo.url+"/question/get/user/"+ResultUtil.getUsername()).get().build();
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
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Warning:")
                        .setMessage("您的登录已过期，请重新登录")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(requireActivity(), MainActivity.class));
                                requireActivity().finish();
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
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    questions = JSON.parseArray(result, Question.class);
                    if (questions!=null) {
                        UserInfo.queSize = questions.size();
                        questionAdapter.setQuestions(questions);//将来要改成answers；
                        recyclerView.setAdapter(questionAdapter);
                    }
                }
            });
        }
    }
    private void errDispaly(final String s) {//错误提示
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireActivity(), "有未知错误"+s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
