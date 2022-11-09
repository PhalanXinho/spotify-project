package es.ulpgc.spotify.downloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Album {
    public List<JsonElement> albumName = new ArrayList<>();
    public List<JsonElement> albumId = new ArrayList<>();
    public List<JsonElement> albumReleaseDate = new ArrayList<>();
    public List<JsonElement> albumTotalTracks = new ArrayList<>();
    public List<ArrayList<JsonElement>> albumArtistName = new ArrayList<>();
    public List<JsonElement> auxAlbumArtistName = new ArrayList<>();

    public List<ArrayList<JsonElement>> albumArtistsId = new ArrayList<>();
    public List<JsonElement> auxAlbumArtistId = new ArrayList<>();

    public void insertIntoAlbumTable() throws Exception {
        DatabaseManager dbManager = new DatabaseManager();
        String dbPath = "/home/jiahao/ULPGC/SEGUNDO/PRIMER SEMESTRE/Desarrollo de Aplicaciones para Ciencia de Datos/spoty/spotify/spotify/src/main/spotify.db";
        Connection connection = dbManager.connect(dbPath);
        Statement statement = connection.createStatement();
        getAlbums();
        dbManager.dropTable(statement, "albums");
        dbManager.createAlbumsTable(statement);
        dbManager.insert(statement, "albums", "album_id", albumId);
        updateAll(dbManager, statement);
    }

    public void getAlbums() throws Exception {
        SpotifyAccessor spotifyAccessor = new SpotifyAccessor();
        Controller controller = new Controller();
        for (Map.Entry<String, String> artistId : controller.artistsMap.entrySet()) {
            String json = spotifyAccessor.get("/artists/" + artistId.getValue() + "/albums/", Map.of());
            addToAlbumLists(json);
        }
    }

    private void addToAlbumLists(String json) {
        JsonArray items = JsonParser.parseString(json).getAsJsonObject().get("items").getAsJsonArray();
        for (JsonElement item : items) {
            albumName.add(item.getAsJsonObject().get("name"));
            albumId.add(item.getAsJsonObject().get("id"));
            albumReleaseDate.add(item.getAsJsonObject().get("release_date"));
            albumTotalTracks.add(item.getAsJsonObject().get("total_tracks"));
            JsonArray artists = item.getAsJsonObject().get("artists").getAsJsonArray();
            for (JsonElement artist : artists) {
                auxAlbumArtistName.add(artist.getAsJsonObject().get("name"));
                auxAlbumArtistId.add(artist.getAsJsonObject().get("id"));
            }
            albumArtistName.add(new ArrayList<>(auxAlbumArtistName));
            albumArtistsId.add(new ArrayList<>(auxAlbumArtistId));
            auxAlbumArtistId.clear();
            auxAlbumArtistName.clear();
        }
    }

    private void updateAll(DatabaseManager dbManager, Statement statement) throws SQLException {
        dbManager.updateArrayLists(statement, "albums", "artist_name", albumArtistName, "album_id", albumId);
        dbManager.updateStrings(statement, "albums", "album_name", albumName, "album_id", albumId);
        dbManager.updateArrayLists(statement, "albums", "artist_id", albumArtistsId, "album_id", albumId);
        dbManager.updateStrings(statement, "albums", "release_date", albumReleaseDate, "album_id", albumId);
        dbManager.updateInteger(statement, "albums", "album_total_tracks", albumTotalTracks, "album_id", albumId);
    }
}
