package ru.AliceTelegramBot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.AliceTelegramBot.models.Grade;
import ru.AliceTelegramBot.models.Photo;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.repositories.GradeRepository;
import ru.AliceTelegramBot.repositories.PhotoRepository;
import ru.AliceTelegramBot.repositories.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserPhotoGradeService {
    private final GradeRepository gradeRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserPhotoGradeService(GradeRepository gradeRepository, PhotoRepository photoRepository, UserRepository userRepository) {
        this.gradeRepository = gradeRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveGrade(Grade grade){
        gradeRepository.save(grade);
    }

    @Transactional
    public void saveGrade(Photo photo, User user, int rate){
        Grade grade = new Grade(rate, user,photo);
        gradeRepository.save(grade);
    }

    @Transactional
    public void savePhoto(Photo photo){
        photoRepository.save(photo);
    }

    @Transactional
    public void saveUser(User user){
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long chatID, User user){
        user.setUserID(chatID);
        userRepository.save(user);
    }

    public List<Grade> getGradesByUser(User user){
        return gradeRepository.findByUser(user);
    }

    public User getUserByID(Long id){
        return userRepository.findByUserID(id).orElse(null);
    }

    public Photo getPhotoByName(String name){
        return photoRepository.findByName(name).orElse(null);
    }

    public String getLastMessageByID(Long id){
        return  userRepository.findByUserID(id).orElse(null).getLastMessage();
    }

    public Long countPhotos(){
        return photoRepository.count();
    }

    public List<String> getPhotosNames(){
        return photoRepository.getAllNames();
    }


}
