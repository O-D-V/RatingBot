package ru.AliceTelegramBot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.util.BotApiMethodContainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

//TODO Добавить сюда действия:
/*Добавить нового админа
* Добавить фото для оценки
* Вывести список админов
* Удалить админа
* Админ меню(с админскими кнопками)*/
@Component
public class AdminCommandsHandler {

    public List<BotApiMethodContainer> handle(Update update, User user){
        final Long chatId = update.hasMessage()?update.getMessage().getChatId():update.getCallbackQuery().getMessage().getChatId();
        final String messageText = update.hasMessage()?update.getMessage().getText():update.getCallbackQuery().getData();

        return sendMessage(chatId.toString(), "Нет больше фото для оценки");
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
