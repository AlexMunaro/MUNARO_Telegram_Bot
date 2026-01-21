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
    public String guesses = "";

    public String ElaborateGuesses() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < guesses.length(); i++) {
            sb.append(guesses.charAt(i));
            if ((i + 1) % length == 0) {
                sb.append('\n');
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public WordleGame(int maxTries, String language, int word_length) throws Exception {
        this.tries = 0;
        this.maxTries = maxTries;
        this.language = language;
        length = word_length;
        this.word = getRandomWord(word_length);
        playing = true;
        answer = new String[maxTries];


        for (int i = 0; i < this.maxTries; i++) {
            answer[i] = "";
            for (int j = 0; j < length; j++) {
                answer[i] += "w";//"⬜ ";
            }
            //answer[i] += "\n";
        }
    }

    public String getResult() {
        String result = "";
        for (int i = 0; i < this.maxTries; i++) {
            for(char c : answer[i].toCharArray()) {
                switch (c) {
                    case 'w':
                        result+="⬜ "; //bianco
                        break;
                    case 'b':
                        result += "⬛ "; //nero
                        break;
                    case 'y':
                        result += "\uD83D\uDFE8 "; //giallo
                        break;
                    case 'g':
                        result += "\uD83D\uDFE9 "; //verde
                        break;
                }
            }
            result += "\n";
        }
        System.out.println(AllString(answer,length));
        return result;
    }

    private String getRandomWord(int length) throws Exception {
        String url = "https://random-word-api.herokuapp.com/word?length=" + length + "&lang=" + language;
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(
                HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        return res.body().replace("[", "").replace("]", "").replace("\"", "");
    }

    public String next(String guess) {
        tries++;
        guesses+=guess;
        if (guess.equals(word)) {
            playing = false;
            return "1";
        }
        if (tries > maxTries) {
            playing = false;
        }

        String[] result = new String[length];
        boolean[] used = new boolean[length];


        for (int i = 0; i < length; i++) {
            result[i] = "b";//"⬛ ";

        }


        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == word.charAt(i)) {
                result[i] = "g";//"\uD83D\uDFE9 "; //verde
                used[i] = true;
            }
        }


        for (int i = 0; i < length; i++) {
            if (result[i].equals("y")) continue;

            for (int j = 0; j < length; j++) {
                if (!used[j] &&
                        guess.charAt(i) == word.charAt(j) &&
                        guess.charAt(i) != word.charAt(i)) {

                    result[i] = "y";//"\uD83D\uDFE8 ";//giallo
                    used[j] = true;
                    break;
                }
            }
        }
        answer[tries - 1] = AllString(result, length);
        return "0";
    }

    /*
    String AllString(){
        String msg="";
        for(int i=0;i<maxTries;i++){
            msg+=answer[i];
        }
        return msg;
    }
    */
    String AllString(String[] arr, int l) {
        String msg = "";
        for (int i = 0; i < l; i++) {
            msg += arr[i];
        }
        //msg +="\n";
        return msg;
    }

}
