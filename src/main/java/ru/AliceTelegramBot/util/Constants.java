package ru.AliceTelegramBot.util;

public class Constants {

    public static final String NO_SUCH_COMMAND_ERROR = "Нет такой команды";
    public static final String SUCCESS_MESSAGE = "Выполнено успешно";

    public static String getWelcomeMessage(String name){
        return "Hi, " + name + ", nice to meet you!";
    }
}
