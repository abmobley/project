package org.mobley.album.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

import com.google.gson.Gson;


public class SpotifyAlbum
{
   private String album_type;
   private SpotifyArtist[] artists;
   private String[] available_markets;
   private SpotifyCopyright[] copyrights;
   private SpotifyExternalID external_ids;
   private SpotifyExternalURL external_urls;
   private String[] genres;
   private String href;
   private String id;
   private SpotifyImage[] images;
   private String name;
   private int popularity;
   private String release_date;
   private String release_date_precision;
   private SpotifyTracksPagingObject tracks;
   private String type;
   private String uri;
   public String getAlbum_type()
   {
      return album_type;
   }
   public SpotifyArtist[] getArtists()
   {
      return artists;
   }
   public String[] getAvailable_markets()
   {
      return available_markets;
   }
   public SpotifyCopyright[] getCopyrights()
   {
      return copyrights;
   }
   public SpotifyExternalID getExternal_ids()
   {
      return external_ids;
   }
   public SpotifyExternalURL getExternal_urls()
   {
      return external_urls;
   }
   public String[] getGenres()
   {
      return genres;
   }
   public String getHref()
   {
      return href;
   }
   public String getId()
   {
      return id;
   }
   public SpotifyImage[] getImages()
   {
      return images;
   }
   public String getName()
   {
      return name;
   }
   public int getPopularity()
   {
      return popularity;
   }
   public String getRelease_date()
   {
      return release_date;
   }
   public String getRelease_date_precision()
   {
      return release_date_precision;
   }
   public SpotifyTracksPagingObject getTracks()
   {
      return tracks;
   }
   public String getType()
   {
      return type;
   }
   public String getUri()
   {
      return uri;
   }
   
   @Override
   public String toString()
   {
      return "SpotifyAlbum [album_type=" + album_type + ", artists=" + Arrays.toString(artists) + ", available_markets=" + Arrays.toString(available_markets) + ", copyrights="
            + Arrays.toString(copyrights) + ", external_ids=" + external_ids + ", external_urls=" + external_urls + ", genres=" + Arrays.toString(genres) + ", href=" + href
            + ", id=" + id + ", images=" + Arrays.toString(images) + ", name=" + name + ", popularity=" + popularity + ", release_date=" + release_date
            + ", release_date_precision=" + release_date_precision + ", tracks=" + tracks + ", type=" + type + ", uri=" + uri + "]";
   }
   
   public static void main(String[] args) throws IOException
   {
      URL oracle = new URL("https://api.spotify.com/v1/albums/5STExWe03BwKEXMWCLWjug");
      BufferedReader in = new BufferedReader(
      new InputStreamReader(oracle.openStream(),"UTF8"));

      
      Gson gson = new Gson();
      SpotifyAlbum result = gson.fromJson(in, SpotifyAlbum.class);
      System.out.println(result.tracks.getItems()[5].getName());
      System.out.println(gson.toJson(result));
   }
}
