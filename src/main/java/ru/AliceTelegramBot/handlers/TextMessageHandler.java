package ru.AliceTelegramBot.handlers;

import jakarta.persistence.Index;
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
import ru.AliceTelegramBot.body.MainHandler;
import ru.AliceTelegramBot.keyboards.InlineKeyboardMaker;
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

            case "/newPhoto": {
                //Запрос на создание нового фото
                user.setLastMessage("/newPhoto");
                userPhotoGradeService.updateUser(chatId, user);
                return sendMessage(chatId.toString(), "Запрос принят. Отправьте подпись к фото");
            }
            case "/showPhoto": {
                //отправляет фото по подписи
                user.setLastMessage("/showPhoto");
                userPhotoGradeService.updateUser(chatId, user);
                return  sendMessage(chatId.toString(), "Введите подпись");
            }
            case "/showPhotoNames":
            {
                //отправляет в ответе список подписей фото
                StringBuilder res = new StringBuilder();
                List<String> list = userPhotoGradeService.getPhotosNames();
                for(String l:list) res.append(l).append("\n");
                if(res.isEmpty()) res.append("Ничего не найдено");
                return sendMessage(chatId.toString(), res.toString());
            }
            case "/createRate":
            {
                user.setLastMessage("/createRate");
                userPhotoGradeService.updateUser(chatId, user);
                return  sendMessage(chatId.toString(), "Введите подпись к фото, которое хотели бы оценить и оценку через пробел");
            }
            case "/ratePhoto":
            {
                Photo photo = null;
                try {
                    photo = userPhotoGradeService.getRandomUnratedPhotoForUser(user.getUserID().intValue());
                }catch (IndexOutOfBoundsException e){
                    return sendMessage(chatId.toString(), "Нет больше фото для оценки");
                }
                return Collections.singletonList(new BotApiMethodContainer(sendPhoto(chatId.toString(), photo.getName(), photo.getUrl(), inlineKeyboardMaker.gradeKeyboard(photo.getName()))));
            }
            default:
                switch (user.getLastMessage()) {
                    case "/newPhoto": {
                        //Обрабатывается подпись к новому фото.
                        savePhotoName(chatId.toString(), messageText);
                        logger.debug("Title is saved");
                        return sendMessage(chatId.toString(), "Отправьте фото");

                    }
                    case "/showPhoto": {
                        //Возвращает пользователю фото с отправленной ранее подписью
                        Photo photo = userPhotoGradeService.getPhotoByName(messageText);
                        if (photo == null) return sendMessage(chatId.toString(), "Нет фото с такой подписью");
                        return Collections.singletonList(new BotApiMethodContainer(sendPhoto(Long.toString(chatId), photo.getName(), photo.getUrl())));
                    }
                    case "/createRate": {
                        String cleanMessageText = messageText.trim();

                        String photoName = cleanMessageText.substring(0, cleanMessageText.indexOf(' '));
                        String photoGrade = cleanMessageText.substring(cleanMessageText.indexOf(' ') + 1);

                        Photo photo = userPhotoGradeService.getPhotoByName(photoName);

                        //TODO Сделать проверку на наличие оценки

                        userPhotoGradeService.saveGrade(photo, user, Integer.parseInt(photoGrade));
                        return sendMessage(chatId.toString(), "Выполнено");
                    }
                }

                return  sendMessage(chatId.toString(), Constants.NO_SUCH_COMMAND_ERROR);
        }
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
        return sendMessage(chatId, answer);
    }

    //Возвращает метод бота для отправки текстового сообщения
    private List<BotApiMethodContainer> sendMessage(String chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
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
