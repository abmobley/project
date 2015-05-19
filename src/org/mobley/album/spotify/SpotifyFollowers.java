package org.mobley.album.spotify;

public class SpotifyFollowers
{

   private String href;
   private int total;
   public String getHref()
   {
      return href;
   }
   public int getTotal()
   {
      return total;
   }
   @Override
   public String toString()
   {
      return "SpotifyFollowers [href=" + href + ", total=" + total + "]";
   }
   
   
}
