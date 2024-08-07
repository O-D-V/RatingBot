package ru.AliceTelegramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.AliceTelegramBot.models.Grade;
import ru.AliceTelegramBot.models.GradeId;
import ru.AliceTelegramBot.models.User;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, GradeId> {
    List<Grade> findByUser(User user);
}
