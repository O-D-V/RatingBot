package ru.AliceTelegramBot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.AliceTelegramBot.body.MainHandler;
import ru.AliceTelegramBot.keyboards.InlineKeyboardMaker;
import ru.AliceTelegramBot.models.Photo;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.services.UserPhotoGradeService;

import java.util.*;

@Component
public class CallbackQueryHandler {

    InlineKeyboardMaker inlineKeyboardMaker;
    private final UserPhotoGradeService userPhotoGradeService;
    Logger logger = LoggerFactory.getLogger(MainHandler.class);

    @Autowired
    public CallbackQueryHandler(InlineKeyboardMaker inlineKeyboardMaker, UserPhotoGradeService userPhotoGradeService){
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        this.userPhotoGradeService = userPhotoGradeService;
    }



    public List<BotApiMethod<?>> processCallbackQuery(CallbackQuery buttonQuery, User user) {
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();
        logger.debug(data);
        String photoCapture = data.substring(1, data.indexOf(':'));
        String rate = data.substring(data.indexOf(':') + 1);
        logger.debug(data);
        switch (rate) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10": {
                Photo photo = userPhotoGradeService.getPhotoByName(photoCapture);
                userPhotoGradeService.saveGrade(photo, user, Integer.parseInt(rate));
                return Collections.singletonList(new SendMessage(chatId, rate));
            }
        }
        return Collections.singletonList(new SendMessage(chatId, "Ошибка, неизвестная команда"));
    }
    }

