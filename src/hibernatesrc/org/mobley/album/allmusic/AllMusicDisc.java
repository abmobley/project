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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((tracks == null) ? 0 : tracks.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AllMusicDisc other = (AllMusicDisc) obj;
      if (tracks == null)
      {
         if (other.tracks != null)
            return false;
      }
      else if (!tracks.equals(other.tracks))
         return false;
      return true;
   }
   
   
}
