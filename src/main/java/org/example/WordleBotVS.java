package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class WordleBotVS implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient =
            new OkHttpTelegramClient(Config.get("TELEGRAM_BOT_TOKEN"));

    private static final String GIPHY_API_KEY =
            Config.get("GIPHY_API_KEY");

    private final Random random = new Random();

    private int statusMessageId;

    WordleProfile pf = null;
    WordleGame wg = null;
    database db;
    private boolean waitingForProfileInput = false;
    private boolean waitingForSettingsInput = false;
    DictionaryAPI Dictionary;
    int ma = 0, wi = 0;

    public WordleBotVS() throws Exception {
        db = new database();
        Dictionary = new DictionaryAPI();
        //pf=db.getPlayerByTelegramUsername("Devopagareilmutuo");
        /*
        db.createPlayer(String.valueOf(1234),
                "Wizard",
                "it",
                "@Devopagareilmutuo");
        */
        //System.out.println(db.getPlayerByTag("7777").tag);
        //System.out.println(db.getPlayerByTelegramUsername("Devopagareilmutuo").username);
    }

    // ===================== ENTRY POINT =====================
    @Override
    public void consume(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== CALLBACK =====================
    private void handleCallback(CallbackQuery cb) throws Exception {
        String data = cb.getData();
        Message msg = (Message) cb.getMessage();
        Long chatId = cb.getMessage().getChatId();

        switch (data) {
            case "EDIT_PROFILE" -> edit_profile(chatId, msg.getFrom().getUserName());

            case "DELETE_PROFILE" -> delete_profile(chatId);

            case "GIVE_UP" -> giveup(chatId);

            case "DEFINITION" -> definition(chatId);
        }
    }

    private void definition(Long chatId) {
        send(chatId, DictionaryAPI.getDefinition(wg.word));
    }

    private void delete_profile(Long chatId) throws Exception {
        if (pf == null) {
            send(chatId, "Your profile doesn't exist");
        } else {
            send(chatId, "Your profile got deleted successfully");
            db.deletePlayerByTag(pf.tag);
            pf = null;
            ma = 0;
            wi = 0;
        }
    }

    private void edit_profile(Long chatId, String User) throws Exception {
        //pf=db.getPlayerByTelegramUsername("Devopagareilmutuo");
        if (pf == null) {
            send(chatId, "Your profile doesn't exist");
        } else {
            db.deletePlayerByTag(pf.tag);
            create_profile(chatId);
            pf = db.getPlayerByTelegramUsername(User);
        }
    }

    // ===================== MESSAGE =====================
    private void handleMessage(Message msg) throws Exception {
        if (waitingForProfileInput) {
            handleProfileCreation(msg);
            return;
        }
        if (waitingForSettingsInput) {
            handlePlayVariant(msg);
            return;
        }
        Long chatId = msg.getChatId();
        Long userId = msg.getFrom().getId();
        String text = msg.getText();
        pf = db.getPlayerByTelegramUsername(msg.getFrom().getUserName());
        if (wg == null || (!wg.playing)) {
            switch (text) {
                case "/help":
                    help(chatId);
                    break;
                case "/profile":
                    if (pf != null) {
                        System.out.println(pf.username);
                        profile(msg, chatId, userId);
                    } else {
                        send(chatId, "You don't have a profile!");
                    }
                    break;
                case "/play":
                    if (pf != null)
                        startGame(chatId, 6, pf.favlang, 5);
                    else
                        send(chatId, "You can't play without a profile!");
                    break;

                case "/play_variant":
                    if (pf != null)
                        play_variant(chatId, msg);
                    else
                        send(chatId, "You can't play without a profile!");
                    break;
                case "/create_profile":
                    create_profile(chatId);
                    break;
                case "/delete_profile":
                    delete_profile(chatId);
                    break;
                case "/edit_profile":
                    edit_profile(chatId, msg.getFrom().getUserName());
                    break;
                case "/leaderboard":
                    leaderboard(chatId);
                    break;
                default:
                    sendGif(chatId, text, text + "!");
                    break;
            }
        } else {
            if (text.startsWith("/")) {
                if (text.equals("/give_up")) {
                    giveup(chatId);
                } else {
                    send(chatId, "❌ You can't use commands during a match");
                }
                return;
            }

            if (text.length() != wg.length) {
                send(chatId, "The word must be exactly " + wg.length + " characters long");
                return;
            }

            String s = wg.next(text);

            editMessage(chatId, statusMessageId,
                    "Tries: " + wg.tries + " / " + wg.maxTries, null);

            if (s.equals("1")) {
                if (Objects.equals(pf.favlang, "en"))
                    DefinitionGif(chatId, "victory", "You won in " + wg.tries + " tries!");
                else
                    sendGif(chatId, "victory", "You won in " + wg.tries + " tries!");
                db.addWinByTag(pf.tag);
                wi++;
            } else {
                editMessage(chatId, statusMessageId, wg.getBlank() + "\nTries: 0 / " + wg.maxTries, InlineKeyboardMarkup.builder()
                        .keyboard(Collections.singletonList(
                                new InlineKeyboardRow(
                                        InlineKeyboardButton.builder()
                                                .text("Give up")
                                                .callbackData("GIVE_UP")
                                                .build()
                                )
                        ))
                        .build()
                );
            }

            if (wg.tries == wg.maxTries) {
                giveup(chatId);
            }
        }
    }

    private void help(Long chatId) {
        send(chatId, "Here is the list of commands for the WordleVS bot");
        send(chatId, """
                /help - Get the WordleVS's command list
                /profile - Get your WordleVS profile informations
                /create_profile - Create your WordleVS profile
                /edit_profile - Edit your WordleVS profile
                /delete_profile - Delete your WordleVS profile
                /leaderboard - Get the list of the 10 best WordleVS players\s
                /play - Play traditional Wordle
                /play_variant - Play Wordle with custom lengths and tries
                /give_up - Give up your current Wordle game
                """);
    }

    private void leaderboard(Long chatId) {
        send(chatId, db.getAll());
    }

    private void play_variant(Long chatId, Message msg) throws Exception {
        waitingForSettingsInput = true;

        send(chatId, """
                To play a variant game you must send ONE message with:
                
                Max_tries, Word_length
                
                • Max_tries must be greater than or equal to 1
                • Word_length must be at least 4
                
                Example:
                6,7
                """);
    }

    private void handlePlayVariant(Message msg) throws Exception {
        Long chatId = msg.getChatId();
        String text = msg.getText().trim();

        waitingForSettingsInput = false;
        String[] parts = text.split(",");
        if (parts.length != 2) {
            send(chatId, "❌ Invalid format. Use:\n6,7");
            return;
        }

        int mt = Integer.parseInt(parts[0].trim());
        int l = Integer.parseInt(parts[1].trim());

        // 🔎 LENGTH
        if (l < 4 || l > 10) {
            send(chatId, "❌ Invalid word length");
            return;
        }

        // 🔎 MAX TRIES
        if (mt < 0) {
            send(chatId, "❌ The max number of tries must be at least 1");
            return;
        }

        startGame(chatId, mt, pf.favlang, l);
    }

    private void handleProfileCreation(Message msg) throws Exception {
        Long chatId = msg.getChatId();
        String telegramUsername = msg.getFrom().getUserName();
        String text = msg.getText().trim();

        waitingForProfileInput = false;

        String[] parts = text.split(",");

        if (parts.length != 3) {
            send(chatId, "❌ Invalid format. Use:\nWizard,1234,it");
            return;
        }

        String usernameWordle = parts[0].trim();
        String tag = parts[1].trim();
        String lang = parts[2].trim().toLowerCase();


        if (usernameWordle.length() < 3 || usernameWordle.length() > 20) {
            send(chatId, "❌ Invalid Wordle username length");
            return;
        }


        if (!tag.matches("[a-zA-Z0-9]{4}")) {
            send(chatId, "❌ Tag must be exactly 4 letters or numbers");
            return;
        }


        if (!List.of("en", "es", "it", "de", "fr").contains(lang)) {
            send(chatId, "❌ Invalid language. Choose: en/es/it/de/fr");
            return;
        }


        boolean created = db.createPlayer(
                tag,
                usernameWordle,
                lang,
                telegramUsername,
                ma,
                wi,
                chatId
        );

        if (!created) {
            send(chatId, "❌ Tag or Telegram account already registered");
            return;
        }

        send(chatId, "✅ Profile created successfully!");
        pf = new WordleProfile(tag, usernameWordle, lang, telegramUsername, 0, 0);
        System.out.println(db.getPlayerByTag(tag));
        System.out.println(db.getPlayerByTelegramUsername(telegramUsername));
    }


    private void create_profile(Long chatId) {
        waitingForProfileInput = true;

        send(chatId, """
                To create your profile send ONE message with:
                
                Wordle_username,Wordle_tag,Favourite_language
                
                • tag must be exactly 4 letters/numbers
                • language: en / es / it / de / fr
                
                Example:
                Wizard,1234,it
                """);
    }

    // ===================== PROFILE =====================
    private void profile(Message msg, Long chatId, Long userId) {
        String text = pf.getProfile(msg.getFrom());

        try {
            GetUserProfilePhotos getPhotos = GetUserProfilePhotos.builder()
                    .userId(userId)
                    .limit(1)
                    .build();

            UserProfilePhotos photos = telegramClient.execute(getPhotos);

            if (photos.getTotalCount() > 0) {
                PhotoSize bestPhoto = photos.getPhotos().get(0).getLast();

                telegramClient.execute(
                        SendPhoto.builder()
                                .chatId(chatId.toString())
                                .photo(new InputFile(bestPhoto.getFileId()))
                                .caption(text)
                                .replyMarkup(profileKeyboard())
                                .build()
                );
                return;
            }

        } catch (TelegramApiException e) {
            if (!e.getMessage().contains("404")) {
                e.printStackTrace();
            }
        }

        send(chatId, text);
    }


    private InlineKeyboardMarkup profileKeyboard() {
        InlineKeyboardRow r1 = new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("✏️ Edit profile")
                        .callbackData("EDIT_PROFILE")
                        .build()
        );
        InlineKeyboardRow r2 = new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("🗑 Delete profile")
                        .callbackData("DELETE_PROFILE")
                        .build()
        );
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(r1, r2))
                .build();
    }

    // ===================== GAME =====================
    private void startGame(Long chatId, int maxTries, String language, int word_length) throws Exception {
        wg = new WordleGame(maxTries, language, word_length);
        db.addMatchByTag(pf.tag);
        ma++;
        send(chatId, "🎮 Wordle started!");

        statusMessageId = send(chatId, wg.getBlank() + "\nTries: 0 / " + maxTries,
                InlineKeyboardMarkup.builder()
                        .keyboard(Collections.singletonList(
                                new InlineKeyboardRow(
                                        InlineKeyboardButton.builder()
                                                .text("Give up")
                                                .callbackData("GIVE_UP")
                                                .build()
                                )
                        ))
                        .build()
        );

        System.out.println("DEBUG word: " + wg.word);
    }


    private void giveup(Long chatId) throws TelegramApiException {
        if (wg != null && (wg.playing)) {
            if (Objects.equals(pf.favlang, "en"))
                DefinitionGif(chatId, "crying", "The word was: " + wg.word);
            else
                sendGif(chatId, "crying", "The word was: " + wg.word);

            wg.playing = false;
        } else {
            send(chatId, "You're not in a Wordle game!");
        }
    }

    // ===================== SEND / EDIT =====================
    private int send(Long chatId, String text) {
        try {
            Message m = telegramClient.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .build()
            );
            return m.getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int send(Long chatId, String text, InlineKeyboardMarkup kb) {
        try {
            Message m = telegramClient.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .replyMarkup(kb)
                            .build()
            );
            return m.getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void editMessage(Long chatId, int messageId, String text, InlineKeyboardMarkup kb) {
        try {
            telegramClient.execute(
                    EditMessageText.builder()
                            .chatId(chatId.toString())
                            .messageId(messageId)
                            .text(text)
                            .replyMarkup(kb)
                            .build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // ===================== GIF =====================
    private void sendGif(Long chatId, String query, String caption) {
        try {
            String encoded, q, c;
            JsonNode gifUrl = null;
            q = query;
            c = caption;
            do {
                encoded = URLEncoder.encode(q, "UTF-8");
                String url = "https://api.giphy.com/v1/gifs/search?api_key=" +
                        GIPHY_API_KEY + "&q=" + encoded + "&limit=1&offset=" +
                        random.nextInt(20);

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String json = br.readLine();

                ObjectMapper mapper = new ObjectMapper();
                try {
                    gifUrl = mapper.readTree(json)
                            .get("data").get(0)
                            .get("images").get("original").get("url");
                } catch (Exception e) {
                    q = "dancing";
                    c = "\uD83E\uDD73\uD83E\uDD73\uD83E\uDD73";
                }
            } while (gifUrl == null);

            InputFile Gif = new InputFile(gifUrl.asText());

            telegramClient.execute(
                    SendAnimation.builder()
                            .chatId(chatId.toString())
                            .animation(Gif)
                            .caption(c)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendGif(Long chatId, String query, String caption, InlineKeyboardMarkup kb) {
        try {
            String encoded, q, c;
            JsonNode gifUrl = null;
            q = query;
            c = caption;
            do {
                encoded = URLEncoder.encode(q, "UTF-8");
                String url = "https://api.giphy.com/v1/gifs/search?api_key=" +
                        GIPHY_API_KEY + "&q=" + encoded + "&limit=1&offset=" +
                        random.nextInt(20);

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String json = br.readLine();

                ObjectMapper mapper = new ObjectMapper();
                try {
                    gifUrl = mapper.readTree(json)
                            .get("data").get(0)
                            .get("images").get("original").get("url");
                } catch (Exception e) {
                    q = "dancing";
                    c = "\uD83E\uDD73\uD83E\uDD73\uD83E\uDD73";
                }
            } while (gifUrl == null);

            InputFile Gif = new InputFile(gifUrl.asText());

            telegramClient.execute(
                    SendAnimation.builder()
                            .chatId(chatId.toString())
                            .animation(Gif)
                            .caption(c)
                            .replyMarkup(kb)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DefinitionGif(Long chatId, String query, String caption) {
        sendGif(chatId, query, caption, InlineKeyboardMarkup.builder()
                .keyboard(Collections.singletonList(
                        new InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                        .text("Do you want to know the definition?")
                                        .callbackData("DEFINITION")
                                        .build()
                        )
                ))
                .build()
        );
    }
}
