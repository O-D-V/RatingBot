package ru.AliceTelegramBot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource("application.properties")

public class BotConfig {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String token;

    @Value("#{'${bot.admins}'.split(',')}")
    private List<String> adminsIDs;

    public BotConfig(String botName, String token) {
        this.botName = botName;
        this.token = token;
    }

    public BotConfig() {
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getAdminsIDs() {
        return adminsIDs;
    }

    public void setAdminsIDs(List<String> adminsIDs) {
        this.adminsIDs = adminsIDs;
    }
}
