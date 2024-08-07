package ru.AliceTelegramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.AliceTelegramBot.models.Photo;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    @Query("select p.name from Photo p")
    List<String> getAllNames();

    Optional<Photo> findByName(String name);
}
