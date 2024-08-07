package ru.AliceTelegramBot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "url")
    @NotNull
    @NotBlank
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "average_rate")
    @NotNull
    private Float averageRate;

    @OneToMany(mappedBy = "photo")
    private List<Grade> grades;

    public Photo(String url, Float averageRate) {
        this.url = url;
        this.averageRate = averageRate;
    }

    public Photo(String url, Float averageRate, String name,List<Grade> grades) {
        this.url = url;
        this.averageRate = averageRate;
        this.name = name;
        this.grades = grades;
    }

    public Photo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public @NotNull @NotBlank String getUrl() {
        return url;
    }

    public void setUrl(@NotNull @NotBlank String url) {
        this.url = url;
    }

    public @NotNull Float getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(@NotNull Float averageRate) {
        this.averageRate = averageRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id == photo.id && Objects.equals(url, photo.url) && Objects.equals(averageRate, photo.averageRate) && Objects.equals(grades, photo.grades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, averageRate, grades);
    }
}
