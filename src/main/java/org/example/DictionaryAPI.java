package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DictionaryAPI {

    public static String getDefinition(String word) {
        StringBuilder result = new StringBuilder();

        try {
            String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() != 200) {
                return "❌ No definition found for *" + word + "*";
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONArray root = new JSONArray(response.toString());
            JSONObject entry = root.getJSONObject(0);

            result.append("📖 ").append(word).append("\n\n");

            JSONArray meanings = entry.getJSONArray("meanings");

            for (int i = 0; i < meanings.length(); i++) {
                JSONObject meaning = meanings.getJSONObject(i);
                String partOfSpeech = meaning.getString("partOfSpeech");

                result.append("🔹 ").append(partOfSpeech).append("\n");

                JSONArray definitions = meaning.getJSONArray("definitions");

                for (int j = 0; j < definitions.length(); j++) {
                    JSONObject def = definitions.getJSONObject(j);

                    result.append("• ")
                            .append(def.getString("definition"))
                            .append("\n");

                    JSONArray synonyms = def.getJSONArray("synonyms");
                    if (synonyms.length() > 0) {
                        result.append("  ↳ Synonyms: ");
                        for (int k = 0; k < synonyms.length(); k++) {
                            result.append(synonyms.getString(k));
                            if (k < synonyms.length() - 1) {
                                result.append(", ");
                            }
                        }
                        result.append("\n");
                    }
                }
                result.append("\n");
            }

        } catch (Exception e) {
            return "⚠️ Error retrieving definition";
        }

        return result.toString();
    }
}
