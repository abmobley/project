package org.mobley.album.allmusic;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mobley.album.DBUtils;
import org.mobley.album.DBUtils.ExecuteQueryCBH;
import org.mobley.album.DBUtils.PreparedStatementCBH;

public class AllMusicGenreProcessor {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		processGenres();
	}
	
	private static void processStylesWithNoSubgenre() throws Exception {
		FindStylesWithNoSubgenreCBH cbh = new FindStylesWithNoSubgenreCBH();
		DBUtils.executeQuery(cbh, "spotify");
		Set<String> styles = cbh.getStyles();
		System.out.println(styles.size());
		
		for(String style : styles) {
			FindAlbumForStyleCBH fasCBH = new FindAlbumForStyleCBH(style);
			DBUtils.executePreparedStatement(fasCBH, false, false,"spotify");
			String url = fasCBH.getUrl();
			String styleUrl = AllMusicUtil.getAlbumStyleURL(url, style);
			if(styleUrl != null) {
				Document styleDoc = null;
				try {
					styleDoc = AllMusicUtil.getDocument(styleUrl);
				} catch (Exception e) {
					
				}
				System.out.println(style);
				if(styleDoc != null) {
					Elements superGenres = styleDoc.select(".genre-name a");
					for(Element superGenre : superGenres) {
						System.out.println(superGenre.ownText());
					}
				}
			}
			System.out.println();
		}
		
	}
	
	
	
	private static void processGenres() throws Exception {
		Document doc = AllMusicUtil
				.getDocument("http://www.allmusic.com/genres");
		Elements elements = doc.select(".genre h3 a");

		for (Element element : elements) {

			String href = element.attr("href");

	         String genreId = AllMusicUtil.getIdFromUrl(href);

	         System.out.println("Processing genre: " + href);
	         Document genreDoc = AllMusicUtil.getDocument(href);
			DBUtils.startTransaction("spotify");

			try {
				boolean hasSubGenres = false;

	               Elements subgenres = genreDoc.select(".subgenres > li > .genre-links");

	               for(Element subgenre : subgenres) {

	                  processSubgenre(genreId, subgenre, true);

	                  hasSubGenres = true;

	               }

	               subgenres = genreDoc.select(".subgenres > li > .genre-parent");

	               for(Element subgenre : subgenres) {

	                  processSubgenre(genreId, subgenre, false);

	                  hasSubGenres = true;

	               }

	               if(!hasSubGenres) {

	                  System.out.println("Has no subgenres so looking for styles directly under genre.");

	                  Set<String> styles = new HashSet<String>();

	                  Elements styleElements = genreDoc.select(".styles a");

	                  for(Element styleElement : styleElements) {

	                	  styles.add(AllMusicUtil.getIdFromUrl(styleElement.attr("href")));

	                  }

	                  if(!styles.isEmpty()) {
	                	  AddSubgenreStylesCBH cbh = new AddSubgenreStylesCBH(genreId, styles);
	         	         DBUtils.executePreparedStatement(cbh, false, true, "spotify");
	         	      }

	               }

				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				DBUtils.rollbackTransaction("spotify");
				throw e;
			}

			DBUtils.commitTransaction("spotify");
			System.out.println();
		}
	}
	
	private static void processSubgenre(String genre, Element subgenre, boolean isStyle) throws Exception

	   {

	      String name = subgenre.ownText();

	      Set<String> styles = new HashSet<String>();

	      if(isStyle) styles.add(AllMusicUtil.getIdFromUrl(subgenre.attr("href")));

	      Elements styleElements = subgenre.parent().select("ul.styles li a");

	      for(Element styleElement : styleElements) {

	         styles.add(AllMusicUtil.getIdFromUrl(styleElement.attr("href")));

	      }

	      if(!styles.isEmpty()) {
	    	  AddSubgenreCBH cbh = new AddSubgenreCBH(genre, name, styles);
	         DBUtils.executePreparedStatement(cbh, false, true, "spotify");
	      }
	   }


	private static class AddSubgenreStylesCBH implements PreparedStatementCBH {

		private static final String SQL = "UPDATE styles SET genre = ?  WHERE id = ?";

		Set<String> styleIds;
		String genreId;

		public AddSubgenreStylesCBH(String genreId,Set<String> styleIds) {
			super();
			this.styleIds = styleIds;
			this.genreId = genreId;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (String styleId : styleIds) {
				pstmt.setString(1, genreId);
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

	private static class AddSubgenreCBH implements PreparedStatementCBH {

		private final String SQL = "UPDATE styles SET genre = ? , subgenre = ? WHERE id = ?";
		private final String genre;
		private final String subgenre;
		private final Set<String> ids;

		public AddSubgenreCBH(String genre, String subgenre, Set<String> ids) {
			super();
			this.genre = genre;
			this.subgenre = subgenre;
			this.ids = ids;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for(String id : ids) {
			pstmt.setString(1, genre);
			pstmt.setString(2, subgenre);
			pstmt.setString(3, id);
			pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return SQL;
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

	private static class GetStyleIdsNotInDBCBH implements PreparedStatementCBH {

		private static final String SQL = "SELECT id FROM styles WHERE id IN (";
		private List<String> ids;
		private Set<String> idsNotInDB = new HashSet<String>();

		public GetStyleIdsNotInDBCBH(Collection<String> ids) {
			this.ids = new ArrayList<String>(ids);
		}

		public Set<String> getIDs() {
			return idsNotInDB;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			for (int i = 1; i <= ids.size(); i++) {
				pstmt.setString(i, ids.get(i - 1));
			}
		}

		@Override
		public String getQuery() {
			StringBuilder s = new StringBuilder(SQL);
			for (int i = 0; i < ids.size(); i++) {
				if (i > 0) {
					s.append(',');
				}
				s.append('?');
			}
			s.append(')');
			String query = s.toString();
			System.out.println("Returning query: " + query);
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
			idsNotInDB.addAll(ids);
			while (rs.next()) {
				String id = rs.getString(1);
				idsNotInDB.remove(id);
			}
		}

	}

	private static class AddIdCBH implements PreparedStatementCBH {

		private static final String SQL = "INSERT INTO styles (id,name) VALUES (?,?)";
		private final String id;
		private final String name;

		public AddIdCBH(String id, String name) {
			super();
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
			return SQL;
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
	
	private static class FindStylesWithNoSubgenreCBH implements ExecuteQueryCBH {

		private static final String SQL = "SELECT styles.id FROM styles LEFT JOIN subgenre_styles ON styles.id = subgenre_styles.style_id WHERE subgenre_styles.style_id IS NULL";
		
		private Set<String> styles = new HashSet<String>();
		
		public Set<String> getStyles() {
			return styles;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {
			while(rs.next()) {
				styles.add(rs.getString(1));
			}
		}

		@Override
		public String getQuery() {
			return SQL;
		}
		
	}
	
	private static class FindAlbumForStyleCBH implements PreparedStatementCBH {

		private static final String SQL = "SELECT albums.url FROM albums LEFT JOIN album_styles ON albums.id = album_styles.album_id WHERE album_styles.style_id = ? LIMIT 1";
		
		private final String style;
		private String url;
		
		public FindAlbumForStyleCBH(String style) {
			super();
			this.style = style;
		}

		public String getUrl() {
			return url;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {
			pstmt.setString(1, style);
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
			while(rs.next())
			   url = rs.getString(1);
		}
		
	}
}
