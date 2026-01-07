package org.example;

import java.sql.*;

public class database {
    //connetti
    //query
    private Connection connection;

    public database() throws SQLException {
        String url = "jdbc:sqlite:database/wordle.db";
        connection = DriverManager.getConnection(url);
        System.out.println("Connected to database");

        createTableIfNotExists();
    }


    public boolean deletePlayerByTag(String tag) {
        String sql = "DELETE FROM players WHERE tag = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tag);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS players (
            tag TEXT NOT NULL,
            username_wordle TEXT NOT NULL,
            favlang TEXT NOT NULL,
            matches INTEGER NOT NULL DEFAULT 0,
            wins INTEGER NOT NULL DEFAULT 0,
            telegram_username TEXT NOT NULL UNIQUE,
            PRIMARY KEY (tag)
        );
    """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public boolean createPlayer(
            String tag,
            String usernameWordle,
            String favLang,
            String telegramUsername,
            int m,
            int w
    ) {
        String sql = """
                INSERT INTO players
                (tag, username_wordle, favlang, matches, wins, telegram_username)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tag);
            ps.setString(2, usernameWordle);
            ps.setString(3, favLang);
            ps.setInt(4, m);
            ps.setInt(5, w);
            ps.setString(6, telegramUsername);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Create player error: " + e.getMessage());
            return false;
        }
    }

    public WordleProfile getPlayerByTelegramUsername(String telegramUsername) {
        String sql = "SELECT * FROM players WHERE telegram_username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, telegramUsername);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new WordleProfile(
                        rs.getString("tag"),
                        rs.getString("username_wordle"),
                        rs.getString("favlang"),
                        rs.getString("telegram_username"),
                        rs.getInt("matches"),
                        rs.getInt("wins")

                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAll() {
        String sql = "SELECT * FROM players";
        StringBuilder result = new StringBuilder();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                if (result.length() > 0) {
                    result.append(", "); // separatore tra i tag
                }
                result.append(rs.getString("tag"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching tags: " + e.getMessage());
        }

        return result.toString();
    }


    public WordleProfile getPlayerByTag(String tag) {
        String sql = "SELECT * FROM players WHERE tag = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tag);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new WordleProfile(
                        rs.getString("tag"),
                        rs.getString("username_wordle"),
                        rs.getString("favlang"),
                        rs.getString("telegram_username"),
                        rs.getInt("matches"),
                        rs.getInt("wins")

                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addWinByTag(String tag) {
        String sql = """
        
                UPDATE players
        SET wins = wins + 1
        WHERE tag = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tag);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add win error: " + e.getMessage());
            return false;
        }
    }

    public boolean addMatchByTag(String tag) {
        String sql =
                """
        UPDATE players
        SET matches =
                matches + 1
                   WHERE tag = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tag);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add win error: " + e.getMessage());
            return false;
        }

    }

}
