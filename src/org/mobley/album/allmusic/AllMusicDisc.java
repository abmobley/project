package org.mobley.album.allmusic;

import java.util.List;

public class AllMusicDisc
{
   private List<AllMusicTrack> tracks;

   public AllMusicDisc(List<AllMusicTrack> tracks)
   {
      super();
      this.tracks = tracks;
   }

   public List<AllMusicTrack> getTracks()
   {
      return tracks;
   }

   @Override
   public String toString()
   {
      return "AllMusicDisc [tracks=" + tracks + "]";
   }
   
   
}
