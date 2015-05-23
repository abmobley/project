package org.mobley.album.spotify;

public class SpotifyExternalID
{

   private String upc;
   private String isrc;
   private String ean;

   public String getIsrc()
   {
      return isrc;
   }

   public String getEan()
   {
      return ean;
   }

   public String getUpc()
   {
      return upc;
   }

   @Override
   public String toString()
   {
      return "SpotifyExternalID [upc=" + upc + ", isrc=" + isrc + ", ean=" + ean + "]";
   }
   
   
}
