package org.mobley.album.data;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="TRACKS")
@SuppressWarnings("serial")
public class Track implements Serializable
{

   public Track()
   {
      super();
   }

   private TrackPK pk;
   private String title;
   private long duration;
   private boolean isPick;
   private String spotifyId;
   private Release release;

   @EmbeddedId
   public TrackPK getPk()
   {
      return pk;
   }

   public void setPk(TrackPK pk)
   {
      this.pk = pk;
   }

   public String getTitle()
   {
      return title;
   }
   
   public void setTitle(String title)
   {
      this.title = title;
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

   @ManyToOne(cascade=CascadeType.ALL)
   public Release getRelease()
   {
      return release;
   }

   public void setRelease(Release release)
   {
      this.release = release;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((pk == null) ? 0 : pk.hashCode());
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
      Track other = (Track) obj;
      if (pk == null)
      {
         if (other.pk != null)
            return false;
      }
      else if (!pk.equals(other.pk))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "Track [pk=" + pk + ", title=" + title + ", duration=" + duration + ", isPick=" + isPick + ", spotifyId=" + spotifyId + "]";
   }
   
   
}
