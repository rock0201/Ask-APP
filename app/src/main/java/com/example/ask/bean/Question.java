package com.example.ask.bean;

import java.io.Serializable;

public class Question implements Serializable {
    private int id;
    private String title;
    private String publishTime;
    private int uid;
    private String username;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", uid=" + uid +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
