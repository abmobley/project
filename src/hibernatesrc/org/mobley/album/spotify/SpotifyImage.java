package org.mobley.album.spotify;

public class SpotifyImage
{

   private int height;
   private String url;
   private int width;
   
   public int getHeight()
   {
      return height;
   }
   
   public String getUrl()
   {
      return url;
   }
   
   public int getWidth()
   {
      return width;
   }

   @Override
   public String toString()
   {
      return "SpotifyImage [height=" + height + ", url=" + url + ", width=" + width + "]";
   }
   
   
}
