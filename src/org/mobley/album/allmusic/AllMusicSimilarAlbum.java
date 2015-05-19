package org.mobley.album.allmusic;

public class AllMusicSimilarAlbum implements Comparable<AllMusicSimilarAlbum>
{

   private final String href;
   private final short rating;
   private final String id;
   
   
   public AllMusicSimilarAlbum(String href, short rating)
   {
      super();
      this.href = href;
      this.rating = rating;
      this.id = AllMusicUtil.getIdFromUrl(href);
   }


   public String getHref()
   {
      return href;
   }

   public short getRating()
   {
      return rating;
   }

   public String getId()
   {
      return id;
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
      AllMusicSimilarAlbum other = (AllMusicSimilarAlbum) obj;
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
   public int compareTo(AllMusicSimilarAlbum o)
   {
      int diff = this.rating - o.rating;
      if(diff == 0)
      {
         diff = this.id.compareTo(o.id);
      }
      return diff;
   }


   @Override
   public String toString()
   {
      return "AllMusicSimilarAlbum [href=" + href + ", rating=" + rating + ", id=" + id + "]";
   }

}
