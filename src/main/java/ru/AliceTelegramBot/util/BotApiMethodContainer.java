package ru.AliceTelegramBot.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Component
public class BotApiMethodContainer {
    private BotApiMethod<?> method = null;
    private SendPhoto sendPhoto = null;

    public BotApiMethodContainer() {
    }

    public BotApiMethodContainer(BotApiMethod<?> method) {
        this.method = method;
        sendPhoto = null;
    }

    public BotApiMethodContainer(SendPhoto sendPhoto) {
        this.sendPhoto = sendPhoto;
        method = null;
    }

    public BotApiMethod<?> getOtherMethod(){
        return method;
    }

    public SendPhoto getSendPhotoMethod() {
        return sendPhoto;
    }

    public boolean isSendPhotoMethod(){
        if(sendPhoto != null) return true;
        return false;
    }

    public boolean isContainerEmpty(){
        return (sendPhoto == null) && (method == null);
    }

    public void setMethod(BotApiMethod<?> method){
        this.method = method;
        sendPhoto = null;
    }

    public void setSendPhotoMethod(SendPhoto sendPhoto){
        this.sendPhoto = sendPhoto;
        method = null;
    }
}
