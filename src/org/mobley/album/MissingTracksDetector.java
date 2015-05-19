package org.mobley.album;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mobley.album.DBUtils.ExecuteQueryCBH;

public class MissingTracksDetector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBUtils.executeQuery(new FindAlbumsWithNoTracksCBH(), "spotify");
	}

	private static class FindAlbumsWithNoTracksCBH implements ExecuteQueryCBH {

		private static final String SQL = "SELECT * FROM albums LEFT JOIN tracks ON tracks.album_id = albums.id WHERE tracks.album_id IS NULL";
		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while(rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(5));
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}
		
	}
}
