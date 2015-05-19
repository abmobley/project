package org.mobley.album.allmusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicUtil
{

   public static Document getDocument(String url) throws IOException
   {
	   Document doc = null;
	   while(doc == null) {
      try {
		doc = Jsoup.connect(url).get();
	} catch (IOException e) {
		
	}
	   }
      return doc;
   }
   
   public static String getAlbumTitle(Document albumDoc)
   {
      Elements elements = albumDoc.select("h2.album-title");
      String albumTitle = null;
      if(elements != null && !elements.isEmpty())
      {
         albumTitle = elements.first().ownText().trim();
      }
      return albumTitle;
   }
   
   public static List<AllMusicArtist> getAlbumArtists(Document albumDoc)
   {
      List<AllMusicArtist> artists = new ArrayList<AllMusicArtist>();
      Elements elements = albumDoc.select("h3.album-artist");
      if(elements != null && !elements.isEmpty())
      {
         Elements albumArtistElements = elements.first().select("a");
         for(Element element : albumArtistElements)
         {
            String url = element.attr("href").trim();
            String name = element.ownText().trim();
            artists.add(new AllMusicArtist(name,url));
         }
      }
      return artists;
   }
   
   public static String getAlbumImageSource(Document albumDoc)
   {
      Elements elements = albumDoc.select("div.album-cover");
      String url = null;
      if(elements != null && !elements.isEmpty())
      {
         Elements imgElements = elements.first().select("img");

         if(imgElements != null && !imgElements.isEmpty())
         {
            url = imgElements.first().attr("src").trim();
         }
      }
      return url;
   }
   
   public static List<AllMusicTrack> getDiscTracks(Element discElement)
   {
      List<AllMusicTrack> tracks = new ArrayList<AllMusicTrack>();
      Elements elements = discElement.select("[itemprop=track]");
      if(elements != null && !elements.isEmpty())
      {
         for(Element element : elements)
         {
            String pick = element.className();
            String title = null;
            String id = null;
            Element titleElement = element.select("div.title a").first();
            if(titleElement != null)
            {
               title = titleElement.ownText().trim();
               id = getIdFromUrl(titleElement.attr("href"));
            }
            String spotifyId = null;
            Element spotifyElement = element.select(".spotify").first();
            if(spotifyElement != null && !spotifyElement.attr("class").startsWith("empty"))
            {
               String href = spotifyElement.attr("href");
               if(href != null)
               {
                  String[] tokens = href.split("/");
                  spotifyId = tokens[tokens.length-1];
               }
            }
            AllMusicTrack track = new AllMusicTrack(id, title,"track pick".equals(pick));
            track.setSpotifyId(spotifyId);
            tracks.add(track);
            
         }
      }
      return tracks;
   }
   
   public static short getAlbumRating(Document albumDoc)
   {

      Elements elements = albumDoc.select("[itemprop=ratingValue]");
      short rating = 0;
      if(elements != null && !elements.isEmpty())
      {
    	  String s = elements.first().ownText().trim();
    	  if(s.length() > 0) {
             try {
				rating = Short.parseShort(elements.first().ownText());
			} catch (NumberFormatException e) {
				rating = 0;
			}
    	  }
      }
      return rating;
   }
   
   public static List<AllMusicDisc> getAlbumDiscs(Document albumDoc)
   {
      List<AllMusicDisc> discs = new ArrayList<AllMusicDisc>();
      Elements elements = albumDoc.select(".disc");
      for(Element element : elements)
      {
         List<AllMusicTrack> tracks =  getDiscTracks(element);
         discs.add(new AllMusicDisc(tracks));
      }
      return discs;
   }
   
   public static List<AllMusicDisc> getReleaseDiscs(Document releaseDoc)
   {
      List<AllMusicDisc> discs = new ArrayList<AllMusicDisc>();
      Elements elements = releaseDoc.select(".disc");
      for(Element element : elements)
      {
         List<AllMusicTrack> tracks =  getDiscTracks(element);
         discs.add(new AllMusicDisc(tracks));
      }
      return discs;
   }
   public static String getAlbumSpotifyId(Document albumDoc)
   {

      Elements elements = albumDoc.select("[data-target=spotify]");
      String spotifyId = null;
      if(elements != null && !elements.isEmpty())
      {
         String href = elements.first().attr("href");
         if(href != null)
         {
            String[] tokens = href.split("/");
            spotifyId = tokens[tokens.length-1];
         }
      }
      return spotifyId;
   }
   
   public static AllMusicAlbum getAlbum(Document albumDoc) throws IOException
   {
	  if(isRelease(albumDoc.location())) {
		  albumDoc = getAlbumDocFromReleaseDoc(albumDoc);
	  }
      String title = getAlbumTitle(albumDoc);
      String url = getAlbumURL(albumDoc);
      AllMusicAlbum album = new AllMusicAlbum(title, url);
      album.setRating(getAlbumRating(albumDoc));
      album.setSpotifyId(getAlbumSpotifyId(albumDoc));
      album.setArtists(getAlbumArtists(albumDoc));
      album.setDiscs(getAlbumDiscs(albumDoc));
      album.setImageSrc(getAlbumImageSource(albumDoc));
      album.setGenres(getAlbumGenres(albumDoc));
      album.setStyles(getAlbumStyles(albumDoc));
      album.setMoods(getAlbumMoods(albumDoc));
      album.setThemes(getAlbumThemes(albumDoc));
      return album;
   }
   
   private static Document getAlbumDocFromReleaseDoc(Document releaseDoc) throws IOException {
	   Document albumDoc = null;
	   Element element = releaseDoc.select("section.main-album a.album-title").first();
	   if(element != null) {
		   albumDoc = getDocument(element.attr("href"));
	   }
	   return albumDoc;
   }
   
   private static boolean isRelease(String url) {
	   String id = getIdFromUrl(url);
	   return id.startsWith("MR");
   }
   
   private static String getAlbumURL(Document albumDoc)
   {
      Element element = albumDoc.select("[itemprop=url]").first();
      String url = null;
      if(element != null)
      {
         url = element.attr("content");
      }
      return url;
   }

   public static String[] getAlbumGenres(Document albumDoc)
   {
      String[] genres = null;
      Elements elements = albumDoc.select(".genre");
      if(elements != null && !elements.isEmpty())
      {
         Elements genreElements = elements.first().select("a");
         genres = new String[genreElements.size()];
         int i = 0;
         for(Element genreElement : genreElements)
         {
            String href = genreElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               genres[i++] = href.substring(index+1).toUpperCase();
            }
         }
      }
      return genres;
   }
   
   public static Map<String,String> getNamesFromAlbumDoc(Document albumDoc, String className) {
	   Map<String,String> urls = new HashMap<String,String>();
	   String select = "." + className + " a";
	   Elements elements = albumDoc.select(select);
	   for(Element element : elements) {
		   String href = element.attr("href");
		   String id = getIdFromUrl(href);
		   String name = element.ownText().trim();
		   urls.put(id, name);
	   }
	   return urls;
   }
   
   public static String[] getAlbumStyles(Document albumDoc)
   {
      String[] styles = null;
      Elements elements = albumDoc.select(".styles");
      if(elements != null && !elements.isEmpty())
      {
         Elements styleElements = elements.first().select("a");
         styles = new String[styleElements.size()];
         int i = 0;
         for(Element styleElement : styleElements)
         {
            String href = styleElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               styles[i++] = href.substring(index+1).toUpperCase();
            }
         }
      }
      return styles;
   }
   
   public static String getAlbumStyleURL(String albumUrl, String styleId) throws Exception
   {

	      String style = null;
	      Document albumDoc = getDocument(albumUrl);
	      Elements elements = albumDoc.select(".styles");
	      if(elements != null && !elements.isEmpty())
	      {
	         Elements styleElements = elements.first().select("a");
	         for(Element styleElement : styleElements)
	         {
	            String href = styleElement.attr("href");
	            int index = href.lastIndexOf('-');
	            if(index > -1)
	            {
	               if(href.substring(index+1).toUpperCase().equals(styleId)){
	            	   style = href;
	            	   break;
	               }
	            }
	         }
	      }
	      return style;
   }
   
   public static String[] getAlbumMoods(Document albumDoc)
   {
      String[] moods = null;
      Elements elements = albumDoc.select(".moods");
      if(elements != null && !elements.isEmpty())
      {
         Elements moodElements = elements.first().select("a");
         moods = new String[moodElements.size()];
         int i = 0;
         for(Element moodElement : moodElements)
         {
            String href = moodElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               moods[i++] = href.substring(index+1).toUpperCase();
            }
         }
      }
      return moods;
   }
   
   public static String[] getAlbumThemes(Document albumDoc)
   {
      String[] themes = null;
      Elements elements = albumDoc.select(".themes");
      if(elements != null && !elements.isEmpty())
      {
         Elements themeElements = elements.first().select("a");
         themes = new String[themeElements.size()];
         int i = 0;
         for(Element themeElement : themeElements)
         {
            String href = themeElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               themes[i++] = href.substring(index+1).toUpperCase();
            }
         }
      }
      return themes;
   }
   
   public static NavigableSet<AllMusicSimilarAlbum> getSimilarAlbums(Document similarDoc)
   {
	  NavigableSet<AllMusicSimilarAlbum> similarAlbums = new TreeSet<AllMusicSimilarAlbum>();
      Elements rows = similarDoc.select("tbody tr");
      if(rows != null)
      {
         for(Element row : rows)
         {
            String href = null;
            short rating = 0;
            Element album = row.select(".album a").first();
            if(album != null)
            {
               href = album.attr("href");
            }
            Element ratingElement = row.select(".rating .allmusic-rating").first();
            if(ratingElement != null)
            {
               int index = ratingElement.attr("class").lastIndexOf('-');
               if(index > -1)
               {
                  rating = Short.parseShort(ratingElement.attr("class").substring(index+1));
               }
            }
            if(href != null)
            {
               similarAlbums.add(new AllMusicSimilarAlbum(href,rating));
            }
         }
      }
      return similarAlbums;
   }
   
   public static String getIdFromUrl(String url)
   {
	  String[] pathElements = url.split("/");
	  String id = pathElements[pathElements.length-1];
      int index = id.lastIndexOf('-');
      
      if(index > -1)
      {
         id = id.substring(index+1);
      }
      return id.toUpperCase();
   }
   
   public static void main(String[] args) throws Exception
   {
     String s = "http://www.allmusic.com/album/30-mw0000593851";
     System.out.println(AllMusicUtil.getIdFromUrl(s));
   }

	public static Set<String> getEditorChoiceURLs(Document doc) {
		Set<String> urls = new HashSet<String>();
		Elements elements = doc.select("div.row div.title a");
		for(Element element : elements) {
			urls.add(element.attr("href"));
		}
		return urls;
	}

	public static String getGenreName(Document genreDoc) {
		Element element = genreDoc.select(".genre-name").first();
		String name = null;
		if(element != null) {
			name = element.ownText().trim();
		}
		return name;
	}

	public static String getThemeName(Document doc) {
		Element element = doc.select(".theme-name").first();
		String name = null;
		if(element != null) {
			name = element.ownText().trim();
		}
		return name;
	}

	public static String getMoodName(Document doc) {
		Element element = doc.select(".mood-name").first();
		String name = null;
		if(element != null) {
			name = element.ownText().trim();
		}
		return name;
	}

	public static String getArtistName(Document artistDoc) {
		Element element = artistDoc.select(".artist-name").first();
		String name = null;
		if(element != null) {
			name = element.ownText().trim();
		}
		return name;
	}
}
