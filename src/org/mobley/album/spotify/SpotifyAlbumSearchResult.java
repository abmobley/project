package org.mobley.album.spotify;

public class SpotifyAlbumSearchResult
{

   private SpotifyAlbumsPagingObject albums;

   public SpotifyAlbumsPagingObject getAlbums()
   {
      return albums;
   }

   @Override
   public String toString()
   {
      return "SpotifyAlbumSearchResult [albums=" + albums + "]";
   }
}
