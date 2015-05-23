package org.mobley.album.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "RELEASES")
@SuppressWarnings("serial")
public class Release implements Serializable
{

   public Release()
   {
      super();
   }

   private String id;
   private String title;
   private String releaseDate;
   private String label;
   private String format;
   private boolean isMain;
   private String imageSrc;
   private String spotifyid;
   private List<Track> tracks = new ArrayList<Track>();
   private Album album;

   @Id
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getReleaseDate()
   {
      return releaseDate;
   }

   public void setReleaseDate(String releaseDate)
   {
      this.releaseDate = releaseDate;
   }

   public String getLabel()
   {
      return label;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }

   public String getFormat()
   {
      return format;
   }

   public void setFormat(String format)
   {
      this.format = format;
   }

   public boolean isMain()
   {
      return isMain;
   }

   public void setMain(boolean isMain)
   {
      this.isMain = isMain;
   }

   public String getImageSrc()
   {
      return imageSrc;
   }

   public void setImageSrc(String imageSrc)
   {
      this.imageSrc = imageSrc;
   }

   public String getSpotifyid()
   {
      return spotifyid;
   }

   public void setSpotifyid(String spotifyid)
   {
      this.spotifyid = spotifyid;
   }

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "release")
   public List<Track> getTracks()
   {
      return tracks;
   }

   public void setTracks(List<Track> tracks)
   {
      this.tracks = tracks;
   }

   @ManyToOne(cascade=CascadeType.ALL)
   public Album getAlbum()
   {
      return album;
   }

   public void setAlbum(Album album)
   {
      this.album = album;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      Release other = (Release) obj;
      if (id == null)
      {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      StringBuilder b = new StringBuilder();
      if(isMain)
      {
         b.append("x ");
      }
      b.append(format).append(" ");
      b.append(title).append(" ");
      b.append(label).append(" ");
      b.append(releaseDate);
      return b.toString();
   }

}
