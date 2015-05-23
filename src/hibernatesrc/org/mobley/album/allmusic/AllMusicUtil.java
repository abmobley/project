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

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicUtil
{

   public static Document getDocument(String url) throws HttpStatusException
   {
      Document doc = null;
      while(doc == null)
      {
         try
         {
            doc = Jsoup.connect(url).get();
         }
         catch(HttpStatusException hse)
         {
           throw hse;
         }
         catch (IOException e)
         {
            System.out.println("Could not get document for " + url + ". Retrying.");
            e.printStackTrace();
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException e1)
            {
               
            }
         }
      }
      return doc;
   }
   
   public static Set<String> getEditorChoiceURLs(Document doc) {
      Set<String> urls = new HashSet<String>();
      Elements elements = doc.select("div.row div.title a");
      for(Element element : elements) {
         urls.add(element.attr("href"));
      }
      return urls;
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

   public static String getReleaseTitle(Document releaseDoc)
   {
      Elements elements = releaseDoc.select("h2.release-title");
      String releaseTitle = null;
      if(elements != null && !elements.isEmpty())
      {
         releaseTitle = elements.first().ownText().trim();
      }
      return releaseTitle;
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
   
   public static List<AllMusicTrack> getDiscTracks(Element discElement, Map<String, Integer> movementMap)
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
            Elements titleElements = element.select("div.title");
            if(titleElements != null && !titleElements.isEmpty())
            {
               Element titleElement = titleElements.select("a").first();
               if(titleElement != null)
               {
                  title = titleElement.ownText().trim();
                  id = AllMusicUtil.getIdFromUrl(titleElement.attr("href"));
               }
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
            
            if (id != null && title != null)
            {
               AllMusicTrack track = new AllMusicTrack(id, title, "track pick".equals(pick));
               track.setSpotifyId(spotifyId);
               Integer mvt = movementMap.get(track.getId());
               if (mvt == null)
               {
                  mvt = new Integer(1);
               }
               else
               {
                  mvt += 1;
               }
               movementMap.put(track.getId(), mvt);
               track.setMovement(mvt);
               tracks.add(track);
            }
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
         rating = Short.parseShort(elements.first().ownText());
      }
      return rating;
   }
   
   public static List<AllMusicDisc> getAlbumDiscs(Document albumDoc)
   {
      List<AllMusicDisc> discs = new ArrayList<AllMusicDisc>();
      Elements elements = albumDoc.select(".disc");
      Map<String,Integer> movementMap = new HashMap<String,Integer>();
      for(Element element : elements)
      {
         List<AllMusicTrack> tracks =  getDiscTracks(element,movementMap);
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
      String title = getAlbumTitle(albumDoc);
      String url = getAlbumURL(albumDoc);
      AllMusicAlbum album = new AllMusicAlbum(title, url);
      album.setRating(getAlbumRating(albumDoc));
      album.setSpotifyId(getAlbumSpotifyId(albumDoc));
      album.setArtists(getAlbumArtists(albumDoc));
      album.setImageSrc(getAlbumImageSource(albumDoc));
      album.setGenres(getAlbumGenres(albumDoc));
      album.setStyles(getAlbumStyles(albumDoc));
      album.setMoods(getAlbumMoods(albumDoc));
      album.setThemes(getAlbumThemes(albumDoc));
      List<AllMusicDisc> discs = getAlbumDiscs(albumDoc);
      album.setReleases(getReleases(album, discs,url));
      return album;
   }
   
   private static List<AllMusicRelease> getReleases(AllMusicAlbum album, List<AllMusicDisc> discs, String url)
   {
      List<AllMusicRelease> releases = new ArrayList<AllMusicRelease>();
      try
      {
         Document releasesDoc = getDocument(url+"/releases");
         Elements rows = releasesDoc.select(".releases tbody tr");
         if(rows != null)
         {
            for(Element row : rows)
            {
               Element title = row.select("div.title a").first();
               String href = title.attr("href");
               System.out.println("Getting release from " + href);
               AllMusicRelease release = getRelease(href);
               if(release != null)
               {
                  if(release.getDiscs().equals(discs))
                  {
                     release.setMain(true);
                  }
                  releases.add(release);
               }
            }
         }
      }
      catch (HttpStatusException e)
      {
         e.printStackTrace();
      }
      return releases;
   }

   private static AllMusicRelease getRelease(String url)
   {
      String id = getIdFromUrl(url);
      AllMusicRelease release = null;
      try
      {
         Document releaseDoc = getDocument(url);
         release = new AllMusicRelease();
         release.setId(id);
         release.setTitle(getReleaseTitle(releaseDoc));
         release.setDate(getReleaseDate(releaseDoc));
         release.setLabel(getReleaseLabel(releaseDoc));
         release.setFormat(getReleaseFormat(releaseDoc));
         release.setImageSrc(getReleaseImageSource(releaseDoc));
         release.setDiscs(getAlbumDiscs(releaseDoc));
      }
      catch (HttpStatusException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return release;
   }

   public static String getReleaseImageSource(Document releaseDoc)
   {
      Element element = releaseDoc.select("div.release-cover img").first();
      String url = null;
      if(element != null)
      {
            url = element.attr("src").trim();
      }
      return url;
   }
   
   private static String getReleaseFormat(Document releaseDoc)
   {
      Element element = releaseDoc.select(".format span").first();
      String releaseFormat = null;
      if(element != null)
      {
         releaseFormat = element.ownText().trim();
      }
      return releaseFormat;
   }

   private static String getReleaseLabel(Document releaseDoc)
   {
      Element element = releaseDoc.select(".label div").first();
      String releaseLabel = null;
      if(element != null)
      {
         releaseLabel = element.ownText().trim();
      }
      return releaseLabel;
   }

   private static String getReleaseDate(Document releaseDoc)
   {
      Element element = releaseDoc.select(".release-date span").first();
      String releaseDate = null;
      if(element != null)
      {
         releaseDate = element.ownText().trim();
      }
      return releaseDate;
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

   public static Map<String, String> getAlbumGenres(Document albumDoc)
   {
      Map<String, String> genres = new HashMap<String,String>();
      Elements elements = albumDoc.select(".genre");
      if(elements != null && !elements.isEmpty())
      {
         Elements genreElements = elements.first().select("a");
         int i = 0;
         for(Element genreElement : genreElements)
         {
            String href = genreElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               genres.put(href.substring(index+1).toUpperCase(),genreElement.ownText());
            }
         }
      }
      return genres;
   }
   
   public static Map<String, String> getAlbumStyles(Document albumDoc)
   {
      Map<String, String> styles = new HashMap<String,String>();
      Elements elements = albumDoc.select(".styles");
      if(elements != null && !elements.isEmpty())
      {
         Elements styleElements = elements.first().select("a");
         int i = 0;
         for(Element styleElement : styleElements)
         {
            String href = styleElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               styles.put(href.substring(index+1).toUpperCase(),styleElement.ownText());
            }
         }
      }
      return styles;
   }
   
   public static Map<String, String> getAlbumMoods(Document albumDoc)
   {
      Map<String, String> moods = new HashMap<String,String>();
      Elements elements = albumDoc.select(".moods");
      if(elements != null && !elements.isEmpty())
      {
         Elements moodElements = elements.first().select("a");
         int i = 0;
         for(Element moodElement : moodElements)
         {
            String href = moodElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               moods.put(href.substring(index+1).toUpperCase(),moodElement.ownText());
            }
         }
      }
      return moods;
   }
   
   public static Map<String, String> getAlbumThemes(Document albumDoc)
   {
      Map<String, String> themes = new HashMap<String,String>();
      Elements elements = albumDoc.select(".themes");
      if(elements != null && !elements.isEmpty())
      {
         Elements themeElements = elements.first().select("a");
         int i = 0;
         for(Element themeElement : themeElements)
         {
            String href = themeElement.attr("href");
            int index = href.lastIndexOf('-');
            if(index > -1)
            {
               themes.put(href.substring(index+1).toUpperCase(),themeElement.ownText());
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
      int index = url.lastIndexOf('-');
      String id = null;
      if(index > -1)
      {
         id = url.substring(index+1).toUpperCase();
      }
      return id;
   }
   
   
   
   private static class AllMusicDiscographyEntry implements Comparable<AllMusicDiscographyEntry>
   {
      private String title;
      private String href;
      private int rating;
      
      public AllMusicDiscographyEntry(String title, String href, int rating)
      {
         super();
         this.title = title;
         this.href = href;
         this.rating = rating;
      }

      @Override
      public int compareTo(AllMusicDiscographyEntry o)
      {
         int c = 0;
         if(this.rating == o.rating)
         {
            c = this.title.compareTo(o.title);
         }
         else
         {
            //higher rating is less than so that highest is at beginning
            c = o.rating - this.rating;
         }
         return c;
      }

      @Override
      public String toString()
      {
         return "AllMusicDiscographyEntry [title=" + title + ", href=" + href + ", rating=" + rating + "]";
      }

   }
   
   public static void main(String[] args) throws Exception
   {
      String url = "http://www.allmusic.com/album/f%C3%A9licien-david-le-d%C3%A9sert-mw0002815295";
      Document doc = getDocument(url);
      AllMusicAlbum album =  getAlbum(doc);
      
      for(AllMusicRelease release : album.getReleases())
      {
         System.out.println(release);
      }
   }
}
