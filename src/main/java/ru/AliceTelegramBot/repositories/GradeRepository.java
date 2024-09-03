package ru.AliceTelegramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.AliceTelegramBot.models.Grade;
import ru.AliceTelegramBot.models.GradeId;
import ru.AliceTelegramBot.models.Photo;
import ru.AliceTelegramBot.models.User;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, GradeId> {
    List<Grade> findByPkUser(User user);
    @Query("select g.value from Grade g where g.pk.photo.id = ?1")
    List<Integer> findAllByPhoto(int p);
    Grade findByPkPhoto(Photo photo);

}
