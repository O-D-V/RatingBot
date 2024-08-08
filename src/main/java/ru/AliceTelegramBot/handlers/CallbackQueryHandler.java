package ru.AliceTelegramBot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.AliceTelegramBot.keyboards.InlineKeyboardMaker;
import ru.AliceTelegramBot.models.User;

import java.util.*;

@Component
public class CallbackQueryHandler {

    InlineKeyboardMaker inlineKeyboardMaker;

    @Autowired
    public CallbackQueryHandler(InlineKeyboardMaker inlineKeyboardMaker){
        this.inlineKeyboardMaker = inlineKeyboardMaker;
    }



    public List<BotApiMethod<?>> processCallbackQuery(CallbackQuery buttonQuery, User user){
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();
        switch (data){
            case "minutesRange":
                SendMessage sm = new SendMessage();
                sm.setChatId(chatId);
                //sm.setReplyMarkup(inlineKeyboardMaker.getCalendarMinuteButtons("/", minutesRange));
                sm.setText("Выберите минуты");
                return Collections.singletonList(sm);
        }

        switch (data) {
            case "/button1":
                return Collections.singletonList(new SendMessage(chatId, "Вы порадовались"));
            case "/button 2":
                return Collections.singletonList(new SendMessage(chatId, "Вы не порадовались"));
            default:
                return Collections.singletonList(new SendMessage(chatId, "Ошибка"));
        }
    }
}
