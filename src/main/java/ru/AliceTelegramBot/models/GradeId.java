package ru.AliceTelegramBot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GradeId implements Serializable {
    private Long photoID;

    private Long userID;

    public GradeId() {
    }

    public GradeId(Long photoID, Long userID) {
        this.photoID = photoID;
        this.userID = userID;
    }

    public Long getPhotoID() {
        return photoID;
    }

    public void setPhotoID(Long photoID) {
        this.photoID = photoID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeId gradeId = (GradeId) o;
        return Objects.equals(photoID, gradeId.photoID) && Objects.equals(userID, gradeId.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoID, userID);
    }
}
