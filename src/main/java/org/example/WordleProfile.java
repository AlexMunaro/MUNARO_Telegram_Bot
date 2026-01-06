package org.example;

import org.telegram.telegrambots.meta.api.objects.User;

class WordleProfile {
    String username;
    String tag;
    public String favlang;
    String flag;
    int games;
    int wins;

    public WordleProfile(String username, String tag, String favlang, int games, int wins) {
        this.username = username;
        this.tag = tag;
        this.favlang = favlang;
        this.games = games;
        this.wins = wins;
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
        return games == 0 ? 0 : (wins * 100.0 / games);
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
                games,
                wins,
                winRate()
        );
    }
}
