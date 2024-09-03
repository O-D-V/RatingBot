package ru.AliceTelegramBot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "grade")
public class Grade implements Serializable  {

    @EmbeddedId
    private GradeId pk;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;*/

    @Min(value = 1)
    @Max(value = 10)
    @NotNull
    @Column(name = "value")
    private Integer value;

    public Grade() {
    }

    public Grade(Integer value, User user, Photo photo) {
        this.value = value;
        this.pk = new GradeId();
        pk.setPhoto(photo);
        pk.setUser(user);
    }

    public GradeId getPk() {
        return pk;
    }

    public void setPk(GradeId pk) {
        this.pk = pk;
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
        return Objects.equals(pk, grade.pk) && Objects.equals(value, grade.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, value);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "pk=" + pk +
                ", value=" + value +
                '}';
    }
}
