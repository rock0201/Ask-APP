package com.example.ask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ask.R;
import com.example.ask.bean.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHoler>{

    List<Question> questions = new ArrayList<>();

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }
/*添加点击事件*/
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private QuestionAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(QuestionAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    static class MyViewHoler extends RecyclerView.ViewHolder{

        private TextView questionTitle,questionUsername,questionContent;
        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.questionTitle);
            questionUsername = itemView.findViewById(R.id.questionUsername);
            questionContent = itemView.findViewById(R.id.questionContent);
        }
    }

    @NonNull
    @Override
    public QuestionAdapter.MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.qusetion_card,parent,false);
        final MyViewHoler myViewHoler = new MyViewHoler(itemView);
        return myViewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionAdapter.MyViewHoler holder, int position) {
        Question question = questions.get(position);
        holder.itemView.setTag(R.id.idforquestion,question);
        holder.questionTitle.setText(question.getTitle());
        holder.questionUsername.setText(question.getUsername());
        holder.questionContent.setText(
                question.getContent().length()>48?question.getContent().substring(0,48)+"...":
                question.getContent());
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
        return questions.size();
    }
}
