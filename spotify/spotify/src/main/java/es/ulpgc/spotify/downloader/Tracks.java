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
import java.util.concurrent.TimeUnit;

public class Tracks {
    public List<String> trackDuration = new ArrayList<>();
    public List<JsonElement> trackDurationMs = new ArrayList<>();
    public List<JsonElement> trackIsExplicit = new ArrayList<>();
    public List<JsonElement> trackId = new ArrayList<>();
    public List<JsonElement> trackName = new ArrayList<>();
    public List<JsonElement> trackNumber = new ArrayList<>();
    public List<JsonElement> auxTrackArtist = new ArrayList<>();
    public List<ArrayList<JsonElement>> trackArtist = new ArrayList<>();
    public List<JsonElement> auxTrackArtistId = new ArrayList<>();
    public List<ArrayList<JsonElement>> trackArtistId = new ArrayList<>();

    public void insertIntoTracksTable() throws Exception {
        String dbPath = "/home/jiahao/ULPGC/SEGUNDO/PRIMER SEMESTRE/Desarrollo de Aplicaciones para Ciencia de Datos/spoty/spotify/spotify/src/main/spotify.db";
        DatabaseManager dbManager = new DatabaseManager();
        Connection connection = dbManager.connect(dbPath);
        Statement statement = connection.createStatement();
        getTracks();
        dbManager.dropTable(statement, "tracks");
        dbManager.createTracksTable(statement);
        dbManager.insert(statement, "tracks", "track_id", trackId);
        updateAll(dbManager, statement);
    }

    public void getTracks() throws Exception {
        SpotifyAccessor spotifyAccessor = new SpotifyAccessor();
        Album album = new Album();
        album.getAlbums();
        for (JsonElement albums : album.albumId) {
            String json = spotifyAccessor.get("/albums/" + albums.getAsString() + "/tracks/", Map.of());
            addToTrackLists(json);
        }
    }

    private void addToTrackLists(String json) {
        JsonArray items = JsonParser.parseString(json).getAsJsonObject().get("items").getAsJsonArray();
        for (JsonElement item : items) {
            trackDuration.add(fromMillisToMinutes(item.getAsJsonObject().get("duration_ms").getAsLong()));
            trackDurationMs.add(item.getAsJsonObject().get("duration_ms"));
            trackIsExplicit.add(item.getAsJsonObject().get("explicit"));
            trackId.add(item.getAsJsonObject().get("id"));
            trackName.add(item.getAsJsonObject().get("name"));
            trackNumber.add(item.getAsJsonObject().get("track_number"));
            JsonArray artists = item.getAsJsonObject().get("artists").getAsJsonArray();
            for (JsonElement artist : artists) {
                auxTrackArtist.add(artist.getAsJsonObject().get("name"));
                auxTrackArtistId.add(artist.getAsJsonObject().get("id"));
            }
            trackArtist.add(new ArrayList<>(auxTrackArtist));
            auxTrackArtist.clear();
            trackArtistId.add(new ArrayList<>(auxTrackArtistId));
            auxTrackArtistId.clear();
        }
    }

    private String fromMillisToMinutes(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
        return (String.format("%d:%02d", minutes, seconds));
    }

    private void updateAll(DatabaseManager dbManager, Statement statement) throws SQLException {
        dbManager.updateArrayLists(statement, "tracks", "artist_name", trackArtist, "track_id", trackId);
        dbManager.updateArrayLists(statement, "tracks", "artist_id", trackArtistId, "track_id", trackId);
        dbManager.updateInteger(statement, "tracks", "track_number", trackNumber, "track_id", trackId);
        dbManager.updateStrings(statement, "tracks", "track_name", trackName, "track_id", trackId);
        dbManager.updateStrings(statement, "tracks", "track_id", trackId, "track_id", trackId);
        dbManager.updateMillis(statement, "tracks", "track_duration", trackDuration, "track_id", trackId);
        dbManager.updateInteger(statement, "tracks", "track_duration_in_ms", trackDurationMs, "track_id", trackId);
        dbManager.updateBoolean(statement, "tracks", "is_explicit", trackIsExplicit, "track_id", trackId);
    }
}
