package ru.AliceTelegramBot.repositories;

import jakarta.validation.constraints.NotNull;
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

    //select id from photo where id not in (select photo_id from Grade where user_id = 1093937171)
    @Query("select p.id from Photo p where p.id not in (select g.pk.photo.id from Grade g where g.pk.user.userID = ?1)")
    List<Integer> getUnratedPhotosIdsForUser(int userId);

    Optional<Photo> findById(int id);

    List<Photo> findTop5ByOrderByAverageRateDesc();


    Optional<Photo> findByName(String name);
}
