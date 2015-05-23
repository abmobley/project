package org.mobley.album.spotify;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mobley.album.data.Artist;

import com.google.gson.Gson;

public class SpotifyUtil
{

   private static final String SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search?q=";
   private static final String SPOTIFY_ARTIST_FILTER = "artist:";
   private static final String SPOTIFY_ALBUM_FILTER = "album:";
   private static final String SPOTITY_SPACE = "%20";
   private static final String SPOTIFY_ALBUM_TYPE = "&type=album";
   private static final String SPOTIFY_MARKET_FILTER = "&market=US";
   private static final String SPOTIFY_ALBUM_URI = "https://api.spotify.com/v1/albums/";
   private static final Gson GSON = new Gson();
   
   public static SpotifyAlbumSearchResult searchAlbum(String title, Set<Artist> set) throws Exception
   {
      StringBuilder artistsString = new StringBuilder();
      int i = 0;
      for(Artist artist : set)
      {
         if(i++ > 0)
         {
            artistsString.append(" ");
         }
         artistsString.append(artist.getName());
      }
      String query = SPOTIFY_SEARCH_URL + SPOTIFY_ALBUM_FILTER + 
            convertString(title) + SPOTITY_SPACE + SPOTIFY_ARTIST_FILTER + 
            convertString(artistsString.toString()) +  SPOTIFY_ALBUM_TYPE + SPOTIFY_MARKET_FILTER;
      URL url = new URL(query);
      BufferedReader in = new BufferedReader(
      new InputStreamReader(url.openStream(),"UTF8"));

      SpotifyAlbumSearchResult result = GSON.fromJson(in, SpotifyAlbumSearchResult.class);
      
      try
      {
         in.close();
      }
      catch (Exception e)
      {
      }
      
      return result;
   }
   
   public static SpotifyAlbum getAlbumFromId(String id) throws Exception
   {
      return getAlbum(getSpotifyAlbumURI(id));
   }
   
   public static SpotifyAlbum getAlbum(String href) throws Exception
   {
      URL url = new URL(href);
      BufferedReader in = new BufferedReader(
      new InputStreamReader(url.openStream(),"UTF8"));

      SpotifyAlbum album = GSON.fromJson(in, SpotifyAlbum.class);
      
      try
      {
         in.close();
      }
      catch (Exception e)
      {
      }
      
      return album;
   }
   
   private static String convertString(String s) throws UnsupportedEncodingException
   {
      return URLEncoder.encode(s.trim().toLowerCase(), "UTF-8");
   }

   public static String getSpotifyAlbumURI(String spotifyid) 
   {
      return SPOTIFY_ALBUM_URI + spotifyid;
   }
   
   public static boolean isAvailableInUS(String[] available_markets)
   {
      boolean b = false;
      if (available_markets != null && available_markets.length > 0)
      {
         b = Arrays.asList(available_markets).contains("US");
      }
      return b;
   }
   
   public static List<SpotifyTrack> getTracks(SpotifyAlbum album)
   {

      List<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>(album.getTracks().getTotal());
      getTracks(album.getTracks(), tracks);
      return tracks;
   }

   private static void getTracks(SpotifyTracksPagingObject pagingObject, List<SpotifyTrack> tracks)
   {
      tracks.addAll(Arrays.asList(pagingObject.getItems()));
      if (pagingObject.getNext() != null)
      {
         try
         {
            SpotifyTracksPagingObject next = getSpotifyTracksPagingObject(pagingObject.getNext());
            getTracks(next, tracks);
         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
   
   public static SpotifyTracksPagingObject getSpotifyTracksPagingObject(String href) throws Exception
   {
      URL url = new URL(href);
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF8"));

      SpotifyTracksPagingObject pagingObject = GSON.fromJson(in, SpotifyTracksPagingObject.class);

      try
      {
         in.close();
      }
      catch (Exception e)
      {
      }

      return pagingObject;
   }
   
   public static void main(String[] args) throws Exception
   {
      SpotifyAlbum album = SpotifyUtil.getAlbumFromId("1gGjSTfser1MPfowGiuy9l");
      List<SpotifyTrack> tracks = SpotifyUtil.getTracks(album);
      for(SpotifyTrack track : tracks) System.out.println(track);
      System.out.println(tracks.size());
   }
}
