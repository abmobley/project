package org.mobley.album.allmusic;

import java.util.List;
import java.util.Map;

public class AllMusicAlbum
{

   private final String title;
   private final String id;
   private String spotifyId;
   private short rating;
   private String imageSrc;
   private final String url;
   private List<AllMusicArtist> artists;
   private List<AllMusicRelease> releases;
   private Map<String,String> genres;
   private Map<String, String> moods;
   private Map<String, String> themes;
   private Map<String,String> styles;
   
   public AllMusicAlbum(String title, String url)
   {
      super();
      this.id = AllMusicUtil.getIdFromUrl(url);
      this.title = title;
      this.url = url;
   }

   public String getSpotifyId()
   {
      return spotifyId;
   }

   public void setSpotifyId(String spotifyId)
   {
      this.spotifyId = spotifyId;
   }

   public short getRating()
   {
      return rating;
   }

   public void setRating(short rating)
   {
      this.rating = rating;
   }

   public String getImageSrc()
   {
      return imageSrc;
   }

   public void setImageSrc(String imageSrc)
   {
      this.imageSrc = imageSrc;
   }

   public List<AllMusicArtist> getArtists()
   {
      return artists;
   }

   public void setArtists(List<AllMusicArtist> artists)
   {
      this.artists = artists;
   }

   public List<AllMusicRelease> getReleases()
   {
      return releases;
   }

   public void setReleases(List<AllMusicRelease> releases)
   {
      this.releases = releases;
   }

   public String getTitle()
   {
      return title;
   }

   public String getId()
   {
      return id;
   }

   public String getUrl()
   {
      return url;
   }

   public Map<String, String> getGenres()
   {
      return genres;
   }

   public void setGenres(Map<String, String> genres)
   {
      this.genres = genres;
   }

   public Map<String, String> getMoods()
   {
      return moods;
   }

   public void setMoods(Map<String, String> moods)
   {
      this.moods = moods;
   }

   public Map<String, String> getThemes()
   {
      return themes;
   }

   public void setThemes(Map<String, String> themes)
   {
      this.themes = themes;
   }

   public Map<String,String> getStyles()
   {
      return styles;
   }

   public void setStyles(Map<String,String> styles)
   {
      this.styles = styles;
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
      AllMusicAlbum other = (AllMusicAlbum) obj;
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
      return "AllMusicAlbum [title=" + title + ", id=" + id + ", spotifyId=" + spotifyId + ", rating=" + rating + ", imageSrc=" + imageSrc + ", url=" + url + ", artists="
            + artists + ", releases=" + releases + ", genres=" + genres + ", moods=" + moods + ", themes=" + themes + ", styles=" + styles + "]";
   }
   
   
}
