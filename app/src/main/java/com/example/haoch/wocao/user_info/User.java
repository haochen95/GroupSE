package com.example.haoch.wocao.user_info;

public class User {
    int user_id;
    String user_name, user_email, user_password;

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public int getUser_id() {
        return this.user_id;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public String getUser_password() {
        return this.user_password;
    }
}
