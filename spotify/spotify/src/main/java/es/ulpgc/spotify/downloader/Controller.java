package es.ulpgc.spotify.downloader;

import java.util.HashMap;

public class Controller {
    public HashMap<String, String> artistsMap = new HashMap<>() {{
        put("Rihanna", "5pKCCKE2ajJHZ9KAiaK11H");
        put("Michael Jackson", "3fMbdgg4jU18AjLCKBhRSm");
        put("Eminem", "7dGJo4pcD2V6oG8kP0tJRR");
        put("Ellie Goulding", "0X2BH1fck6amBIoJhDVmmJ");
        put("David Guetta", "1Cs0zKBU1kc0i8ypK3B9ai");
        put("Bruno Mars", "0du5cEVh5yTK9QJze8zA0C");
        put("Alvaro Soler", "2urF8dgLVfDjunO0pcHUEe");
    }};
    Artist artist = new Artist();
    Album album = new Album();
    Tracks tracks = new Tracks();

    public void execute() throws Exception {
        artist.insertIntoArtistTable();
        album.insertIntoAlbumTable();
        tracks.insertIntoTracksTable();
    }
}
