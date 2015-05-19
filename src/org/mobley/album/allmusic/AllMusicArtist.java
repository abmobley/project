package org.mobley.album.allmusic;

public class AllMusicArtist
{

   private String name;
   private String id;
   private String url;
   
   public AllMusicArtist(String name, String url)
   {
      super();
      this.name = name;
      this.url = url;
      setIdFromUrl(url);
   }
   
   private void setIdFromUrl(String url)
   {
      int index = url.lastIndexOf('-');
      if(index > -1)
      {
         id = url.substring(index+1).toUpperCase();
      }
   }
   
   public String getName()
   {
      return name;
   }
   
   public String getId()
   {
      return id;
   }
   
   public String getUrl()
   {
      return url;
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
      AllMusicArtist other = (AllMusicArtist) obj;
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
      return "AllMusicArtist [name=" + name + ", id=" + id + ", url=" + url + "]";
   }
   
}
