package com.example.ask.bean;

public class Session {
    static String sessionId;

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {

        Session.sessionId = sessionId;
    }
}
