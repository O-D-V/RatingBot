package ru.AliceTelegramBot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboardMaker {

    public InlineKeyboardMaker() {
    }

    public InlineKeyboardMarkup gradeKeyboard() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        for(int i = 1; i < 6; i++) {
            keyboardButtonsRow1.add(getButton(
                    ""+i,
                    "/" + i
            ));
        }
        rowList.add(keyboardButtonsRow1);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        for(int i = 6; i < 11; i++) {
            keyboardButtonsRow2.add(getButton(
                    ""+i,
                    "/" + i
            ));
        }
        rowList.add(keyboardButtonsRow2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);
        return button;
    }
}
