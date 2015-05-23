package org.mobley.album.allmusic;

import java.util.List;

public class AllMusicRelease
{

   private String id;
   private String title;
   private String date;
   private String label;
   private String format;
   private boolean isMain;
   private String imageSrc;
   private List<AllMusicDisc> discs;

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

   public String getDate()
   {
      return date;
   }

   public void setDate(String date)
   {
      this.date = date;
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

   public List<AllMusicDisc> getDiscs()
   {
      return discs;
   }

   public void setDiscs(List<AllMusicDisc> discs)
   {
      this.discs = discs;
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
      AllMusicRelease other = (AllMusicRelease) obj;
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
      return "AllMusicRelease [id=" + id + ", title=" + title + ", date=" + date + ", label=" + label + ", format=" + format + ", isMain=" + isMain + ", imageSrc=" + imageSrc
            + ", discs=" + discs + "]";
   }

}
