package org.mobley.album;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mobley.album.DBUtils.ExecuteQueryCBH;

public class ListMissingAlbums {

	public static void main(String args[]) {
		ListMissingAlbumsCBH cbh = new ListMissingAlbumsCBH();
		DBUtils.executeQuery(cbh,"spotify");
	}
	
	private static class ListMissingAlbumsCBH implements ExecuteQueryCBH {

		private static final String query = "SELECT * FROM missing_albums ORDER BY refs DESC LIMIT 50";
		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while(rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getInt(2));
			}
		}

		@Override
		public String getQuery() {
			return query;
		}
		
	}
}
