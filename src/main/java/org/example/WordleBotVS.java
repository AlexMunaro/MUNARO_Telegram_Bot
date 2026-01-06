package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.UserProfilePhotos;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class WordleBotVS implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient = new OkHttpTelegramClient("8430822310:AAH7dni1GTuKRaO4Qdw5prHoC4NjQMQj1pE");
    database db;
    Update update;
    User tgUser;
    String telegramUsername;
    Long userId;
    Long chatId;
    static WordleProfile pf;
    private static final String GIPHY_API_KEY = "1fVdyGPz1ETjWXqiydw2jGEMHHnbAh7Q";
    Random random = new Random();

    boolean playing;
    String word;
    int length, tries, max_t, m_id1, m_id2;

    public WordleBotVS() {
        try {
            db = new database();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            System.exit(-1);
        }
        playing = false;
    }

    @Override
    public void consume(Update update) {

        this.update = update;

        if (tgUser == null) {
            tgUser = update.getMessage().getFrom();
        }
        if (telegramUsername == null) {
            telegramUsername = tgUser.getUserName();
        }
        if (userId == null) {
            userId = tgUser.getId();
        }
        if (chatId == null) {
            chatId = update.getMessage().getChatId();
        }
        if (pf == null) {
            pf = new WordleProfile("alex", "3130", "it", 0, 0);
        }

        try {
            if (update.hasCallbackQuery()) {
                handleMessage(update.getCallbackQuery().getData());
                //return;
            } else {
                handleMessage(update.getMessage().getText());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //if (update.hasMessage() && update.getMessage().hasText()) {
        //}
    }

    private void handleMessage(String msg) throws Exception {
        if(!playing){
            switch (msg) {
                case "/profile":
                    profile();
                    break;
                case "/create_profile":
                    create_profile();
                    break;
                case "/edit_profile":
                    edit_profile();
                    System.out.println("modifica");
                    return;
                case "/delete_profile":
                    delete_profile();
                    System.out.println("elimina");
                    return;
                case "/play":
                    play(6, 5);
                    break;
                case "/play_vs":
                    play_vs();
                    break;
                case "/play_variant":
                    play_variant();
                    break;
                case "/play_variant_vs":
                    play_variant_vs();
                    break;
                case "/giveup":
                    giveup();
                    break;
                default:
                    sendGif("dancing", "");
                    break;
            }
        }else{
            if(msg.startsWith("/")){
                if(msg.equals("/giveup")){
                    giveup();
                }else{
                    send("You can't do other commands while playing!");
                }
            }else{
                if(msg.length()==5){
                    send(playing(msg));
                }else{
                    send("The word must be "+length+" characters long!");
                }

            }
        }

    }

    private String playing(String msg) throws TelegramApiException {
        if(msg.equals(word)){
            sendGif("victory","You won in "+tries+" tries!");
            playing = false;
            return word;
        }
        String word2 = word;

        String word3 = "";
        for(int i=0; i<length; i++){
            if(msg.charAt(i)==word.charAt(i)){
                word3+="\uD83D\uDFE9";
            }else if(word2.contains(msg.charAt(i)+"")){
                word2.trim();
                word3+="\uD83D\uDFE8";
            }else{
                word3+="⬛";
            }
        }
        return word3;
    }


    private InlineKeyboardMarkup profileKeyboard(int i) {
        switch (i) {
            case 1:
                InlineKeyboardRow row1 = new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("✏️ Modifica profilo")
                                .callbackData("/edit_profile")
                                .build()
                );

                InlineKeyboardRow row2 = new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("🗑 Elimina account")
                                .callbackData("/delete_profile")
                                .build()
                );
                return InlineKeyboardMarkup.builder()
                        .keyboard(List.of(row1, row2))
                        .build();
            case 2:
                InlineKeyboardRow row3 = new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("Give up")
                        .callbackData("/giveup")
                        .build());
                return InlineKeyboardMarkup.builder()
                        .keyboard(Collections.singleton(row3))
                        .build();
        }
        return null;
    }


    private void profile() {
        GetUserProfilePhotos getPhotos = GetUserProfilePhotos.builder()
                .userId(userId)
                .limit(1)
                .build();

        String text = pf.getProfile(tgUser);

        try {
            UserProfilePhotos photos = telegramClient.execute(getPhotos);

            if (photos.getTotalCount() > 0) {
                PhotoSize bestPhoto = photos.getPhotos()
                        .get(0)
                        .getLast();

                SendPhoto msg = SendPhoto.builder()
                        .chatId(chatId.toString())
                        .photo(new InputFile(bestPhoto.getFileId()))
                        .caption(text)
                        .parseMode("Markdown")
                        .replyMarkup(profileKeyboard(1))
                        .build();

                telegramClient.execute(msg);
            } else {
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(text)
                        .parseMode("Markdown")
                        .replyMarkup(profileKeyboard(1))
                        .build();

                telegramClient.execute(msg);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    private int send(String text) {
        try {
            telegramClient.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return update.getMessage().getMessageId();
    }

    private int send(String text, int req) {
        try {
            telegramClient.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .replyMarkup(profileKeyboard(req))
                            .build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return update.getMessage().getMessageId();
    }

    private void sendGif(String query, String text) throws TelegramApiException {
        try {
            // Encode testo ricerca
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String urlString =
                    "https://api.giphy.com/v1/gifs/search" +
                            "?api_key=" + GIPHY_API_KEY +
                            "&q=" + encodedQuery +
                            "&limit=1" +
                            "&offset=" + random.nextInt(10) +
                            "&rating=g";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());

            JsonNode data = root.get("data");
            if (data.isEmpty()) {
                send("❌ Nessuna GIF trovata");
                return;
            }

            String gifUrl = data.get(0)
                    .get("images")
                    .get("original")
                    .get("url")
                    .asText();

            // Invia GIF su Telegram
            SendAnimation animation = SendAnimation.builder()
                    .chatId(chatId.toString())
                    .animation(new InputFile(gifUrl))
                    .caption(text)
                    .build();

            telegramClient.execute(animation);

        } catch (Exception e) {
            e.printStackTrace();
            send("❌ Errore nel recupero della GIF");
        }
    }

    private void create_profile() {
        do {
            send(
                    """
                            Insert your username followed by an unique 4 characters tag
                            special characters such as !"£$%&/()=#]@ are not allowed
                            Examples:
                            
                            """);
        } while (true);
    }

    private void edit_profile() {
        send("modifica");
    }

    private void delete_profile() {
        send("delete profile");
    }

    private void play(int t, int l) throws Exception {
        //sendGif("/gif dancing","congrats");
        //sendGif("/gif crying","nice try");
        send("Hi Wordler, ready to get started?");
        send("Find the secret " + l + " letters long word in " + t + " or less tries");
        word = getRandomWord(5);
        playing = true;
        tries = 0;
        max_t = t;
        length = l;
        m_id1 = send("Current tries: 0 out of " + max_t,2);
        System.out.println(word);
    }

    private void play_variant() {

    }

    private void play_vs() {

    }

    private void play_variant_vs() {

    }

    private void giveup() throws TelegramApiException {
        if(playing){
            sendGif("crying","The word was "+word);
            playing = false;
        }else{
            send("You aren't playing right now!");
        }
        
    }
    public static String getRandomWord(int length) throws Exception {
        String url = "https://random-word-api.herokuapp.com/word?length=" + length + "&lang="+pf.favlang;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        return body.replace("[", "")
                .replace("]", "")
                .replace("\"", "");
    }
}
