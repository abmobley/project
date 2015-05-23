package org.mobley.album.allmusic;

public class AllMusicTrack
{


   private final String id;
   private int movement;
   private final String title;
   private long duration;
   private boolean isPick;
   private String spotifyId;
   
   public AllMusicTrack(String id, String title, boolean isPick)
   {
      super();
      this.id = id;
      this.title = title;
      this.isPick = isPick;
   }

   public String getId()
   {
      return id;
   }

   public String getTitle()
   {
      return title;
   }

   public int getMovement()
   {
      return movement;
   }

   public void setMovement(int movement)
   {
      this.movement = movement;
   }

   public long getDuration()
   {
      return duration;
   }

   public void setDuration(long duration)
   {
      this.duration = duration;
   }

   public boolean isPick()
   {
      return isPick;
   }
   
   public void setPick(boolean isPick)
   {
      this.isPick = isPick;
   }

   public String getSpotifyId()
   {
      return spotifyId;
   }

   public void setSpotifyId(String spotifyId)
   {
      this.spotifyId = spotifyId;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + movement;
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
      AllMusicTrack other = (AllMusicTrack) obj;
      if (id == null)
      {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      if (movement != other.movement)
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "AllMusicTrack [id=" + id + ", movement=" + movement + ", title=" + title + ", duration=" + duration + ", isPick=" + isPick + ", spotifyId=" + spotifyId + "]";
   }
   
}
