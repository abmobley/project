package org.mobley.album.spotify;


public abstract class SpotifyPagingObject
{

   private String href;
   private int limit;
   private String next;
   private int offset;
   private String previous;
   private int total;
   
   public String getHref()
   {
      return href;
   }
   public int getLimit()
   {
      return limit;
   }
   public String getNext()
   {
      return next;
   }
   public int getOffset()
   {
      return offset;
   }
   public String getPrevious()
   {
      return previous;
   }
   public int getTotal()
   {
      return total;
   }
   
}
