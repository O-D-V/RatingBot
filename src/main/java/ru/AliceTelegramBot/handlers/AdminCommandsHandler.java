package ru.AliceTelegramBot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.AliceTelegramBot.keyboards.ReplyKeyboardMaker;
import ru.AliceTelegramBot.models.Photo;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.services.UserPhotoGradeService;
import ru.AliceTelegramBot.util.BotApiMethodContainer;
import ru.AliceTelegramBot.util.TempUserData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class AdminCommandsHandler {

    private final UserPhotoGradeService userPhotoGradeService;
    private  final TempUserData tempUserData;

    public AdminCommandsHandler(UserPhotoGradeService userPhotoGradeService, TempUserData tempUserData) {
        this.userPhotoGradeService = userPhotoGradeService;
        this.tempUserData = tempUserData;
    }

    public List<BotApiMethodContainer> handle(Update update, User user) throws NoSuchMethodException {
        final Long chatId = update.hasMessage()?update.getMessage().getChatId():update.getCallbackQuery().getMessage().getChatId();
        final String messageText = update.hasMessage()?update.getMessage().getText():update.getCallbackQuery().getData();

        switch(messageText){
            case "Добавить админа":
            case "/addAdmin":{
                user.setLastMessage("/addAdmin");
                userPhotoGradeService.updateUser(chatId, user);
                return  sendMessage(chatId.toString(), "Введите id человека которого хотите сделать админом.");
            }
            case "Удалить админа":
            case "/deleteAdmin":{
                user.setLastMessage("/deleteAdmin");
                userPhotoGradeService.updateUser(chatId, user);
                return  sendMessage(chatId.toString(), "Введите id человека которого хотите удалить из админов.");
            }

            case "Список админов":
            case "/showAllAdmins":{
                List<User> admins = userPhotoGradeService.getAllAdmins();
                StringBuilder answer = new StringBuilder();
                for(User admin:admins){
                    answer.append(admin.getUserID()).append(" ").append(admin.getName()).append("\n");
                }

                return  sendMessage(chatId.toString(), answer.toString());
            }

            case "Добавить фото":
            case "/newPhoto": {
                //Запрос на создание нового фото
                user.setLastMessage("/newPhoto");
                userPhotoGradeService.updateUser(chatId, user);
                return sendMessage(chatId.toString(), "Запрос принят. Отправьте подпись к фото");
            }

            case "Удалить фото":
            case "/deletePhoto": {
                //Запрос на создание нового фото
                user.setLastMessage("/deletePhoto");
                userPhotoGradeService.updateUser(chatId, user);
                return sendMessage(chatId.toString(), "Запрос принят. Отправьте подпись к фото");
            }
            case "/adminMenu": {
                ReplyKeyboardMaker replyKeyboardMaker = new ReplyKeyboardMaker();
                return sendMessage(chatId.toString(), "...", replyKeyboardMaker.getAdminMenu());
            }
            case "/hideAdminMenu": {
                return sendMessage(chatId.toString(), "...", new ReplyKeyboardMarkup());
            }
            case "Список фото":
            case "/showPhotoNames":
            {
                //отправляет в ответе список подписей фото
                StringBuilder res = new StringBuilder();
                List<String> list = userPhotoGradeService.getPhotosNames();
                for(String l:list) res.append(l).append("\n");
                if(res.isEmpty()) res.append("Ничего не найдено");
                return sendMessage(chatId.toString(), res.toString());
            }
            case "Меню пользователя":{
                return sendMessage(chatId.toString(), "...", new ReplyKeyboardMaker().getUserMenu());
            }
            case "/showPhoto": {
                //отправляет фото по подписи
                user.setLastMessage("/showPhoto");
                userPhotoGradeService.updateUser(chatId, user);
                return  sendMessage(chatId.toString(), "Введите подпись");
            }
            case "/createRate":
            {
                user.setLastMessage("/createRate");
                userPhotoGradeService.updateUser(chatId, user);
                return  sendMessage(chatId.toString(), "Введите подпись к фото, которое хотели бы оценить и оценку через пробел");
            }
            case "/help":
            {
                StringBuilder res = new StringBuilder();
                res.append("/id - показывает id пользователя").append("\n");
                res.append("/adminMenu - (адм.) переключает меню на меню администратора").append("\n");
                res.append("/hideAdminMenu - (адм.) переключает меню на меню пользователя").append("\n");
                res.append("/hideAdminMenu - (адм.) убирает меню админа").append("\n");
                res.append("/showPhoto - (адм.) показывает конкретное фото по описанию").append("\n");
                res.append("/createRate - (адм.) добавляет оценку к фото").append("\n");
                return  sendMessage(chatId.toString(), res.toString());
            }
            default:
                switch (user.getLastMessage()){
                    case "/addAdmin":{
                        User userToBeUpdated = userPhotoGradeService.getUserByID(Long.parseLong(messageText));
                        if(userToBeUpdated == null) return  sendMessage(chatId.toString(), "В базе нет человека с таким id.");
                        userToBeUpdated.setAdmin(true);
                        userPhotoGradeService.updateUser(Long.parseLong(messageText), userToBeUpdated);
                        user.setLastMessage("null");
                        userPhotoGradeService.updateUser(chatId, user);
                        return  sendMessage(chatId.toString(), "Человек с именем " + userToBeUpdated.getName() + " стал админом.");
                    }
                    case "/deleteAdmin":{
                        User userToBeUpdated = userPhotoGradeService.getUserByID(Long.parseLong(messageText));
                        if(userToBeUpdated == null) return  sendMessage(chatId.toString(), "В базе нет человека с таким id.");
                        userToBeUpdated.setAdmin(false);
                        userPhotoGradeService.updateUser(Long.parseLong(messageText), userToBeUpdated);
                        user.setLastMessage("null");
                        userPhotoGradeService.updateUser(chatId, user);
                        return  sendMessage(chatId.toString(), "Человек с именем " + userToBeUpdated.getName() + " удален из админов.");
                    }
                    case "/newPhoto": {
                        userPhotoGradeService.updateUser(chatId, user);
                        savePhotoName(String.valueOf(chatId), messageText);
                        return sendMessage(chatId.toString(), "Отправьте фото");
                    }
                    case "/deletePhoto": {
                        user.setLastMessage("null");
                        userPhotoGradeService.updateUser(chatId, user);
                        try {
                            userPhotoGradeService.deletePhotoByCapture(messageText.trim());
                        }catch (NoSuchElementException e) {
                            return sendMessage(chatId.toString(), "Нет такого фото");
                        }
                        return sendMessage(chatId.toString(), "Удалено");
                    }
                    case "/showPhoto": {
                        //Возвращает пользователю фото с отправленной ранее подписью
                        user.setLastMessage("null");
                        userPhotoGradeService.updateUser(chatId, user);
                        Photo photo = userPhotoGradeService.getPhotoByName(messageText);
                        if (photo == null) return sendMessage(chatId.toString(), "Нет фото с такой подписью");
                        return Collections.singletonList(new BotApiMethodContainer(sendPhoto(Long.toString(chatId), photo.getName(), photo.getUrl())));
                    }
                    case "/createRate": {
                        user.setLastMessage("null");
                        userPhotoGradeService.updateUser(chatId, user);
                        String cleanMessageText = messageText.trim();

                        String photoName = cleanMessageText.substring(0, cleanMessageText.indexOf(' '));
                        String photoGrade = cleanMessageText.substring(cleanMessageText.indexOf(' ') + 1);

                        Photo photo = userPhotoGradeService.getPhotoByName(photoName);

                        //TODO Сделать проверку на наличие оценки

                        userPhotoGradeService.saveGrade(photo, user, Integer.parseInt(photoGrade));
                        return sendMessage(chatId.toString(), "Выполнено");
                    }
            }
        }
        throw new NoSuchMethodException();
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
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        return Collections.singletonList(new BotApiMethodContainer(sendMessage));
    }


    private void savePhotoName(String userId, String photoName){
        tempUserData.setPhotoNameForUser(Long.parseLong(userId), photoName);
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
