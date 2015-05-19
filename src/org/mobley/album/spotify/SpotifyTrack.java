package org.mobley.album.spotify;

import java.util.Arrays;

public class SpotifyTrack
{

   private SpotifyAlbum album;
   private SpotifyArtist[] artists;
   private String[] available_markets;
   private int disc_number;
   private int duration_ms;
   private boolean explicit;
   private SpotifyExternalID external_ids;
   private SpotifyExternalURL external_urls;
   private String href;
   private String id;
   private String name;
   private int popularity;
   private String preview_url;
   private int track_number;
   private String type;
   private String uri;
   public SpotifyAlbum getAlbum()
   {
      return album;
   }
   public SpotifyArtist[] getArtists()
   {
      return artists;
   }
   public String[] getAvailable_markets()
   {
      return available_markets;
   }
   public int getDisc_number()
   {
      return disc_number;
   }
   public int getDuration_ms()
   {
      return duration_ms;
   }
   public boolean isExplicit()
   {
      return explicit;
   }
   public SpotifyExternalID getExternal_ids()
   {
      return external_ids;
   }
   public SpotifyExternalURL getExternal_urls()
   {
      return external_urls;
   }
   public String getHref()
   {
      return href;
   }
   public String getId()
   {
      return id;
   }
   public String getName()
   {
      return name;
   }
   public int getPopularity()
   {
      return popularity;
   }
   public String getPreview_url()
   {
      return preview_url;
   }
   public int getTrack_number()
   {
      return track_number;
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
      return "SpotifyTrack [album=" + album + ", artists=" + Arrays.toString(artists) + ", available_markets=" + Arrays.toString(available_markets) + ", disc_number="
            + disc_number + ", duration_ms=" + duration_ms + ", explicit=" + explicit + ", external_ids=" + external_ids + ", external_urls=" + external_urls + ", href=" + href
            + ", id=" + id + ", name=" + name + ", popularity=" + popularity + ", preview_url=" + preview_url + ", track_number=" + track_number + ", type=" + type + ", uri="
            + uri + "]";
   }
   
   
}
