package org.mobley.album.allmusic;

public class AllMusicTrack
{

   private final String id;
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

   public void setPick(boolean isPick) {
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
   public String toString() {
	return "AllMusicTrack [id=" + id + ", title=" + title + ", duration="
			+ duration + ", isPick=" + isPick + ", spotifyId=" + spotifyId
			+ "]";
   }
   
}
