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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mobley.album.DBUtils.PreparedStatementCBH;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicArtistDiscographyProcessor;
import org.mobley.album.allmusic.AllMusicSimilarAlbum;
import org.mobley.album.allmusic.AllMusicUtil;

public class SimilarProcessor {

	private static final String SIMILAR_URL_START = "http://www.allmusic.com/album/ajax/";
	private static final String SIMILAR_URL_FINISH = "/similar/listview";
	private static final String ALBUM_URL_START = "http://www.allmusic.com/album/";
	
	public static void main(String args[]) throws Exception {

		Set<String> albums = getOwnedFromFile();
		//Set<String> albums = AllMusicArtistDiscographyProcessor.get("http://www.allmusic.com/artist/dave-holland-mn0000585092/discography/all", 10);

		Set<String> urls = new HashSet<String>();
		Set<String> classicalUrls = new HashSet<String>();
		Set<String> notInSpotifyURLs = MissingProcessor.getMissingURLs("spotify");
		List<String> skippedUrls = new ArrayList<String>();
		for(String album : albums) {
		   try {
			processSimilarAlbums(album,5,urls, classicalUrls, notInSpotifyURLs, skippedUrls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		System.out.println();
		System.out.println("Not classical");
		for(String url : urls) {
			System.out.println(url);
		}
		System.out.println();
		System.out.println("Classical");
		for(String url : classicalUrls) {
			System.out.println(url);
		}
		System.out.println();
		System.out.println("Skipped");
		for(String url : skippedUrls) {
			System.out.println(url);
		}
	}

	private static Set<String> getOwnedFromFile() {
		Set<String> albums = new HashSet<String>();
		File f = new File("owned.txt");
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				albums.add(line);
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
		return albums;
	}
	
	public static void processSimilarAlbums(String url, int number, Set<String> urls, Set<String> classicalUrls, Set<String> notInSpotify, List<String> skipped) throws Exception {
		Document albumDoc = AllMusicUtil.getDocument(url);
		AllMusicAlbum album = AllMusicUtil.getAlbum(albumDoc);
		int index = ALBUM_URL_START.length();
	    String similarUrl = SIMILAR_URL_START + albumDoc.location().substring(index) + SIMILAR_URL_FINISH;
	    boolean processClassical = isClassical(album.getGenres());
	    boolean processNonClassical = isNotClassical(album.getGenres());

		Set<String> id = new HashSet<String>();
		id.add(album.getId());
		if(processNonClassical) {
		   GetAlbumIdsNotInDBCBH cbh = new GetAlbumIdsNotInDBCBH(id,false);
		   int i = 0;
			String inDBString = " is already in DB";
			try {
				DBUtils.executePreparedStatement(cbh, false, false,"spotify");
				if (!cbh.getIDs().isEmpty()) {
					i = 1;
					inDBString = " is not in DB yet";
					boolean added = AlbumProcessor.addAlbumToDB(albumDoc, album);
					if(added) {
						System.out.println("Added to db.");
					    urls.add(url);
					} else {
						System.out.println("Added to missing albums.");
						MissingProcessor.addMissingAlbum(url);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Processing: " + url + inDBString + " as not classical");
			try {
				
				List<String> skippedUrls = new ArrayList<String>();
				Document similarDoc = AllMusicUtil.getDocument(similarUrl);
				processNew(similarDoc,i,number,skippedUrls,urls,notInSpotify,false);
				System.out.println("Skipped:");
				for(String skippedUrl : skippedUrls) {
					System.out.println("Incrementing sikpped refs in missing albums: " + skippedUrl);
					MissingProcessor.addMissingAlbum(skippedUrl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(processClassical) {
			   GetAlbumIdsNotInDBCBH cbh = new GetAlbumIdsNotInDBCBH(id,true);
			   int i = 0;
				String inDBString = " is already in DB";
				try {
					DBUtils.executePreparedStatement(cbh, false, false,"spotify");
					if (!cbh.getIDs().isEmpty()) {
						i = 1;
						inDBString = " is not in DB yet";
						classicalUrls.add(url);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Processing: " + url + inDBString + " as classical");
				try {
					
					List<String> skippedUrls = new ArrayList<String>();
					Document similarDoc = AllMusicUtil.getDocument(similarUrl);
					processNew(similarDoc,i,number,skippedUrls,classicalUrls,notInSpotify,true);
					System.out.println("Skipped:");
					for(String skippedUrl : skippedUrls) {
						System.out.println(skippedUrl);
					}
					skipped.addAll(skippedUrls);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}
	
	private static boolean isClassical(String[] genres) {
		boolean b = false;
		for(String genre : genres) {
			if(genre.equals("MA0000002521")) {
				b = true;
				break;
			}
		}
		return b;
	}
	
	private static boolean isNotClassical(String[] genres) {
		boolean b = false;
		for(String genre : genres) {
			if(!genre.equals("MA0000002521")) {
				b = true;
				break;
			}
		}
		return b;
	}
	
	private static void processNew(Document doc, int index, int number, List<String> skippedUrls, Set<String> urls, Set<String> notInSpotify, boolean isClassical) throws Exception {
		if (index >= number)
			return;
		NavigableSet<AllMusicSimilarAlbum> sorted = AllMusicUtil.getSimilarAlbums(doc);
		
		Set<String> id = new HashSet<String>();
		for(AllMusicSimilarAlbum similarAlbum : sorted) {
			id.add(similarAlbum.getId());
		}
		if(id.isEmpty()) {

	    	System.out.println("No similar found: " + doc.location());
	    	return;
		}
		Iterator<AllMusicSimilarAlbum> iterator = sorted.descendingIterator();
		GetAlbumIdsNotInDBCBH cbh = new GetAlbumIdsNotInDBCBH(id,isClassical);
		DBUtils.executePreparedStatement(cbh, false, false,"spotify");
		Set<String> idsNotInDB = cbh.getIDs();
		String next = null;
		while(iterator.hasNext()) {
			AllMusicSimilarAlbum nextAlbum = iterator.next();
			String url = nextAlbum.getHref();
			if (!notInSpotify.contains(url)) {
				if (!urls.contains(url)) {
					boolean add = false;
					if(idsNotInDB.contains(nextAlbum.getId())) {
					Document albumDoc = AllMusicUtil.getDocument(url);
					String[] genres = AllMusicUtil.getAlbumGenres(albumDoc);
					if(genres != null && genres.length > 0) {
					if(isClassical && isClassical(genres)) {
						add = true;
						System.out.println(url);
						urls.add(url);
					} else if(isNotClassical(genres)) {
                        add = true;
						boolean added = AlbumProcessor.addAlbumToDB(albumDoc, AllMusicUtil.getAlbum(albumDoc));
						if(added) {
							System.out.println("Added to db.");
						    urls.add(url);
						} else {
							System.out.println("Added to missing albums.");
							MissingProcessor.addMissingAlbum(url);
						}
					}
					}
					if(add) {
						next = url;
						break;
					}
					}
				}
			} else {
				skippedUrls.add(url);
			}
		}

		if (next != null) {
			index++;
			int i = ALBUM_URL_START.length();
		    String similarUrl = SIMILAR_URL_START + next.substring(i) + SIMILAR_URL_FINISH;
		    int tries = 0;
		    boolean isEmpty = true;
		    Document similarDoc = null;
		    while(tries < 5 && isEmpty) {
		    	try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
		    	similarDoc = AllMusicUtil.getDocument(similarUrl);
		    	tries++;
		    	Elements rows = similarDoc.select("tbody tr");
		    	isEmpty = rows == null || rows.isEmpty(); 
		    }
		    if(isEmpty) {
		    	System.out.println("No similar found after 5 attempts: " + next);
		    	return;
		    }
			processNew(similarDoc, index, number, skippedUrls, urls, notInSpotify, isClassical);
		} else {
			System.out.println("No similar found.");
		}
	}

	private static class GetAlbumIdsNotInDBCBH implements PreparedStatementCBH {

		private static final String SQL = "SELECT id FROM albums WHERE id IN (";
		private static final String CLASSICAL_SQL = "SELECT id FROM classical_albums WHERE id IN (";
		private List<String> ids;
		private Set<String> idsNotInDB = new HashSet<String>();
		private final boolean isClassical;

		public GetAlbumIdsNotInDBCBH(Collection<String> ids,boolean isClassical) {
			this.ids = new ArrayList<String>(ids);
			this.isClassical = isClassical;
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
			String start = isClassical ? CLASSICAL_SQL : SQL;
			StringBuilder s = new StringBuilder(start);
			for (int i = 0; i < ids.size(); i++) {
				if (i > 0) {
					s.append(',');
				}
				s.append('?');
			}
			s.append(')');
			String query = s.toString();
			System.out.println("Returning query: " +  query);
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

}
