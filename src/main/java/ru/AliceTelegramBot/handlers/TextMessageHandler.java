package ru.AliceTelegramBot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import ru.AliceTelegramBot.body.MainHandler;
import ru.AliceTelegramBot.util.Constants;

import java.util.Collections;
import java.util.List;

@Component
public class TextMessageHandler {
private final MainHandler mainHandler;

    @Autowired
    public TextMessageHandler(MainHandler mainHandler) {
        this.mainHandler = mainHandler;
    }

    public List<BotApiMethod<?>> processTextMessage(Update update){
        final String chatId = update.getMessage().getChatId().toString();
        final String messageText = update.getMessage().getText();

        switch (messageText) {
            case "/start":
                return startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            case "/helloKitty":
               mainHandler.sendPhoto(chatId, "Photo", "photo//HelloKitty.jpg");
                break;
            case "/newPhoto":
                break;
            default:
                return  Collections.singletonList(new SendMessage(chatId, Constants.NO_SUCH_COMMAND_ERROR));
        }
        return  Collections.singletonList(new SendMessage(chatId, Constants.SUCCESS_MESSAGE));
    }

    private List<BotApiMethod<?>> startCommandReceived(String chatId, String name) {
        String answer = Constants.getWelcomeMessage(name);
        return sendMessage(chatId, answer);
    }

    private List<BotApiMethod<?>> sendMessage(String chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
            return Collections.singletonList(sendMessage);
    }
}
