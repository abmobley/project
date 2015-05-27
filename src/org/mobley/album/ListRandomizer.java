package org.mobley.album;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mobley.album.DBUtils.ExecuteQueryCBH;
import org.mobley.album.DBUtils.PreparedStatementCBH;
import org.mobley.album.data.Album;
import org.mobley.album.data.Track;
import org.mobley.album.spotify.SpotifyLoginServer;

import com.google.api.client.auth.oauth2.Credential;

public class ListRandomizer {

	private static final String POP_ROCK = "MA0000002613";
	private static final String JAZZ = "MA0000002674";
	private static final String ELECTRONIC = "MA0000002572";
	private static final String BLUES = "MA0000002467";
	private static final String COUNTRY = "MA0000002532";
	private static final String FOLK = "MA0000002592";
	private static final String INTERNATIONAL = "MA0000002660";
	private static final String NEW_AGE = "MA0000002745";
	private static final String R_AND_B = "MA0000002809";
	private static final String RAP = "MA0000002816";
	private static final String REGGAE = "MA0000002820";
	private static final String LATIN = "MA0000002692";
	private static final String AVANT_GARDE = "MA0000012170";
	private static final String EASY_LISTENING = "MA0000002567";
	private static final String VOCAL = "MA0000011877";

	private static final double TOTAL_TIME_ALL_LISTS = 10 * 60 * 60 * 1000; // 10
																				// hours
																				// in
																				// millis

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 0 Pop/Rock
		// 1 Jazz
		// 2 Electronic
		// 3 Country/Blues/Folk
		// 4 International/Latin/Avant-Garde
		// 5 R&B/Rap/Reggae
		// 6 New Age/Easy Listening/Vocal
		Credential credential =  SpotifyLoginServer.authorize();
		List<Playlist> playlists = createPlaylistDefinitions();
		GetAlbumsForPlaylistsCBH cbh = new GetAlbumsForPlaylistsCBH(playlists);
		DBUtils.executeQuery(cbh, "spotify");
		GetTracksCBH tracksCbh = new GetTracksCBH();
		DBUtils.executeQuery(tracksCbh, "spotify");
		
		double total = 0;
		for(Playlist playlist : playlists) {
			for(String albumId : playlist.getAlbumIds()) {

				Set<Track> tracks = tracksCbh.getTracks().get(albumId);
				try {
					playlist.addTracks(tracks);
				} catch (Exception e) {
					System.out.println("Exception while processing " + playlist.getTitle());
					e.printStackTrace();
				}
			}
			
			total += playlist.getDuration();
		}

		System.out.println("Total duration " + total);
		Set<Track> tracksUsed = new HashSet<Track>();
		for(Playlist playlist : playlists) {
			System.out.println("Creating " + playlist.getTitle() + ": " + (playlist.getDuration() / total));
		    createPlaylist(playlist, total, tracksUsed, credential);
		}
	}

	private static void createPlaylist(Playlist playlist, double total, Set<Track> tracksUsed, Credential credential) throws Exception {
		double duration = (playlist.getDuration() / total) * TOTAL_TIME_ALL_LISTS;
		double playlistDuration = 0;
		List<Track> tracks = new ArrayList<Track>();
		List<Track> trackList = new ArrayList<Track>();
		Set<String> albumids = new HashSet<String>();
		
		for(Track track : playlist.getTracks()) {
			if(!tracksUsed.contains(track)) {
				int rating = playlist.getAlbumRating(track.getAlbumId());
				if(track.isPick()) rating++;
				for(int i = 0; i < rating; i++) {
					tracks.add(track);
				}
			}
		}
		
		while(playlistDuration < duration) {
			int index = (int) (Math.random() * tracks.size());
			Track track = tracks.get(index);
			if (!trackList.contains(track)) {
				playlistDuration += track.getDuration();
				trackList.add(track);
				albumids.add(track.getAlbumId());
				System.out.println(track.getTitle());
			}
		}

		GetAlbumsCBH albumcbh = new GetAlbumsCBH(
				new ArrayList<String>(albumids));
		DBUtils.executePreparedStatement(albumcbh, false, false, "spotify");
		List<Track> tracksToAdd = new ArrayList<Track>();
		for (Track track : trackList) {
			Album album = albumcbh.albums.get(track.getAlbumId());
			if (track.getSpotifyid() != null
					&& track.getSpotifyid().trim().length() > 0)
				tracksToAdd.add(track);
			System.out.println(track.getTitle() + " " + album.getTitle() + " "
					+ track.getDuration() + " " + track.getSpotifyid());
		}
		
		try {
			SpotifyLoginServer.replacePlaylist(tracksToAdd, playlist.getId(),
					credential);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	private static List<Playlist> createPlaylistDefinitions() {
		List<Playlist> playlists = new ArrayList<Playlist>();
		
		Playlist alternative = new Playlist("Alternative","27Puf66ulrlpZOdkbrXPs1",POP_ROCK);
		alternative.addSubGenre("Alternative/Indie Rock");
		playlists.add(alternative);
		
		Playlist art = new Playlist("Art","42D0dBTFk3AktH8lHNIU7S",POP_ROCK);
		art.addSubGenre("Art-Rock/Experimental");
		playlists.add(art);
		
		Playlist rootsRock = new Playlist("Roots Rock","7py5V2Zi4aA9NDBHJipBXn",POP_ROCK);
		rootsRock.addSubGenre("British Invasion");
		rootsRock.addSubGenre("Psychedelic/Garage");
		rootsRock.addSubGenre("Rock & Roll/Roots");
		playlists.add(rootsRock);
		
		Playlist euro = new Playlist("Euro","5gymiLHAAnMiXYjoRN6KEc",POP_ROCK);
		euro.addSubGenre("Dance");
		euro.addSubGenre("Europop");
		euro.addSubGenre("Foreign Language Rock");
		playlists.add(euro);
		
		Playlist singerSongwriter = new Playlist("Singer/Songwriter","7Fj09d7RiK1lIubaeVeaGa",POP_ROCK);
		singerSongwriter.addSubGenre("Folk/Country Rock");
		singerSongwriter.addSubGenre("Singer/Songwriter");
		playlists.add(singerSongwriter);

		Playlist hard = new Playlist("Hard","26cgnmFkCUdZQBmIWmzfBN",POP_ROCK);
		hard.addSubGenre("Hard Rock");
		hard.addSubGenre("Heavy Metal");
		playlists.add(hard);

		Playlist pop = new Playlist("Pop","60hZAqxfS2MO2Sve06dUFf",POP_ROCK);
		pop.addSubGenre(null);
		pop.addSubGenre("Pop/Rock");
		pop.addSubGenre("Soft Rock");
		playlists.add(pop);

		Playlist punk = new Playlist("Punk/New Wave","5juEZhOo35y0rWnUfYvg7V",POP_ROCK);
		punk.addSubGenre("Punk/New Wave");
		playlists.add(punk);

		Playlist classicJazz = new Playlist("Classic Jazz","0zGZcVZQtYayza2mWOJiX2",JAZZ);
		classicJazz.addSubGenre("Big Band/Swing");
		classicJazz.addSubGenre("New Orleans/Classic Jazz");
		playlists.add(classicJazz);

		Playlist bop = new Playlist("Bop","5lIIdugqgbmjZtHCuRFWYi",JAZZ);
		bop.addSubGenre("Bop");
		bop.addSubGenre("Cool");
		bop.addSubGenre("Hard Bop");
		playlists.add(bop);

		Playlist freeJazz = new Playlist("Free Jazz","745JBMnVGjF1CXt4jEexEN",JAZZ);
		freeJazz.addSubGenre("Free Jazz");
		playlists.add(freeJazz);

		Playlist jazzInstrument = new Playlist("Jazz Instrument","4a8SVneyNUcUV7nSHJoFUz",JAZZ);
		jazzInstrument.addSubGenre(null);
		jazzInstrument.addSubGenre("Jazz Instrument");
		playlists.add(jazzInstrument);

		Playlist fusion = new Playlist("Fusion","4eFb19zrKWy03fIacMncEO",JAZZ);
		fusion.addSubGenre("Fusion");
		fusion.addSubGenre("Latin Jazz/World Fusion");
		fusion.addSubGenre("Soul Jazz/Groove");
		playlists.add(fusion);

		Playlist contemporaryJazz = new Playlist("Contemporary Jazz","5lP2GtoLcLLli638qfvYTF",JAZZ);
		contemporaryJazz.addSubGenre("Contemporary Jazz");
		playlists.add(contemporaryJazz);

		Playlist electronica = new Playlist("Electronica","6H43U64Pa5LFeoEX0qbvmS",ELECTRONIC);
		electronica.addSubGenre(null);
		electronica.addSubGenre("Electronica");
		playlists.add(electronica);

		Playlist downtempo = new Playlist("Downtempo","6xUvIT9qQeb5Q3zYxjZ8ky",ELECTRONIC);
		downtempo.addSubGenre("Downtempo");
		downtempo.addSubGenre("Experimental Electronic");
		playlists.add(downtempo);

		Playlist club = new Playlist("Club","5kaSnMINyEAGtojYJiKV2S",ELECTRONIC);
		club.addSubGenre("House");
		club.addSubGenre("Jungle/Drum'n'Bass");
		club.addSubGenre("Techno");
		club.addSubGenre("Trance");
		playlists.add(club);

		playlists.add(new Playlist("Avant-Garde","76vxgsOISVMwFq2zSKh2sd",AVANT_GARDE));
		playlists.add(new Playlist("Blues","5zHPKA1FqQ54UcVLdMMRX6",BLUES));
		playlists.add(new Playlist("Country","56Yzzf7TGOA2zPHVlgH2KZ",COUNTRY));
		playlists.add(new Playlist("Easy Listening","4d8MzRTfDn2Y5pONIBM8CD",EASY_LISTENING));
		playlists.add(new Playlist("Folk","5tuK0tB9rXdWKOZIZzIebs",FOLK));
		playlists.add(new Playlist("International","3p2tVoRgqiDef0UjPRlJqM",INTERNATIONAL));
		playlists.add(new Playlist("Latin","3FU3zvqL9b8MKeS6DhuCup",LATIN));
		playlists.add(new Playlist("New Age","00NZRiHQQnQnBdYFpXwmy8",NEW_AGE));
		playlists.add(new Playlist("R&B","21YraBzrGv9Xv2AK2UabmZ",R_AND_B));
		playlists.add(new Playlist("Rap","5kMvwZXTlLWAcMwGJqc4uC",RAP));
		playlists.add(new Playlist("Reggae","4A4Be0V5zuy0I5FHi2qsCd",REGGAE));
		playlists.add(new Playlist("Vocal","1AlxCZSyJrHJ0Hda1ikxIt",VOCAL));
		return playlists;
	}
	
	private static class GetAlbumsForPlaylistsCBH implements ExecuteQueryCBH {
		private static final String SQL = "SELECT albums.id, albums.rating, album_genres.genre_id, styles.subgenre FROM albums LEFT JOIN album_genres ON albums.id = album_genres.album_id LEFT JOIN album_styles ON albums.id = album_styles.album_id LEFT JOIN styles ON album_styles.style_id = styles.id";

		private final List<Playlist> playlists;

		public GetAlbumsForPlaylistsCBH(List<Playlist> playlists) {
			super();
			this.playlists = playlists;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while (rs.next()) {
				String id = rs.getString(1);
				int rating = rs.getInt(2);
				String genre = rs.getString(3);
				String subGenre = rs.getString(4);
				for (Playlist playlist : playlists) {
					if (!playlist.hasSubGenres()) {
						if (genre != null
								&& playlist.getGenreId().equals(genre)) {
							playlist.addAlbum(id,rating);
							break;
						}
					} else {
						if (subGenre != null) {
							if (playlist.containsSubGenre(subGenre)) {
								playlist.addAlbum(id,rating);
								break;
							}
						} else {
							if (playlist.getGenreId().equals(genre)
									&& (!playlist.hasSubGenres() || playlist
											.containsSubGenre(null))) {
								playlist.addAlbum(id,rating);
								break;
							}
						}
					}
				}
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}
	}

	private static class GetTracksCBH implements
	ExecuteQueryCBH {

		private static final String SQL = "SELECT tracks.id, tracks.title, tracks.duration, tracks.pick, tracks.album_id, tracks.spotifyid FROM tracks";
		
		private Map<String, Set<Track>> tracks = new HashMap<String,Set<Track>>();

		public Map<String, Set<Track>> getTracks() {
			return tracks;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			int count = 0;
			while (rs.next()) {
				String id = rs.getString(1);
				String title = rs.getString(2);
				int duration = rs.getInt(3);
				boolean isPick = rs.getBoolean(4);
				String albumid = rs.getString(5);
				String spotifyid = rs.getString(6);
				Track track = new Track(id, title, duration, isPick, albumid,
						spotifyid);
				
				Set<Track> tracksForAlbum = tracks.get(track.getAlbumId());
				if(tracksForAlbum == null) {
					tracksForAlbum = new HashSet<Track>();
					tracks.put(track.getAlbumId(), tracksForAlbum);
				}
				tracksForAlbum.add(track);
				count++;
			}
			System.out.println("Processed " + count + " tracks. Albums " + tracks.size());
		}

		@Override
		public String getQuery() {
			return SQL;
		}

	}
	
	

	private static class GetAlbumsCBH implements PreparedStatementCBH {

		private static final String SQL = "SELECT * FROM albums WHERE id IN (";
		private final List<String> ids;

		private final Map<String, Album> albums = new HashMap<String, Album>();

		public GetAlbumsCBH(List<String> ids) {
			super();
			this.ids = ids;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while (rs.next()) {
				String id = rs.getString(1);
				String title = rs.getString(2);
				int rating = rs.getInt(3);
				String spotifyid = rs.getString(4);
				Album album = new Album(id, title, rating, spotifyid);
				albums.put(id, album);
			}
		}

		@Override
		public String getQuery() {
			StringBuilder sb = new StringBuilder(SQL);
			for (int i = 0; i < ids.size(); i++) {
				if (i > 0)
					sb.append(",");
				sb.append("?");
			}
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (int i = 1; i <= ids.size(); i++) {
				pstmt.setString(i, ids.get(i - 1));
			}
		}

		@Override
		public void setAutoIncrementKey(int key) {

		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

	}
	
	private static class Playlist {
		private final String id;
		private final String genreId;
		private final String title;
		private final Set<String> subGenres = new HashSet<String>();
		private Map<String,Integer> albums = new HashMap<String,Integer>();
		private Set<Track> tracks = new HashSet<Track>();
		private long duration = 0;
		
		public Playlist(String title, String id, String genreId) {
			this.id = id;
			this.genreId = genreId;
			this.title = title;
		}
		
		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public long getDuration() {
			return duration;
		}
		
		public void addTracks(Set<Track> tracks) {
			this.tracks.addAll(tracks);
			for(Track t : tracks) {
				duration += t.getDuration();
			}
		}

		public Set<String> getAlbumIds() {
			return albums.keySet();
		}

		public boolean hasSubGenres() {
			return !subGenres.isEmpty();
		}

		public void addSubGenre(String subGenre) {
			subGenres.add(subGenre);
		}
		
		public boolean containsSubGenre(String subGenre) {
			return subGenres.contains(subGenre);
		}
		
		public String getGenreId() {
			return genreId;
		}
		
		public void addAlbum(String albumId, Integer rating) {
			albums.put(albumId, rating);
		}
		
		public int getAlbumRating(String albumId) {
			return albums.get(albumId);
		}

		public Set<Track> getTracks() {
			return tracks;
		}
	}
}
