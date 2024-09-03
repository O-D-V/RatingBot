package ru.AliceTelegramBot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_table")
public class User {
    @Id
    @Column(name = "user_id")
    private Long userID;

    @Column(name = "name")
    private String name;

    @Column(name = "last_message")
    private String lastMessage;

    @NotNull
    @Column(name = "is_admin")
    private Boolean isAdmin;

    @OneToMany(mappedBy = "pk.user")
    private List<Grade> grades;

    public User() {
    }

    public User(Long userID, Boolean isAdmin) {
        this.userID = userID;
        this.isAdmin = isAdmin;
    }

    public User(Long userID, String name, Boolean isAdmin, List<Grade> grades) {
        this.userID = userID;
        this.name = name;
        this.isAdmin = isAdmin;
        this.grades = grades;
    }

    public User(Boolean isAdmin, List<Grade> grades) {
        this.isAdmin = isAdmin;
        this.grades = grades;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public @NotNull Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(@NotNull Boolean admin) {
        isAdmin = admin;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userID, user.userID) && Objects.equals(isAdmin, user.isAdmin) && Objects.equals(grades, user.grades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, isAdmin, grades);
    }
}
