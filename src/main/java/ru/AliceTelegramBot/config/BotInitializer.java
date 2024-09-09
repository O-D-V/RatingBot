package ru.AliceTelegramBot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.AliceTelegramBot.body.MainHandler;
import ru.AliceTelegramBot.models.User;
import ru.AliceTelegramBot.services.UserPhotoGradeService;

import java.util.List;

@Component
public class BotInitializer {
    private final MainHandler telegramBot;
    private final BotConfig botConfig;
    private final UserPhotoGradeService userPhotoGradeService;

    @Autowired
    public BotInitializer(MainHandler telegramBot, BotConfig botConfig, UserPhotoGradeService userPhotoGradeService) {
        this.telegramBot = telegramBot;
        this.botConfig = botConfig;
        this.userPhotoGradeService = userPhotoGradeService;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init()throws TelegramApiException {
        setDefaultAdmins();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e){

        }
    }

    private void setDefaultAdmins(){
        List<String> adminsID = botConfig.getAdminsIDs();
        User user = null;
            for(String id:adminsID) {
                user = userPhotoGradeService.getUserByID(Long.parseLong(id));
                if(user == null || user.isAdmin()) continue;
                user.setAdmin(true);
                userPhotoGradeService.updateUser(Long.parseLong(id), user);
            }
    }
}