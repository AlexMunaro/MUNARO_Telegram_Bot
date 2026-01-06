package org.example;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            String botToken = "8430822310:AAH7dni1GTuKRaO4Qdw5prHoC4NjQMQj1pE";
            TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();

            botsApplication.registerBot(botToken, new WordleBotVS());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
