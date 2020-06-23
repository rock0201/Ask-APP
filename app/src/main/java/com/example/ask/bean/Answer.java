package com.example.ask.bean;

import java.io.Serializable;

public class Answer implements Serializable {
    private int id;
    private String publishTime;
    private int uid;
    private String username;
    private int qid;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", publishTime='" + publishTime + '\'' +
                ", uid=" + uid +
                ", username='" + username + '\'' +
                ", qid=" + qid +
                ", content='" + content + '\'' +
                '}';
    }
}
