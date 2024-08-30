package ru.AliceTelegramBot.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class TempUserData {

    private HashMap<Long, String> photoName;
    private HashMap<Long, String> photoUrl;

    public TempUserData() {
        photoName = new HashMap<>();
        photoUrl = new HashMap<>();
    }

    public String getPhotoNameByUserID(Long userID) {
        return photoName.get(userID);
    }

    public List<String> getAllPhotoNames(){
        if(photoName.isEmpty()) return null;
        photoName.forEach((key, value) -> System.out.println(key + " : " +value));
        return photoName.keySet().stream().map(Object::toString).toList();
    }

    public void setPhotoNameForUser(Long userID, String photoName) {
        this.photoName.put(userID, photoName);
    }

    public String getPhotoUrlByUserID(Long userID) {
        return photoUrl.get(userID);
    }

    public void setPhotoUrl(Long userID,String photoUrl) {
        this.photoUrl.put(userID,photoUrl);
    }
}
