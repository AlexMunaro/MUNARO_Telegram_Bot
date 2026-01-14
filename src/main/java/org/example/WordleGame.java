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
    String[] answer;

    public WordleGame(int maxTries, String language, int word_length) throws Exception {
        this.tries = 0;
        this.maxTries = maxTries;
        this.language = language;
        length = word_length;
        this.word = getRandomWord(word_length);
        playing = true;
        answer = new String[maxTries];

        for(int i=0;i<this.maxTries;i++){
            answer[i] = "";
            for(int j=0;j<length;j++){
                answer[i] += "⬜ ";
            }
            answer[i] += "\n";
        }
    }

    public String getBlank(){
        String result = "";
        for(String s : answer){
            result += s;
        }
        return result;
    }

    private String getRandomWord(int length) throws Exception {
        String url = "https://random-word-api.herokuapp.com/word?length=" + length+"&lang="+language;
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(
                HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        return res.body().replace("[", "").replace("]", "").replace("\"", "");
    }

    public String next(String guess) {

        if (guess.equals(word)) {
            playing = false;
            return "1";
        }


        String[] result = new String[length];
        boolean[] used = new boolean[length];


        for (int i = 0; i < length; i++) {
            result[i] = "⬛ ";
        }


        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == word.charAt(i)) {
                result[i] = "\uD83D\uDFE9 ";
                used[i] = true;
            }
        }


        for (int i = 0; i < length; i++) {
            if (result[i].equals("\uD83D\uDFE8 ")) continue;

            for (int j = 0; j < length; j++) {
                if (!used[j] &&
                        guess.charAt(i) == word.charAt(j) &&
                        guess.charAt(i) != word.charAt(i)) {

                    result[i] = "\uD83D\uDFE8 ";
                    used[j] = true;
                    break;
                }
            }
        }

        answer[tries] = AllString(result, length);
        tries++;
        if (tries > maxTries) {
            playing = false;
        }
        return AllString();
    }


    public String AllString(){
        String msg="";
        for(int i=0;i<maxTries;i++){
            msg+=answer[i];
        }
        return msg;
    }
    String AllString(String[] arr, int l){
        String msg="";
        for(int i=0;i<l;i++){
            msg+=arr[i];
        }
        msg +="\n";
        return msg;
    }

}
