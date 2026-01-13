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

    public String next(String guess)
    {
        tries++;
        if(guess.equals(word)){
            return "1";
        }

        String[] result = new String[length];
        int yellow = 0;
        //int green = 0;

        /*
        for(int i=0;i<length;i++){
            if(guess.charAt(i)==word.charAt(i)){
                result[i] = "\uD83D\uDFE9 ";
            } else if () {

            }

            result[i] = "⬛ "; //nero
        }


        /*
        for(int i=0; i<length; i++){
            if(guess.charAt(i)==word.charAt(i)){
                word3[i]="\uD83D\uDFE9 "; //verde
                word_temp = word_temp.replace(guess.charAt(i)+"", "");
            }
        }

        for(int i=0;i<length;i++){
            if(word3[i]==null){
                word3[i]= "\uD83D\uDFE8 "; //giallo
            }
        }


        for(int i=0; i<length; i++){
            if(word2.contains(guess.charAt(i)+"")){
                word2 = word2.replace(guess.charAt(i)+"", "");
                System.out.println(word2);
                word3+="\uD83D\uDFE8";
            }else{
                word3+="⬛";
            }
        }
        word3+="\n";
        */

        if(tries==maxTries){
            playing = false;
            return "0";
        }else{
            //answer[tries-1] = word3;
            return AllString();
        }
    }

    String AllString(){
        String msg="";
        for(int i=0;i<maxTries;i++){
            msg+=answer[i];
        }
        return msg;
    }
}
