package es.ulpgc.spotify.downloader;

import com.google.gson.JsonElement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager extends Updater{
    public Connection connect(String dbPath) {
        Connection connection;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public void createArtistsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS artists (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT," +
                "followers INTEGER," +
                "popularity INTEGER" +
                ");");
    }

    public void createAlbumsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS albums (" +
                "artist_id TEXT," +
                "artist_name TEXT," +
                "album_name TEXT," +
                "album_id TEXT," +
                "release_date TEXT," +
                "album_total_tracks INTEGER" +
                ");");
    }

    public void createTracksTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS tracks (" +
                "artist_id TEXT," +
                "artist_name TEXT," +
                "track_number INTEGER," +
                "track_name TEXT," +
                "track_id TEXT," +
                "track_duration TEXT," +
                "track_duration_in_ms INTEGER," +
                "is_explicit TEXT" +
                ");");
    }

    public void dropTable(Statement statement, String tableName) throws SQLException {
        statement.execute(String.format("DROP TABLE IF EXISTS %s;", tableName));
    }

    public void insert(Statement statement, String tableName, String columnName, List<JsonElement> values) throws SQLException {
        for (JsonElement value : values) {
            String stringValue = String.valueOf(value);
            statement.execute(String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnName, stringValue));
        }
    }


}