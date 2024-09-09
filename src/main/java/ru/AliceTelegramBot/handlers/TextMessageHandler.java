package ru.AliceTelegramBot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.AliceTelegramBot.keyboards.InlineKeyboardMaker;
import ru.AliceTelegramBot.keyboards.ReplyKeyboardMaker;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TextMessageHandler {
    private final UserPhotoGradeService userPhotoGradeService;
    private  final TempUserData tempUserData;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    Logger logger = LoggerFactory.getLogger(TextMessageHandler.class);

    @Autowired
    public TextMessageHandler(UserPhotoGradeService userPhotoGradeService, TempUserData tempUserData, InlineKeyboardMaker inlineKeyboardMaker) {
        this.userPhotoGradeService = userPhotoGradeService;
        this.tempUserData = tempUserData;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
    }

    public List<BotApiMethodContainer> processTextMessage(Update update, User user){
        final Long chatId = update.getMessage().getChatId();
        final String messageText = update.getMessage().getText();


        switch (messageText) {
            case "/start":
                return startCommandReceived(chatId.toString(), update.getMessage().getChat().getFirstName());
            case "/helloKitty":
                return Collections.singletonList(new BotApiMethodContainer(sendPhoto(chatId.toString(), "Photo", "photo//HelloKitty.jpg")));
            case "Оценить фото":
            case "/ratePhoto": {
                Photo photo = null;
                try {
                    photo = userPhotoGradeService.getRandomUnratedPhotoForUser(user.getUserID().intValue());
                } catch (IndexOutOfBoundsException e) {
                    return sendMessage(chatId.toString(), "Нет больше фото для оценки");
                }
                return Collections.singletonList(new BotApiMethodContainer(sendPhoto(chatId.toString(), photo.getName(), photo.getUrl(), inlineKeyboardMaker.gradeKeyboard(photo.getName()))));
            }
            case "Топ 5":
            case "/topFivePhotos": {
                List<Photo> photos = userPhotoGradeService.getTopFiveByRate();
                List<BotApiMethodContainer> methods = new ArrayList<>();
                for (Photo photo : photos)
                    methods.add(new BotApiMethodContainer(sendPhoto(chatId.toString(), photo.getAverageRate() + "★" + " - " + photo.getName(), photo.getUrl())));
                return methods;
            }
            case "/id": {
                return sendMessage(chatId.toString(), chatId + " - ваш ID.");
            }
        }
                return  sendMessage(chatId.toString(), Constants.NO_SUCH_COMMAND_ERROR);

        //return  sendMessage(chatId.toString(), Constants.SUCCESS_MESSAGE);
    }

    private void savePhotoName(String userId, String photoName){
        tempUserData.setPhotoNameForUser(Long.parseLong(userId), photoName);
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
        ReplyKeyboardMaker replyKeyboardMaker = new ReplyKeyboardMaker();
        return sendMessage(chatId, answer, replyKeyboardMaker.getUserMenu());
    }

    //Возвращает метод бота для отправки текстового сообщения
    private List<BotApiMethodContainer> sendMessage(String chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
            return Collections.singletonList(new BotApiMethodContainer(sendMessage));
    }

    private List<BotApiMethodContainer> sendMessage(String chatId, String textToSend, ReplyKeyboardMarkup replyKeyboardMarkup){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return Collections.singletonList(new BotApiMethodContainer(sendMessage));
    }

    public SendPhoto sendPhoto(String chatId, String caption, String path){
        return sendPhoto(chatId,caption,path, null);
    }

    //Возвращает метод бота для отправки фото
    public SendPhoto sendPhoto(String chatId, String caption, String path, InlineKeyboardMarkup replyKeyboardMarkup){
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
        if(replyKeyboardMarkup != null) sendPhoto.setReplyMarkup(replyKeyboardMarkup);
        //sendPhoto.setReplyMarkup(inlineKeyboardMaker.gradeKeyboard());
        return sendPhoto;
    }
}
