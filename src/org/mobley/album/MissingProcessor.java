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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mobley.album.DBUtils.ExecuteQueryCBH;
import org.mobley.album.DBUtils.PreparedStatementCBH;

public class MissingProcessor {

	public static void main(String[] args) {
		File f = new File("albums_not_in_spotify.txt");
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
			addMissingAlbum(url);
		}
	}

	public static void addMissingAlbum(String url) {
		FindAlbumsMissingURLCBH findCBH = new FindAlbumsMissingURLCBH(url);
		try {
			DBUtils.executePreparedStatement(findCBH, false, false, "spotify");
			if(findCBH.isInDB) {
				IncrementRefsCBH incCBH = new IncrementRefsCBH(url);
				DBUtils.executePreparedStatement(incCBH, false, false, "spotify");
			} else {
				AddMissingAlbumCBH addCBH = new AddMissingAlbumCBH(url);
				DBUtils.executePreparedStatement(addCBH, false, false, "spotify");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Set<String> getMissingURLs() {
		return getMissingURLs("music");
	}
	
	public static Set<String> getMissingURLs(String dbName) {
		GetMissingUrlsCBH cbh = new GetMissingUrlsCBH();
		DBUtils.executeQuery(cbh, dbName);
		return cbh.urls;
	}
	
	private static class GetMissingUrlsCBH implements ExecuteQueryCBH {

		private static final String SQL = "SELECT url FROM missing_albums";
		private Set<String> urls = new HashSet<String>();
		
		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while(rs.next()) {
				urls.add(rs.getString(1));
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}
		
	}

	private static class FindAlbumsMissingURLCBH implements PreparedStatementCBH {
		
		private static final String SQL = "SELECT * FROM missing_albums WHERE url = ?";

		private final String url;
		
		private boolean isInDB;
		
		public FindAlbumsMissingURLCBH(String url) {
			super();
			this.url = url;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			isInDB = rs.next();
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, url);
		}

		@Override
		public void setAutoIncrementKey(int key) {
			
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}
		
	}
	
	private static class AddMissingAlbumCBH implements PreparedStatementCBH {
		
		private static final String SQL = "INSERT INTO missing_albums (url, refs) VALUES (?,1)";
		
		private final String url;

		public AddMissingAlbumCBH(String url) {
			super();
			this.url = url;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, url);
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

	private static class IncrementRefsCBH implements PreparedStatementCBH {
		
		private static final String SQL = "UPDATE missing_albums SET refs = refs + 1 WHERE url = ?";

		private final String url;
		
		public IncrementRefsCBH(String url) {
			super();
			this.url = url;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
		}

		@Override
		public String getQuery() {
			return SQL;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, url);
		}

		@Override
		public void setAutoIncrementKey(int key) {
			
		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}
		
	}
}
