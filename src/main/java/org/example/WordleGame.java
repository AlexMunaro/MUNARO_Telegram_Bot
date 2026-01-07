package org.example;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WordleGame {
    public int tries;
    public int maxTries;
    String language;
    public String word;
    int length;
    public boolean playing;

    public WordleGame(int maxTries, String language, int word_length) throws Exception {
        this.tries = 0;
        this.maxTries = maxTries;
        this.language = language;
        length = word_length;
        this.word = getRandomWord(word_length);
        playing = true;
    }

    private String getRandomWord(int length) throws Exception {
        String url = "https://random-word-api.herokuapp.com/word?length=" + length;
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(
                HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        return res.body().replace("[", "").replace("]", "").replace("\"", "");
    }

    public String next(String guess)
    {
        tries++;
        if(guess.equals(word)){
            playing = false;
            return "1";
        }
        String word2 = word;

        String word3 = "";
        for(int i=0; i<length; i++){
            if(guess.charAt(i)==word.charAt(i)){
                word3+="\uD83D\uDFE9";
            }else if(word2.contains(guess.charAt(i)+"")){
                word2.trim();
                word3+="\uD83D\uDFE8";
            }else{
                word3+="⬛";
            }
        }
        if(tries==maxTries){
            playing = false;
            return "0";
        }else{
            return word3;
        }
    }
}
