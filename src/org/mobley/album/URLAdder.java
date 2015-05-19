package org.mobley.album;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mobley.album.DBUtils.ExecuteQueryCBH;

public class URLAdder {
	
	private static final String SQL_SET = "UPDATE albums SET url = '";
	private static final String SQL_WHERE = "' WHERE id = '";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean update = false;
		if(update) {
			updateAlbums();
		} else {
			printAlbums();
		}
	}
	
	private static void updateAlbums() {
		File f = new File("urls.txt");
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
			String[] s = url.split("-");
			String id = s[s.length-1].toUpperCase();
			String sql = SQL_SET + url + SQL_WHERE + id + "'";
			System.out.println("Processing: " + sql);
			DBUtils.executeUpdate(sql);
		}
	}
	
	private static void printAlbums() {
		FindAlbumsMissingURLCBH cbh = new FindAlbumsMissingURLCBH();
		DBUtils.executeQuery(cbh);
	}
	
	private static class FindAlbumsMissingURLCBH implements ExecuteQueryCBH {
		
		private static final String SQL = "SELECT id, title FROM albums WHERE url IS NULL LIMIT 50";

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while(rs.next()) {
			   System.out.println(rs.getString(1) + " " + rs.getString(2));
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}
		
	}

}
