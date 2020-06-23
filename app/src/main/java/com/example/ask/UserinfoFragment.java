 package com.example.ask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ask.staticinfo.UserInfo;
import com.example.ask.utils.ResultUtil;


 /**
 * A simple {@link Fragment} subclass.
 */
public class UserinfoFragment extends Fragment {


    private TextView usernameInfo,queSize,ansSize;
    private SwipeRefreshLayout refreshLayout;
    private Button logout;
    private String res,que,name;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_userinfo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert();
            }
        });
    }

     private void alert() {
         new android.app.AlertDialog.Builder(requireActivity())
                 .setTitle("Warning:")
                 .setMessage("确定要注销吗？")
                 .setPositiveButton("ok,我想好了", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         ResultUtil.goLogin();
                         startActivity(new Intent(requireActivity(), MainActivity.class));
                         requireActivity().finish();
                     }
                 })
                 .setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                 })
                 .create().show();
     }

     private void refresh() {
         initData();
         refreshLayout.setRefreshing(false);
     }

     private void initData() {
         usernameInfo.setText(name+ ResultUtil.getUsername() );
         queSize.setText(res+UserInfo.queSize);
         ansSize.setText(que+UserInfo.ansSize);
     }

     private void init() {
         usernameInfo = requireActivity().findViewById(R.id.infousername);
         queSize = requireActivity().findViewById(R.id.quesize);
         ansSize= requireActivity().findViewById(R.id.anssize);
         refreshLayout = requireActivity().findViewById(R.id.userinfoRefresh);
         logout = requireActivity().findViewById(R.id.logout);
         res = queSize.getText()+"";
         que = ansSize.getText()+"";
         name = usernameInfo.getText()+"";
     }
 }
