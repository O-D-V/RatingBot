package ru.AliceTelegramBot.models;

public class User {
    private Long userID;
    private boolean isAdmin;

    public User(Long userID, boolean isAdmin) {
        this.userID = userID;
        this.isAdmin = isAdmin;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
