package org.mobley.album.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table (name="ALBUMS")
@SuppressWarnings("serial")
public class Album implements Serializable
{

   public Album()
   {
      super();
   }
   
   private String id;
   private short rating;
   private String imagesrc;
   private String title;
   private String url;
   private int timesSkipped;
   private Set<Artist> artists = new HashSet<Artist>();
   private List<Release> releases = new ArrayList<Release>();
   private Set<Genre> genres = new HashSet<Genre>();
   private Set<Style> styles = new HashSet<Style>();
   private Set<Mood> moods = new HashSet<Mood>();
   private Set<Theme> themes = new HashSet<Theme>();

   @Id
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public short getRating()
   {
      return rating;
   }

   public void setRating(short rating)
   {
      this.rating = rating;
   }

   public String getImagesrc()
   {
      return imagesrc;
   }

   public void setImagesrc(String imagesrc)
   {
      this.imagesrc = imagesrc;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getUrl()
   {
      return url;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }
   
   public int getTimesSkipped()
   {
      return timesSkipped;
   }

   public void setTimesSkipped(int timesSkipped)
   {
      this.timesSkipped = timesSkipped;
   }

   @ManyToMany(cascade=CascadeType.ALL)
   @JoinTable(name="ALBUM_ARTISTS",joinColumns=@JoinColumn(name="ALBUM_ID"),inverseJoinColumns=@JoinColumn(name="ARTIST_ID"))
   public Set<Artist> getArtists()
   {
      return artists;
   }

   public void setArtists(Set<Artist> artists)
   {
      this.artists = artists;
   }

   @OneToMany(cascade=CascadeType.ALL,mappedBy="album")
   public List<Release> getReleases()
   {
      return releases;
   }

   public void setReleases(List<Release> releases)
   {
      this.releases = releases;
   }

   @ManyToMany(cascade=CascadeType.ALL)
   @JoinTable(name="ALBUM_GENRES",joinColumns=@JoinColumn(name="ALBUM_ID"),inverseJoinColumns=@JoinColumn(name="GENRE_ID"))
   public Set<Genre> getGenres()
   {
      return genres;
   }

   public void setGenres(Set<Genre> genres)
   {
      this.genres = genres;
   }

   @ManyToMany(cascade=CascadeType.ALL)
   @JoinTable(name="ALBUM_STYLES",joinColumns=@JoinColumn(name="ALBUM_ID"),inverseJoinColumns=@JoinColumn(name="STYLE_ID"))
   public Set<Style> getStyles()
   {
      return styles;
   }

   public void setStyles(Set<Style> styles)
   {
      this.styles = styles;
   }

   @ManyToMany(cascade=CascadeType.ALL)
   @JoinTable(name="ALBUM_MOODS",joinColumns=@JoinColumn(name="ALBUM_ID"),inverseJoinColumns=@JoinColumn(name="MOOD_ID"))
   public Set<Mood> getMoods()
   {
      return moods;
   }

   public void setMoods(Set<Mood> moods)
   {
      this.moods = moods;
   }

   @ManyToMany(cascade=CascadeType.ALL)
   @JoinTable(name="ALBUM_THEMES",joinColumns=@JoinColumn(name="ALBUM_ID"),inverseJoinColumns=@JoinColumn(name="THEME_ID"))
   public Set<Theme> getThemes()
   {
      return themes;
   }

   public void setThemes(Set<Theme> themes)
   {
      this.themes = themes;
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
      Album other = (Album) obj;
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
      return "Album [id=" + id + ", rating=" + rating + ", imagesrc=" + imagesrc + ", title=" + title + ", url=" + url + ", timesSkipped=" + timesSkipped + ", artists=" + artists
            + ", releases=" + releases + ", genres=" + genres + ", styles=" + styles + ", moods=" + moods + ", themes=" + themes + "]";
   }
   
   

}
