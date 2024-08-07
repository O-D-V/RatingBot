package ru.AliceTelegramBot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "grade")
public class Grade {

    @EmbeddedId
    private GradeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")

    private User user;

    @Min(value = 1)
    @Max(value = 10)
    @NotNull
    @Column(name = "value")
    private Integer value;

    public Grade() {
    }

    public Grade(Integer value, User user, Photo photo) {
        this.photo = photo;
        this.user = user;
        this.value = value;
    }

    public GradeId getId() {
        return id;
    }

    public void setId(GradeId id) {
        this.id = id;
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

    public @Min(value = 1) @Max(value = 10) @NotNull Integer getValue() {
        return value;
    }

    public void setValue(@Min(value = 1) @Max(value = 10) @NotNull Integer value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(id, grade.id) && Objects.equals(value, grade.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", photo=" + photo +
                ", user=" + user +
                ", value=" + value +
                '}';
    }
}
