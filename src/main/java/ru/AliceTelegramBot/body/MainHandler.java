package ru.AliceTelegramBot.body;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.AliceTelegramBot.config.BotConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.AliceTelegramBot.handlers.CallbackQueryHandler;
import ru.AliceTelegramBot.handlers.TextMessageHandler;
import ru.AliceTelegramBot.keyboards.InlineKeyboardMaker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class MainHandler extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    private final CallbackQueryHandler callbackQueryHandler;
    private final TextMessageHandler textMessageHandler;

    public MainHandler(BotConfig botConfig, InlineKeyboardMaker inlineKeyboardMaker, CallbackQueryHandler callbackQueryHandler, TextMessageHandler textMessageHandler){
        this.botConfig = botConfig;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        this.callbackQueryHandler = callbackQueryHandler;
        this.textMessageHandler = textMessageHandler;
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
        long chatId = update.getMessage().getChatId();
        if(update.hasMessage()){
            if(update.getMessage().hasText()) {
                textMessageHandler.processTextMessage(update);
            }
            if(update.getMessage().hasPhoto()){
                List<PhotoSize> photos = update.getMessage().getPhoto();
                getPhoto(photos, "photo/photo.png");
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

    public void getPhoto(List<PhotoSize> photos, String whereToDownload){
        PhotoSize photo = photos.get(photos.size()-1);
        GetFile getFile = new GetFile(photo.getFileId());
        try {
            File file = execute(getFile);
            downloadFile(file, new java.io.File(whereToDownload));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(String chatId, String caption, String path){
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
        sendPhoto.setReplyMarkup(inlineKeyboardMaker.getGradeKeyboard());
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
