package org.mobley.album.spotify;

import java.util.Arrays;

public class SpotifyTracksPagingObject extends SpotifyPagingObject
{

   private SpotifyTrack[] items;

   public SpotifyTrack[] getItems()
   {
      return items;
   }

   @Override
   public String toString()
   {
      return "SpotifyTracksPagingObject [items=" + Arrays.toString(items) + ", getHref()=" + getHref() + ", getLimit()=" + getLimit() + ", getNext()=" + getNext()
            + ", getOffset()=" + getOffset() + ", getPrevious()=" + getPrevious() + ", getTotal()=" + getTotal() + "]";
   }

}
