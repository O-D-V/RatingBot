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

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Я котик чипи чипи"));
        row1.add(new KeyboardButton("Я котик happy happy"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Я ГУЛЬ"));
        row2.add(new KeyboardButton("Я человек"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Календарь"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getCalendarMainMenuKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Создать событие"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Все события"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("События сегодня"));
        row3.add(new KeyboardButton("События завтра"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("Найти по описанию"));
        row4.add(new KeyboardButton("Найти по дате"));
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
