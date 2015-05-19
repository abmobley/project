package org.mobley.album;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.mobley.album.DBUtils.PreparedStatementCBH;
import org.mobley.album.OwnedProcessor.OwnedTrack;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicArtist;
import org.mobley.album.allmusic.AllMusicDisc;
import org.mobley.album.allmusic.AllMusicTrack;
import org.mobley.album.allmusic.AllMusicUtil;
import org.mobley.album.spotify.SpotifyAlbum;
import org.mobley.album.spotify.SpotifyAlbumSearchResult;
import org.mobley.album.spotify.SpotifyTrack;
import org.mobley.album.spotify.SpotifyUtil;

public class AlbumProcessor {

	public static void main(String[] args) {
		File f = new File("albums.txt");
		List<String> urls = new ArrayList<String>();
		Console console = new Console();

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				urls.add(line);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (String url : urls) {
			Document doc = null;
			System.out.println("Processing: " + url);
			
			
			try {
				doc = AllMusicUtil.getDocument(url);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (doc != null) {

				AllMusicAlbum album = null;

				try {
					album = AllMusicUtil.getAlbum(doc);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (album != null) {

					boolean addToDB = addAlbumToDB(doc, album);
					
					if(!addToDB) {
						int stratInt = 0;
						while(stratInt == 0) {
						String strategy = console.readLine("Choose strategy: 1 FORCE, 2 RELEASE, 3 OWNED, 4 OWNED RELEASE, 5 COMBINE, 6 REORDER, 7 RANGE, 8 COMBINE OWNED, 9 SKIP");
						
						try {
							stratInt = Integer.parseInt(strategy);
						} catch (NumberFormatException e) {
							
						}
						
						switch(stratInt) {
						case 1:
							stratInt = forceAddtoDB(console, album, doc);
							break;
						case 2:
							stratInt = addFromRelease(console, album, doc);
							break;
						case 3:
							stratInt = addOwned(console, album, doc);
						break;
						case 4:
							stratInt = addOwnedFromRelease(console, album, doc);
							break;
						case 5:
							stratInt = combineSpotify(console, album, doc);
							break;
						case 6:
							stratInt = reorderSpotify(console, album, doc);
							break;
						case 7:
							stratInt = rangeOfSpotify(console, album, doc);
							break;
						case 8:
							stratInt = combineOwned(console, album, doc);
						break;
						case 9:
							break;
						default:
							System.out.println("Invalid strategy.");
							stratInt = 0 ;
							break;
						}
						
						}
					}
				}
			}
		}
		
	}

	public static boolean addAlbumToDB(Document doc, AllMusicAlbum album) {
		boolean addToDB = false;
		try {
			if (album.getSpotifyId() != null) {
				SpotifyAlbum spotifyAlbum = SpotifyUtil
						.getAlbum(SpotifyUtil
								.getSpotifyAlbumURI(album
										.getSpotifyId()));

				if (spotifyAlbum != null) {
					if (SpotifyUtil.isAvailableInUS(spotifyAlbum
							.getAvailable_markets())) {
						addToDB = AlbumComparator
								.compareSpotifyIds(album,
										spotifyAlbum);
						if (addToDB) {
							addToDB = setTrackDurations(album,
									spotifyAlbum);
							if (!addToDB) {
								System.out
										.println("Failed to set track durations from SpotifyAlbum.");
							}
						} else {
							System.out
									.println("AllMusicAlbum and SpotifyAlbum do not have same spotifyids");
						}
					} else {
						System.out
								.println("Spotify album is not avaialable in US market: "
										+ "spotify:album:" + album.getSpotifyId());
					}
				} else {
					System.out
							.println("Could not find spotify album from id "
									+ "spotify:album:" + album.getSpotifyId());
				}
			} 
			
			if(!addToDB){
				SpotifyAlbumSearchResult result = SpotifyUtil
						.searchAlbum(album.getTitle(), album
								.getArtists().get(0).getName());
				if (result != null && result.getAlbums().getItems() != null && result.getAlbums().getItems().length > 0 ) {
					for (SpotifyAlbum sa : result.getAlbums()
							.getItems()) {
						SpotifyAlbum spotifyAlbum = SpotifyUtil
								.getAlbum(sa.getHref());
						addToDB = AlbumComparator.compare(album,
								spotifyAlbum);
						if (addToDB) {
							setSpotifyIdsAndDurations(album, spotifyAlbum.getId(),
									SpotifyUtil.getTracks(spotifyAlbum));
							break;
						}
					}
				} else {
					System.out
							.println("Could not find matching spotify album.");
				}
			}

		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (addToDB) {
			try {
				addToDB(album, doc);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					DBUtils.rollbackTransaction("spotify");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				addToDB = false;
			}
		} else {
			addToDB = false;
		}
		return addToDB;
	}
	
	private static int rangeOfSpotify(Console console, AllMusicAlbum album,
			Document doc) {
		SpotifyAlbum spotifyAlbum = null;
		String uri = console.readLine("Enter spotify uri: ");
		try {
			spotifyAlbum = SpotifyUtil
					.getAlbum(SpotifyUtil
							.getSpotifyAlbumURI(uri));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
		if(spotifyAlbum == null) return 0;

		String rangeString = console.readLine("Range: ");
		String[] rangeTokens = rangeString.split("-");
		
		int[] range = new int[2];
		range[0] = Integer.parseInt(rangeTokens[0]);
		range[1] = Integer.parseInt(rangeTokens[1]);
		
		List<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>();
		List<SpotifyTrack> albumTracks = SpotifyUtil.getTracks(spotifyAlbum);
		for(int i = range[0]; i <= range[1]; i++) {
			tracks.add(albumTracks.get(i));
		}

		setSpotifyIdsAndDurations(album, spotifyAlbum.getId(), tracks);
		try {
			addToDB(album, doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 7;
	}

	private static int reorderSpotify(Console console, AllMusicAlbum album,
			Document doc) {

		SpotifyAlbum spotifyAlbum = null;
		String uri = console.readLine("Enter spotify uri: ");
		try {
			spotifyAlbum = SpotifyUtil
					.getAlbum(SpotifyUtil
							.getSpotifyAlbumURI(uri));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
		if(spotifyAlbum == null) return 0;
		
		String order = console.readLine("Order: ");
			String[] orderTokens = order.split(",");
			List<AllMusicTrack> tracks = new ArrayList<AllMusicTrack>();
			for(AllMusicDisc disc : album.getDiscs()) {
				tracks.addAll(disc.getTracks());
			}
			
			List<SpotifyTrack> neworder = new ArrayList<SpotifyTrack>();
			List<SpotifyTrack> oldorder = SpotifyUtil.getTracks(spotifyAlbum);
			for(int i = 0; i < orderTokens.length; i++) {
				int o = Integer.parseInt(orderTokens[i]);
				neworder.add(oldorder.get(o));
			}
			setSpotifyIdsAndDurations(album, spotifyAlbum.getId(), neworder);
			try {
				addToDB(album, doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		return 6;
	}

	private static int combineSpotify(Console console, AllMusicAlbum album,
			Document doc) {
		int num = Integer.parseInt(console.readLine("How many? "));
		if(num == 0)
			return 0;
		
		SpotifyAlbum[] albums = new SpotifyAlbum[num];
		for(int i = 0; i < num; i++) {
		String uri = console.readLine("Enter spotify uri: ");
		try {
			albums[i] = SpotifyUtil
					.getAlbum(SpotifyUtil
							.getSpotifyAlbumURI(uri));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		}
		List<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>();
		for(SpotifyAlbum spotifyAlbum : albums) {
			tracks.addAll(SpotifyUtil.getTracks(spotifyAlbum));
		}
		String id = albums[0].getId();
			setSpotifyIdsAndDurations(album, id,
					tracks);
			try {
				addToDB(album, doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		return 1;
	}

	private static int combineOwned(Console console, AllMusicAlbum album,
			Document doc) {
		int num = Integer.parseInt(console.readLine("How many? "));
		if(num == 0)
			return 0;
		List<OwnedTrack> ownedTracks = new ArrayList<OwnedTrack>();
		for(int i = 0; i < num; i++) {
			String title = album.getTitle();
			String input = console.readLine(title + ": ");
			if(input != null) {
				title = input;
			}
			try {
				ownedTracks.addAll(OwnedProcessor.getOwnedTracks(title));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		List<AllMusicTrack> allMusicTracks = new ArrayList<AllMusicTrack>();

	      for(AllMusicDisc allMusicDisc : album.getDiscs()) {

	         allMusicTracks.addAll(allMusicDisc.getTracks());

	      }

	 
	      for(int i = 0 ; i < allMusicTracks.size() && i < ownedTracks.size(); i++) {

	        System.out.println(allMusicTracks.get(i).getTitle() + "  " + ownedTracks.get(i).getName());

	      }

	      String accept = console.readLine("Accept? ");

	      if(accept.equalsIgnoreCase("y")) {

	         for(int i = 0 ; i < allMusicTracks.size() && i < ownedTracks.size(); i++) {

	            allMusicTracks.get(i).setDuration(ownedTracks.get(i).getTime());

	          }
	         String picks = console.readLine("Enter picks: ");
				if(!picks.equalsIgnoreCase("n")) {
					String[] picksTokens = picks.split(",");
					List<AllMusicTrack> tracks = new ArrayList<AllMusicTrack>();
					for(AllMusicDisc disc : album.getDiscs()) {
						tracks.addAll(disc.getTracks());
					}
					for(String pick : picksTokens) {
						int p = Integer.parseInt(pick);
						tracks.get(p).setPick(true);
					}
				}
				try {
					addToDB(album, doc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
	         return 8;
	      }

		return 0;
	}
	
	private static int addOwnedFromRelease(Console console,  AllMusicAlbum album, Document doc) {
		Document releaseDoc = null;
		try {
			String release = console.readLine("Enter AllMusic release url: ");
			releaseDoc = AllMusicUtil.getDocument(release);
			List<AllMusicDisc> releaseDiscs = AllMusicUtil.getReleaseDiscs(releaseDoc);
			if(releaseDiscs != null) {
				album.setDiscs(releaseDiscs);
			} else {
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return addOwned(console, album, doc);
	}
	
	private static int addOwned(Console console, AllMusicAlbum album,
			Document doc) {
		List<OwnedTrack> ownedTracks = null;
		String title = album.getTitle();
		String input = console.readLine(title + ": ");
		if(input != null) {
			title = input;
		}
		try {
			ownedTracks = OwnedProcessor.getOwnedTracks(title);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		List<AllMusicTrack> allMusicTracks = new ArrayList<AllMusicTrack>();

	      for(AllMusicDisc allMusicDisc : album.getDiscs()) {

	         allMusicTracks.addAll(allMusicDisc.getTracks());

	      }

	 
	      for(int i = 0 ; i < allMusicTracks.size() && i < ownedTracks.size(); i++) {

	        System.out.println(allMusicTracks.get(i).getTitle() + "  " + ownedTracks.get(i).getName());

	      }

	      String accept = console.readLine("Accept? ");

	      if(accept.equalsIgnoreCase("y")) {

	         for(int i = 0 ; i < allMusicTracks.size() && i < ownedTracks.size(); i++) {

	            allMusicTracks.get(i).setDuration(ownedTracks.get(i).getTime());

	          }
	         String picks = console.readLine("Enter picks: ");
				if(!picks.equalsIgnoreCase("n")) {
					String[] picksTokens = picks.split(",");
					List<AllMusicTrack> tracks = new ArrayList<AllMusicTrack>();
					for(AllMusicDisc disc : album.getDiscs()) {
						tracks.addAll(disc.getTracks());
					}
					for(String pick : picksTokens) {
						int p = Integer.parseInt(pick);
						tracks.get(p).setPick(true);
					}
				}
				try {
					addToDB(album, doc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
	         return 4;
	      }

		return 0;
	}

	private static int addFromRelease(Console console, AllMusicAlbum album, Document doc) {

		String uri = console.readLine("Enter spotify uri: ");
		SpotifyAlbum spotifyAlbum;
		Document releaseDoc;
		try {
			spotifyAlbum = SpotifyUtil
					.getAlbum(SpotifyUtil
							.getSpotifyAlbumURI(uri));
			String release = console.readLine("Enter AllMusic release url: ");
			releaseDoc = AllMusicUtil.getDocument(release);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		List<AllMusicDisc> releaseDiscs = AllMusicUtil.getReleaseDiscs(releaseDoc);
		if(releaseDiscs != null) {
			album.setDiscs(releaseDiscs);
		if(spotifyAlbum != null) {
			setSpotifyIdsAndDurations(album, spotifyAlbum.getId(),
					SpotifyUtil.getTracks(spotifyAlbum));
			String picks = console.readLine("Enter picks: ");
			if(!picks.equalsIgnoreCase("n")) {
				String[] picksTokens = picks.split(",");
				List<AllMusicTrack> tracks = new ArrayList<AllMusicTrack>();
				for(AllMusicDisc disc : releaseDiscs) {
					tracks.addAll(disc.getTracks());
				}
				for(String pick : picksTokens) {
					int p = Integer.parseInt(pick);
					tracks.get(p).setPick(true);
				}
			}
			try {
				addToDB(album, doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		else
		{
			return 0;
		}
		}
		return 2;
	}

	private static int forceAddtoDB(Console console, AllMusicAlbum album, Document doc) {
		
		String uri = console.readLine("Enter spotify uri: ");
			SpotifyAlbum spotifyAlbum;
			try {
				spotifyAlbum = SpotifyUtil
						.getAlbum(SpotifyUtil
								.getSpotifyAlbumURI(uri));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			if(spotifyAlbum != null) {
				
				setSpotifyIdsAndDurations(album, spotifyAlbum.getId(),
						SpotifyUtil.getTracks(spotifyAlbum));
				try {
					addToDB(album, doc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
			}
			return 1;
	}

	private static void addToDB(AllMusicAlbum album, Document doc) throws Exception  {
		DBUtils.startTransaction("spotify");

		try {
			addIdsToDBIfNecessary(album.getGenres(), "genres",
					"genre", doc);
			addIdsToDBIfNecessary(album.getStyles(), "styles",
					"styles", doc);
			addIdsToDBIfNecessary(album.getThemes(), "themes",
					"themes", doc);
			addIdsToDBIfNecessary(album.getMoods(), "moods",
					"moods", doc);

			ArtistProcessor.addArtistsToDBIfNecessary(album
					.getArtists());

			AddAlbumCBH cbh = new AddAlbumCBH(album);
			DBUtils.executePreparedStatement(cbh, false, false,
					"spotify");

			List<AllMusicTrack> tracks = new ArrayList<AllMusicTrack>();
			for (AllMusicDisc disc : album.getDiscs()) {
				tracks.addAll(disc.getTracks());
			}
			TrackProcessor.addTracksToDB(tracks, album.getId());

			addAlbumGenresStylesMoodsThemes(album);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DBUtils.rollbackTransaction("spotify");
			throw e;
		}

		DBUtils.commitTransaction("spotify");
	}

	public static void addAlbumGenresStylesMoodsThemes(AllMusicAlbum album)
			throws Exception {
		if (album.getGenres() != null
				&& album.getGenres().length > 0) {
			AddAlbumGenresCBH gCbh = new AddAlbumGenresCBH(
					Arrays.asList(album.getGenres()),
					album.getId());
			DBUtils.executePreparedStatement(gCbh, false,
					true, "spotify");
		}

		if (album.getStyles() != null
				&& album.getStyles().length > 0) {
			AddAlbumStylesCBH gCbh = new AddAlbumStylesCBH(
					Arrays.asList(album.getStyles()),
					album.getId());
			DBUtils.executePreparedStatement(gCbh, false,
					true, "spotify");
		}

		if (album.getThemes() != null
				&& album.getThemes().length > 0) {
			AddAlbumThemesCBH gCbh = new AddAlbumThemesCBH(
					Arrays.asList(album.getThemes()),
					album.getId());
			DBUtils.executePreparedStatement(gCbh, false,
					true, "spotify");
		}

		if (album.getMoods() != null
				&& album.getMoods().length > 0) {
			AddAlbumMoodsCBH gCbh = new AddAlbumMoodsCBH(
					Arrays.asList(album.getMoods()),
					album.getId());
			DBUtils.executePreparedStatement(gCbh, false,
					true, "spotify");
		}

		if (album.getArtists() != null
				&& !album.getArtists().isEmpty()) {
			AddAlbumArtistsCBH aCbh = new AddAlbumArtistsCBH(
					album.getArtists(), album.getId());
			DBUtils.executePreparedStatement(aCbh, false,
					true, "spotify");
		}
	}
	
	

	private static void setSpotifyIdsAndDurations(AllMusicAlbum album,
			String spotifyAlbumId, List<SpotifyTrack> spotifyTracks) {
		album.setSpotifyId(spotifyAlbumId);
		int i = 0;
		for (AllMusicDisc disc : album.getDiscs()) {
			for (int j = 0; j < disc.getTracks().size() && i < spotifyTracks.size(); j++, i++) {
				SpotifyTrack spotifyTrack = spotifyTracks.get(i);
				AllMusicTrack track = disc.getTracks().get(j);
				track.setSpotifyId(spotifyTrack.getId());
				track.setDuration(spotifyTrack.getDuration_ms());
			}
		}
	}

	private static boolean setTrackDurations(AllMusicAlbum album,
			SpotifyAlbum spotifyAlbum) {
		boolean success = true;
		Map<String, Integer> durations = new HashMap<String, Integer>();
		for (SpotifyTrack track : SpotifyUtil.getTracks(spotifyAlbum)) {
			durations.put(track.getId(), track.getDuration_ms());
		}
		for (AllMusicDisc disc : album.getDiscs()) {
			for (AllMusicTrack track : disc.getTracks()) {
				Integer d = durations.get(track.getSpotifyId());
				if (d != null) {
					track.setDuration(d);
				} else {
					success = false;
				}
			}
		}
		return success;
	}

	public static void addIdsToDBIfNecessary(String[] ids, String table,
			String className, Document albumDoc) throws Exception {
		if (ids == null || ids.length == 0)
			return;

		GetIdsNotInDBCBH cbh = new GetIdsNotInDBCBH(ids, table);
		DBUtils.executePreparedStatement(cbh, false, false, "spotify");
		Set<String> idsToAdd = cbh.getIDs();
		if (!idsToAdd.isEmpty()) {

			try {
				Map<String, String> names = AllMusicUtil.getNamesFromAlbumDoc(
						albumDoc, className);
				for (String id : idsToAdd) {
					String name = names.get(id);
					if (name != null) {
							AddIdCBH aicbh = new AddIdCBH(table,id,name);
							DBUtils.executePreparedStatement(aicbh, false, false,"spotify");
					} else {
						throw new Exception("Could not find name for " + id);
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	private static class AddIdCBH implements PreparedStatementCBH {

		private final String table;
		private final String id;
		private final String name;
		
		
		public AddIdCBH(String table, String id, String name) {
			super();
			this.table = table;
			this.id = id;
			this.name = name;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, id);
			pstmt.setString(2, name);
		}

		@Override
		public String getQuery() {
			return "INSERT INTO " + table
					+ " (id,name) VALUES (?,?)";
		}

		@Override
		public void setAutoIncrementKey(int key) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getAutoIncrementKey() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			
		}
		
	}

	private static class GetIdsNotInDBCBH implements PreparedStatementCBH {

		private static final String SELECT = "SELECT id FROM ";
		private static final String WHERE = " WHERE id in (";
		private String sql = "SELECT id FROM genres WHERE id IN (";
		private String[] ids;
		private Set<String> idsNotInDB = new HashSet<String>();

		public GetIdsNotInDBCBH(String[] ids, String table) {
			this.ids = ids;
			sql = SELECT + table + WHERE;
		}

		public Set<String> getIDs() {
			return idsNotInDB;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (int i = 1; i <= ids.length; i++) {
				pstmt.setString(i, ids[i - 1]);
			}
		}

		@Override
		public String getQuery() {
			String start = sql;
			StringBuilder s = new StringBuilder(start);
			for (int i = 0; i < ids.length; i++) {
				if (i > 0) {
					s.append(',');
				}
				s.append('?');
			}
			s.append(')');
			String query = s.toString();
			return query;
		}

		@Override
		public void setAutoIncrementKey(int key) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getAutoIncrementKey() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			idsNotInDB.addAll(Arrays.asList(ids));
			while (rs.next()) {
				String id = rs.getString(1);
				idsNotInDB.remove(id);
			}
		}

	}

	private static class AddAlbumCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO albums (id,title,rating,url,spotifyid,image) VALUES (?,?,?,?,?,?)";
		private AllMusicAlbum album;

		public AddAlbumCBH(AllMusicAlbum album) {
			super();
			this.album = album;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, album.getId());
			pstmt.setString(2, album.getTitle());
			pstmt.setInt(3, album.getRating());
			pstmt.setString(4, album.getUrl());
			pstmt.setString(5, album.getSpotifyId());
			pstmt.setString(6, album.getImageSrc());
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void setAutoIncrementKey(int key) {
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}

	private static class AddAlbumGenresCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO album_genres (album_id,genre_id) VALUES (?,?)";

		List<String> genreIds;
		String albumId;

		public AddAlbumGenresCBH(List<String> genreIds, String albumId) {
			super();
			this.genreIds = genreIds;
			this.albumId = albumId;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (String genreId : genreIds) {
				pstmt.setString(1, albumId);
				pstmt.setString(2, genreId);
				pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void setAutoIncrementKey(int key) {
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}

	private static class AddAlbumArtistsCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO album_artists (album_id,artist_id) VALUES (?,?)";

		List<AllMusicArtist> artists;
		String albumId;

		public AddAlbumArtistsCBH(List<AllMusicArtist> artists, String albumId) {
			super();
			this.artists = artists;
			this.albumId = albumId;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (AllMusicArtist artist : artists) {
				pstmt.setString(1, albumId);
				pstmt.setString(2, artist.getId());
				pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void setAutoIncrementKey(int key) {
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}

	private static class AddAlbumStylesCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO album_styles (album_id,style_id) VALUES (?,?)";

		List<String> styleIds;
		String albumId;

		public AddAlbumStylesCBH(List<String> styleIds, String albumId) {
			super();
			this.styleIds = styleIds;
			this.albumId = albumId;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (String styleId : styleIds) {
				pstmt.setString(1, albumId);
				pstmt.setString(2, styleId);
				pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void setAutoIncrementKey(int key) {
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}

	private static class AddAlbumThemesCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO album_themes (album_id,theme_id) VALUES (?,?)";

		List<String> themeIds;
		String albumId;

		public AddAlbumThemesCBH(List<String> themeIds, String albumId) {
			super();
			this.themeIds = themeIds;
			this.albumId = albumId;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (String themeId : themeIds) {
				pstmt.setString(1, albumId);
				pstmt.setString(2, themeId);
				pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void setAutoIncrementKey(int key) {
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}

	private static class AddAlbumMoodsCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO album_moods (album_id,mood_id) VALUES (?,?)";

		List<String> moodIds;
		String albumId;

		public AddAlbumMoodsCBH(List<String> moodIds, String albumId) {
			super();
			this.moodIds = moodIds;
			this.albumId = albumId;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (String moodId : moodIds) {
				pstmt.setString(1, albumId);
				pstmt.setString(2, moodId);
				pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void setAutoIncrementKey(int key) {
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}
	
	public static class Console {
	    BufferedReader br;
	    PrintStream ps;

	    public Console(){
	        br = new BufferedReader(new InputStreamReader(System.in));
	        ps = System.out;
	    }

	    public String readLine(String out){
	        ps.format(out);
	        try{
	            return br.readLine();
	        }catch(IOException e)
	        {
	            return null;
	        }
	    }
	}
}
