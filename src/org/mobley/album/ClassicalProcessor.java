package org.mobley.album;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.mobley.album.DBUtils.ExecuteQueryCBH;
import org.mobley.album.DBUtils.PreparedStatementCBH;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicUtil;
import org.mobley.album.data.Album;

public class ClassicalProcessor {

	public static class GetClassicalAlbumsCBH implements ExecuteQueryCBH {

		private static final String SQL = "select id,title,rating,url,spotifyid from classical_albums";
		private List<Album> albums = new ArrayList<Album>();
		
		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while(rs.next()) {
				String id = rs.getString(1);
				String title = rs.getString(2);
				int rating = rs.getInt(3);
				String url = rs.getString(4);
				String spotifyid = rs.getString(5);
				Album album = new Album(id,title,rating,spotifyid);
				album.setUrl(url);
				albums.add(album);
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		public List<Album> getAlbums() {
			return albums;
		}

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		updateAlbums();
		//selectAlbum();
	}
	
	
	private static void updateAlbums() {
		File f = new File("classical.txt");
		List<String> urls = new ArrayList<String>();

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

		for(String url : urls) {

			Document doc = null;

			System.out.println("Processing: " + url);
			String[] tokens = url.split(" ");
			url = tokens[0];
			try {
				doc = AllMusicUtil.getDocument(url);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (doc != null) {

				try {
					AllMusicAlbum album = AllMusicUtil.getAlbum(doc);
					if(tokens.length > 1)
					   album.setSpotifyId(tokens[1]);

					if (album != null) {
						AddAlbumCBH cbh = new AddAlbumCBH(album);
						DBUtils.executePreparedStatement(cbh, false, false, "spotify");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void selectAlbum() throws Exception
	{

		List<Album> albums = getAlbums();
		List<Album> allAlbums = new ArrayList<Album>();
		for(Album album : albums) {
				int n = album.getRating();
				for(int j = 0; j < n; j++) {
					allAlbums.add(album);
				}
			}
		
			int index = (int)(Math.random() * allAlbums.size());
			Album album = allAlbums.get(index);
			
			System.out.println(album.getTitle() + " " + album.getUrl() + " " + album.getSpotifyid());
	}

	private static List<Album> getAlbums() throws Exception {
		GetClassicalAlbumsCBH cbh = new GetClassicalAlbumsCBH();
		DBUtils.executeQuery(cbh,"spotify");
		return cbh.getAlbums();
	}
	
	private static class AddAlbumCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO classical_albums (id,title,rating,url,spotifyid,image) VALUES (?,?,?,?,?,?)";
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
}
