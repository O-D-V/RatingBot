package ru.AliceTelegramBot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplyKeyboardMaker {

    public ReplyKeyboardMaker() {
    }

    public ReplyKeyboardMarkup getAdminMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Добавить админа"));
        row1.add(new KeyboardButton("Удалить админа"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Добавить фото"));
        row2.add(new KeyboardButton("Удалить фото"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Список админов"));

        KeyboardRow row4 = new KeyboardRow();
        row3.add(new KeyboardButton("Список фото"));

        KeyboardRow row5 = new KeyboardRow();
        row3.add(new KeyboardButton("Меню пользователя"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getUserMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Оценить фото"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Топ 5"));


        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
