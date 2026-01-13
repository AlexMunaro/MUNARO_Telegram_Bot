package org.example;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            String botToken = Config.get("TELEGRAM_BOT_TOKEN");
            TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();

            botsApplication.registerBot(botToken, new WordleBotVS());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
