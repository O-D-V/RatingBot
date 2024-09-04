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
*
* Админ добавляет фото (/addPhoto)
* -устанавливаем пользователю последнее сообщение = /addPhoto
* -запрашиваем фото и подпись
*
*
*  Админ отправляет подпись:
* -добаляем подпись во временный объект
* -проверяем не заполнен ли объект для добавления фото в БД
*
*
*  Админ отправляет фото:
* -скачиваем фото
* -добавляем путь во временный объект
* -проверяем заполнен ли временный объект для добавления в БД
* */

//TODO Добавить админа
//TODO Добавить запуск цикла оценки фото после /start
//TODO Добавить кнопки для админа

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



        //Authentication user
        User user = authenticateUser(update);
        logger.debug("receive message from " + user.getUserID() + ":" + ((update.hasMessage() && update.getMessage().hasText())?update.getMessage().getText():"Not a text"));

        if(update.hasMessage()){
            if(update.getMessage().hasText()) {
                List<BotApiMethodContainer> methods = textMessageHandler.processTextMessage(update, user);
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
                handlePhoto(update, user);
            }
        }else if(update.hasCallbackQuery()){
            List<BotApiMethod<?>> methodsList =  callbackQueryHandler.processCallbackQuery(update.getCallbackQuery(), user);
            for(BotApiMethod<?> method : methodsList) {
                try {
                    execute(method);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void savePhoto(Long userID){
        logger.debug("URL:" + tempUserData.getPhotoUrlByUserID(userID) + " Name:" + tempUserData.getPhotoNameByUserID(userID));
        Photo photo = new Photo(tempUserData.getPhotoUrlByUserID(userID), 0F, tempUserData.getPhotoNameByUserID(userID), null);
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

    private User authenticateUser(Update update){
        Long chatId = update.hasMessage()? update.getMessage().getChatId(): update.getCallbackQuery().getFrom().getId();
        User user = userPhotoGradeService.getUserByID(chatId);
        if (user == null) {
            user = saveNewUser(update);
        }
        return user;
    }

    private User saveNewUser(Update update){
        User user = new User();
        user.setAdmin(false);
        if(update.hasMessage()) {
            user.setName(update.getMessage().getChat().getFirstName());
            user.setUserID(update.getMessage().getChatId());
        }else if(update.hasCallbackQuery()){
            user.setName(update.getCallbackQuery().getFrom().getFirstName());
            user.setUserID(update.getCallbackQuery().getFrom().getId());
        }
        userPhotoGradeService.saveUser(user);
        return user;
    }

    private void handlePhoto(Update update, User user){
        Long chatId = update.getMessage().getChatId();
        if (Objects.equals(user.getLastMessage(), "/newPhoto") && tempUserData.getPhotoNameByUserID(chatId) != null) {
            //Обрабатываем новое фото(скачиваем, добавляем в БД)
            logger.info("Downloading photo from " + chatId);
            List<PhotoSize> photos = update.getMessage().getPhoto();


            String photoName = tempUserData.getPhotoNameByUserID(chatId) + ".png";
            downloadPhoto(photos.get(photos.size() - 1), "photo/" + photoName);
            tempUserData.setPhotoUrl(chatId, "photo/" + photoName);
            SendMessage sendMessage;

            savePhoto(chatId);

            user.setLastMessage("null");
            userPhotoGradeService.updateUser(chatId, user);
            sendMessage = new SendMessage(String.valueOf(chatId), "Добавлено");

            logger.debug("Photo saved with name " + photoName);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                execute(new SendMessage(String.valueOf(chatId), "Введите сначала подпись к фото"));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }}
    }

}
