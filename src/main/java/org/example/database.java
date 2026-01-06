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
    }

    public String select(){
        return "";
    }
/*
    public String selectAll() {
        String result = "";

        //Controlla connessione al database
        try {
            if(connection == null || !connection.isValid(5)){
                System.err.println("Errore di connessione al database");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Errore di connessione al database");
            return null;
        }

        String query = "SELECT * FROM menu";


        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet piatti = statement.executeQuery();
            while (piatti.next()) {
                result += piatti.getString("id") + "\t";
                result += piatti.getString("piatto") + "\t";
                result += piatti.getString("prezzo") + "\t";
                result += piatti.getString("quantita") + "\n";
            }


        } catch (SQLException e) {
            System.err.println("Errore di query: " + e.getMessage());
            return null;
        }


        return result;
    }*/

    public boolean insert(String tag, String name, String favlang) {

        //Controlla connessione al database
        try {
            if(connection == null || !connection.isValid(5)){
                System.err.println("Database connection error");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database connection error");
            return false;
        }

        String query = "INSERT INTO users(tag, name, favlang,matches,wins) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, tag);
            statement.setString(2, name);
            statement.setString(3, favlang);
            statement.setInt(4, 0);
            statement.setInt(5, 0);

            statement.executeUpdate();


        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
            return false;
        }


        return true;
    }
}