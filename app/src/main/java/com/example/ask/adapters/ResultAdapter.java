package com.example.ask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ask.R;
import com.example.ask.bean.Answer;
import com.example.ask.bean.Question;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHoler> {

    List<Answer> answers = new ArrayList<>();

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public ResultAdapter(List<Answer> answers) {
        this.answers = answers;
    }
    /*添加点击事件*/
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private ResultAdapter.OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(ResultAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.result_card,parent,false);
        final ResultAdapter.MyViewHoler myViewHoler = new ResultAdapter.MyViewHoler(itemView);
        return myViewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHoler holder, int position) {
        Answer answer = answers.get(position);
        holder.itemView.setTag(R.id.idforanswer,answer);
        holder.resultUsername.setText(answer.getUsername());
        holder.resultTime.setText(answer.getPublishTime());
        holder.resultContent.setText(
                answer.getContent().length()>48?answer.getContent().substring(0,48)+"...":
                        answer.getContent());
        //绑定监听事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    static class MyViewHoler extends RecyclerView.ViewHolder{

        private TextView resultUsername, resultTime, resultContent;
        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            resultUsername = itemView.findViewById(R.id.resultUsername);
            resultTime = itemView.findViewById(R.id.resultPublishtime);
            resultContent = itemView.findViewById(R.id.resultContent);
        }
    }
}
