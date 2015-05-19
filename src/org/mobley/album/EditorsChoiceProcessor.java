package org.mobley.album;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.mobley.album.allmusic.AllMusicUtil;

public class EditorsChoiceProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Document doc = null;
		
		try {
			doc = AllMusicUtil.getDocument("http://www.allmusic.com/newreleases/editorschoice/june-2011");
			//doc = AllMusicUtil.getDocument("http://www.allmusic.com/newreleases/editorschoice/april-2015");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(doc != null) {
			Set<String> editorsChoiceUrls = AllMusicUtil.getEditorChoiceURLs(doc);
			Set<String> urls = new HashSet<String>();
			Set<String> classicalUrls = new HashSet<String>();
			Set<String> notInSpotifyURLs = MissingProcessor.getMissingURLs("spotify");
			List<String> skippedUrls = new ArrayList<String>();
			for(String url : editorsChoiceUrls) {
				try {
					SimilarProcessor.processSimilarAlbums(url, 5, urls, classicalUrls, notInSpotifyURLs, skippedUrls);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println(doc.location());
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
	}

}
