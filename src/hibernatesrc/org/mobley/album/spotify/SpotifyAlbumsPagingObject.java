package org.mobley.album.spotify;

import java.util.Arrays;

public class SpotifyAlbumsPagingObject extends SpotifyPagingObject
{

   private SpotifyAlbum[] items;

   public SpotifyAlbum[] getItems()
   {
      return items;
   }

   @Override
   public String toString()
   {
      return "SpotifyAlbumsPagingObject [items=" + Arrays.toString(items) + ", getHref()=" + getHref() + ", getLimit()=" + getLimit() + ", getNext()=" + getNext()
            + ", getOffset()=" + getOffset() + ", getPrevious()=" + getPrevious() + ", getTotal()=" + getTotal() + "]";
   }
   
   
}
