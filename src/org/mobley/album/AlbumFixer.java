package org.mobley.album;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicUtil;

public class AlbumFixer {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String url = "http://www.allmusic.com/album/stankonia-mw0000252371";
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

				DBUtils.startTransaction("spotify");
				try {
					AlbumProcessor.addAlbumGenresStylesMoodsThemes(album);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DBUtils.rollbackTransaction("spotify");
					throw e;
				}
				DBUtils.commitTransaction("spotify");
			}
	}
		}
	

}
