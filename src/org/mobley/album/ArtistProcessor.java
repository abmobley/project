package org.mobley.album;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.mobley.album.DBUtils.ExecuteQueryCBH;
import org.mobley.album.DBUtils.PreparedStatementCBH;
import org.mobley.album.allmusic.AllMusicArtist;
import org.mobley.album.allmusic.AllMusicUtil;

public class ArtistProcessor {

	public static void addArtistsToDBIfNecessary(List<AllMusicArtist> artists) throws Exception {
		for(AllMusicArtist artist : artists) {
		FindArtistByIDCBH cbh = new FindArtistByIDCBH(artist.getId());
		DBUtils.executeQuery(cbh, "spotify");
		if (cbh.add()) {

				System.out.println("Getting document for " + artist.getUrl());
				Document artistDoc = AllMusicUtil.getDocument(artist.getUrl());
				String name = AllMusicUtil.getArtistName(artistDoc);
				AddArtistCBH acbh = new AddArtistCBH(artist.getId(),name, artist.getUrl());
				DBUtils.executePreparedStatement(acbh, false, false, "spotify");
				System.out.println("Added " + artist.getId() + " " + artist.getName());
		}
		}
	}

	private static class FindArtistByIDCBH implements ExecuteQueryCBH {

		private String id;
		private boolean add = false;

		public FindArtistByIDCBH(String id) {
			this.id = id;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			if (rs != null) {
				add = !rs.first();
			}
		}

		@Override
		public String getQuery() {

			return "SELECT * FROM artists WHERE id='" + id + "'";
		}

		public boolean add() {
			return add;
		}
	}
	
	private static class AddArtistCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO artists (id,name,url) VALUES (?,?,?)";
		
		private final String id;
		private final String name;
		private final String url;
		
		public AddArtistCBH(String id, String name, String url) {
			super();
			this.id = id;
			this.name = name;
			this.url = url;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, url);
		}

		@Override
		public String getQuery() {
			// TODO Auto-generated method stub
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
