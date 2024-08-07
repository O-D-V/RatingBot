package ru.AliceTelegramBot.body;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.AliceTelegramBot.config.BotConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.AliceTelegramBot.handlers.CallbackQueryHandler;
import ru.AliceTelegramBot.handlers.TextMessageHandler;
import ru.AliceTelegramBot.models.Photo;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.services.UserPhotoGradeService;
import ru.AliceTelegramBot.util.BotApiMethodContainer;
import ru.AliceTelegramBot.util.TempUserData;

import java.util.List;
import java.util.Objects;

/*
*TODO
* Админ добавляет фото (/addPhoto)
* -устанавливаем пользователю последнее сообщение = /addPhoto
* -запрашиваем фото и подпись
*
* TODO
*  Админ отправляет подпись:
* -добаляем подпись во временный объект
* -проверяем не заполнен ли объект для добавления фото в БД
*
* TODO
*  Админ отправляет фото:
* -скачиваем фото
* -добавляем путь во временный объект
* -проверяем заполнен ли временный объект для добавления в БД
* */

@Component
public class MainHandler extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CallbackQueryHandler callbackQueryHandler;
    private final TextMessageHandler textMessageHandler;
    private final UserPhotoGradeService userPhotoGradeService;
    private final TempUserData tempUserData;
    Logger logger = LoggerFactory.getLogger(MainHandler.class);


    @Autowired
    public MainHandler(BotConfig botConfig, CallbackQueryHandler callbackQueryHandler, TextMessageHandler textMessageHandler, UserPhotoGradeService userPhotoGradeService, TempUserData tempUserData){
        this.botConfig = botConfig;
        this.callbackQueryHandler = callbackQueryHandler;
        this.textMessageHandler = textMessageHandler;
        this.userPhotoGradeService = userPhotoGradeService;
        this.tempUserData = tempUserData;
        logger.info("AAAAAAAAAAAAAAAA");
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String currency = "";
        Long chatId = update.getMessage().getChatId();

        logger.info("receive message from " + chatId);

        //TODO Добавить чтобы тут получался юзер и поставлялся в остальные методы. Чтобы не обращаться каждый раз к БД

        //Authentication user
        authenticateUser(update);

        if(update.hasMessage()){
            if(update.getMessage().hasText()) {
                List<BotApiMethodContainer> methods = textMessageHandler.processTextMessage(update);
                for(BotApiMethodContainer botMethodContainer : methods) {
                    try {
                        if(botMethodContainer.isSendPhotoMethod()) execute(botMethodContainer.getSendPhotoMethod());
                        else execute(botMethodContainer.getOtherMethod());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(update.getMessage().hasPhoto()){
                handlePhoto(update);
            }
        }else if(update.hasCallbackQuery()){
            List<BotApiMethod<?>> methodsList =  callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());
            for(BotApiMethod<?> method : methodsList) {
                try {
                    execute(method);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void savePhoto(String userID){
        Photo photo = new Photo(tempUserData.getPhotoUrlByUserID(Long.getLong(userID)), 0F, tempUserData.getPhotoNameByUserID(Long.getLong(userID)), null);
        userPhotoGradeService.savePhoto(photo);
    }

    public boolean isPhotoObjectCompleted(String userID){
        Long userIDLong = Long.getLong(userID);
        return tempUserData.getPhotoNameByUserID(userIDLong) != null && tempUserData.getPhotoUrlByUserID(userIDLong) != null;
    }

    public void downloadPhoto(PhotoSize photo, String whereToDownload){
        //PhotoSize photo = photos.get(photos.size()-1);
        GetFile getFile = new GetFile(photo.getFileId());
        try {
            File file = execute(getFile);
            downloadFile(file, new java.io.File(whereToDownload));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void authenticateUser(Update update){
        Long chatId = update.getMessage().getChatId();
        User user = userPhotoGradeService.getUserByID(chatId);
        if (user == null) {
            user = new User();
            user.setAdmin(false);
            user.setName(update.getMessage().getChat().getFirstName());
            user.setUserID(chatId);
            userPhotoGradeService.saveUser(user);
        }
    }

    private void handlePhoto(Update update){
        Long chatId = update.getMessage().getChatId();
        User user = userPhotoGradeService.getUserByID(chatId);
        if (Objects.equals(userPhotoGradeService.getUserByID(chatId).getLastMessage(), "/newPhoto")) {
            //Обрабатываем новое фото(скачиваем, добавляем в БД)
            logger.info("Downloading photo from " + chatId);
            List<PhotoSize> photos = update.getMessage().getPhoto();
            //photoName добавлять во временный объект и там обновлять после отправки нормальной подписи
            String photoName = tempUserData.getPhotoNameByUserID(chatId) == null? tempUserData.getPhotoNameByUserID(chatId) + ".png":"photo" + (userPhotoGradeService.countPhotos()+1) + ".png";
            downloadPhoto(photos.get(photos.size() - 1), "photo/" + photoName);
            tempUserData.setPhotoUrl(chatId, "photo/" + photoName);
            SendMessage sendMessage;
            if(isPhotoObjectCompleted(chatId.toString())){
                savePhoto(chatId.toString());
                sendMessage = new SendMessage(String.valueOf(chatId), "Добавлено");
            }
            else sendMessage = new SendMessage(String.valueOf(chatId), "Отправьте название фото");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
