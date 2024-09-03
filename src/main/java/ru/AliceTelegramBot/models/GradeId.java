package ru.AliceTelegramBot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GradeId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public GradeId() {
    }

    public GradeId(Photo photo, User user) {
        this.photo = photo;
        this.user = user;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeId gradeId = (GradeId) o;
        return Objects.equals(photo, gradeId.photo) && Objects.equals(user, gradeId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photo, user);
    }
}
