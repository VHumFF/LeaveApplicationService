package com.dcom.dataModel;

public class UserSessionInfo {
    private int userId;
    private String userType;

    public UserSessionInfo(int userId, String userType){
        this.userId = userId;
        this.userType = userType;
    }

    public int getUserId(){
        return userId;
    }

    public String getUserType(){
        return userType;
    }
}
