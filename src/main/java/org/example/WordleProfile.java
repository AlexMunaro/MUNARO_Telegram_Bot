package org.example;

import org.telegram.telegrambots.meta.api.objects.User;

class WordleProfile {
    public String username;
    public String tag;
    public String favlang;
    public String flag;
    public int matches;
    public int wins;
    public String telegram_username;


    public WordleProfile(String tag,String username, String favlang, String telegram_username, int matches, int wins) throws Exception {
        this.username = username;
        this.tag = tag;
        this.favlang = favlang;
        this.matches = matches;
        this.wins = wins;
        this.telegram_username = telegram_username;
        switch (favlang) {
            case "en":
                flag = "\uD83C\uDDEC\uD83C\uDDE7";
                break;
                case "es":
                flag = "\uD83C\uDDEA\uD83C\uDDF8";
                break;
                case "it":
                flag = "\uD83C\uDDEE\uD83C\uDDF9";
                break;
                case "de":
                flag = "\uD83C\uDDE9\uD83C\uDDEA";
                break;
                case "fr":
                flag = "\uD83C\uDDEB\uD83C\uDDF7";
                break;
        }
    }

    double winRate() {
        return matches == 0 ? 0 : (wins * 100.0 / matches);
    }

    public String getProfile(User tgUser) {
        return String.format(
                "👤 *PROFILE*\n\n" +
                        "🧑 Telegram: @%s\n" +
                        "🎮 Wordle: %s\n" +
                        "🔖 Tag: #%s\n" +
                        "%s: %s\n\n" +
                        "📊 *Stats*\n" +
                        "Matches: %d\n" +
                        "Wins: %d\n" +
                        "Win rate: %.1f%%",
                tgUser.getUserName(),
                username,
                tag,
                flag,
                favlang,
                matches,
                wins,
                winRate()
        );
    }

}
