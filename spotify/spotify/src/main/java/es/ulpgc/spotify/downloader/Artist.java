package es.ulpgc.spotify.downloader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Artist {
    public List<JsonElement> artistsNames = new ArrayList<>();
    public List<JsonElement> artistsIds = new ArrayList<>();
    public List<JsonElement> artistsFollowers = new ArrayList<>();
    public List<JsonElement> artistsPopularity = new ArrayList<>();

    public void insertIntoArtistTable() throws Exception {
        DatabaseManager dbManager = new DatabaseManager();
        String dbPath = "/home/jiahao/ULPGC/SEGUNDO/PRIMER SEMESTRE/Desarrollo de Aplicaciones para Ciencia de Datos/spoty/spotify/spotify/src/main/spotify.db";
        Connection connection = dbManager.connect(dbPath);
        Statement statement = connection.createStatement();
        getArtist();
        dbManager.dropTable(statement, "artists");
        dbManager.createArtistsTable(statement);
        dbManager.insert(statement, "artists", "id", artistsIds);
        updateAll(dbManager, statement);
    }

    public void getArtist() throws Exception {
        SpotifyAccessor spotifyAccessor = new SpotifyAccessor();
        Controller controller = new Controller();
        for (Map.Entry<String, String> artistId : controller.artistsMap.entrySet()) {
            String json = spotifyAccessor.get("/artists/" + artistId.getValue(), Map.of());
            addToArtistLists(json);
        }
    }

    private void addToArtistLists(String json) {
        artistsNames.add(JsonParser.parseString(json).getAsJsonObject().get("name"));
        artistsIds.add(JsonParser.parseString(json).getAsJsonObject().get("id"));
        artistsPopularity.add(JsonParser.parseString(json).getAsJsonObject().get("popularity"));
        artistsFollowers.add(JsonParser.parseString(json).getAsJsonObject().get("followers").getAsJsonObject().get("total"));
    }

    private void updateAll(DatabaseManager dbManager, Statement statement) throws SQLException {
        dbManager.updateStrings(statement, "artists", "name", artistsNames, "id", artistsIds);
        dbManager.updateInteger(statement, "artists", "followers", artistsFollowers, "id", artistsIds);
        dbManager.updateInteger(statement, "artists", "popularity", artistsPopularity, "id", artistsIds);
    }
}
