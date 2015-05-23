package org.mobley.album.spotify;

import java.util.Arrays;


public class SpotifyArtist
{

   private SpotifyExternalURL external_urls;
   private SpotifyFollowers followers;
   private String[] genres;
   private String href;
   private String id;
   private SpotifyImage[] images;
   private String name;
   private int popularity;
   private String type;
   private String uri;
   public SpotifyExternalURL getExternal_urls()
   {
      return external_urls;
   }
   public SpotifyFollowers getFollowers()
   {
      return followers;
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
      return "SpotifyArtist [external_urls=" + external_urls + ", followers=" + followers + ", genres=" + Arrays.toString(genres) + ", href=" + href + ", id=" + id + ", images="
            + Arrays.toString(images) + ", name=" + name + ", popularity=" + popularity + ", type=" + type + ", uri=" + uri + "]";
   }
   
   
}
