package ru.AliceTelegramBot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.AliceTelegramBot.models.Photo;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.services.UserPhotoGradeService;
import ru.AliceTelegramBot.util.BotApiMethodContainer;
import ru.AliceTelegramBot.util.Constants;
import ru.AliceTelegramBot.util.TempUserData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Component
public class TextMessageHandler {
    private final UserPhotoGradeService userPhotoGradeService;
    private  final TempUserData tempUserData;

    @Autowired
    public TextMessageHandler(UserPhotoGradeService userPhotoGradeService, TempUserData tempUserData) {
        this.userPhotoGradeService = userPhotoGradeService;
        this.tempUserData = tempUserData;
    }

    public List<BotApiMethodContainer> processTextMessage(Update update){
        final Long chatId = update.getMessage().getChatId();
        final String messageText = update.getMessage().getText();


        switch (messageText) {
            case "/start":
                return startCommandReceived(chatId.toString(), update.getMessage().getChat().getFirstName());
            case "/helloKitty":
               return Collections.singletonList(new BotApiMethodContainer(sendPhoto(chatId.toString(), "Photo", "photo//HelloKitty.jpg")));

            case "/newPhoto": {
                //
                User user = userPhotoGradeService.getUserByID(chatId);
                user.setLastMessage("/newPhoto");
                userPhotoGradeService.updateUser(chatId, user);
                return sendMessage(chatId.toString(), "Запрос принят. Отправьте фото и подпись к фото");
            }
            case "/showPhoto": {
                //
                User user = userPhotoGradeService.getUserByID(chatId);
                user.setLastMessage("/showPhoto");
                userPhotoGradeService.updateUser(chatId, user);
            }
                break;
            case "/showPhotoNames":
            {
                //отправляет в ответе список имен фото
                StringBuilder res = new StringBuilder();
                List<String> list = userPhotoGradeService.getPhotosNames();
                for(String l:list) res.append(l).append("/n");
                if(res.isEmpty()) res.append("Ничего не найдено");
                return sendMessage(chatId.toString(), res.toString());
            }
            default:
                switch (userPhotoGradeService.getUserByID(chatId).getLastMessage()){
                    case "/newPhoto":
                    {
                        //Обрабатывается подпись к новому фото.
                        savePhotoName(chatId.toString(),messageText);
                        if(isPhotoObjectCompleted(chatId.toString())){
                            savePhoto(chatId.toString());
                            return  sendMessage(chatId.toString(), "Добавлено");
                        }
                        else return  sendMessage(chatId.toString(), "Отправьте фото");
                    }
                    case"/showPhoto":
                        //Отправляет фото с отправленным пользователем именем
                        Photo photo = userPhotoGradeService.getPhotoByName(messageText);
                        if(photo == null) return  sendMessage(chatId.toString(), "Нет фото с таким именем");
                        return Collections.singletonList(new BotApiMethodContainer(sendPhoto(Long.toString(chatId), photo.getName(), photo.getUrl())));
                }

                return  sendMessage(chatId.toString(), Constants.NO_SUCH_COMMAND_ERROR);
        }
        return  sendMessage(chatId.toString(), Constants.SUCCESS_MESSAGE);
    }

    private void savePhotoName(String userId, String photoName){
        tempUserData.setPhotoNameForUser(Long.getLong(userId), photoName);
    }

    //Сохраняет подпись и путь к фото
    private void savePhoto(String userID){
            Photo photo = new Photo(tempUserData.getPhotoUrlByUserID(Long.getLong(userID)), 0F, tempUserData.getPhotoNameByUserID(Long.getLong(userID)), null);
            userPhotoGradeService.savePhoto(photo);
    }

    //Проверяет, готово ли фото для сохранения в БД (объект фото имеет путь и подпись)
    public boolean isPhotoObjectCompleted(String userID){
        Long userIDLong = Long.getLong(userID);
        return tempUserData.getPhotoNameByUserID(userIDLong) != null && tempUserData.getPhotoUrlByUserID(userIDLong) != null;
    }

    //Обрабатывает команду /start
    private List<BotApiMethodContainer> startCommandReceived(String chatId, String name) {
        String answer = Constants.getWelcomeMessage(name);
        return sendMessage(chatId, answer);
    }

    //Возвращает метод бота для отправки текстового сообщения
    private List<BotApiMethodContainer> sendMessage(String chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
            return Collections.singletonList(new BotApiMethodContainer(sendMessage));
    }

    //Возвращает метод бота для отправки фото
    public SendPhoto sendPhoto(String chatId, String caption, String path){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Path pa = Paths.get(path);
        String filename = pa.getFileName().toString();
        InputFile photo = new InputFile();//new InputFile("https://upload.wikimedia.org/wikipedia/en/0/05/Hello_kitty_character_portrait.png ");
        photo.setMedia(inputStream,filename);
        sendPhoto.setPhoto(photo);
        //sendPhoto.setReplyMarkup(inlineKeyboardMaker.getGradeKeyboard());
        return sendPhoto;
    }
}
