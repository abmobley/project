package org.mobley.album.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="STYLES")
@SuppressWarnings("serial")
public class Style implements Serializable
{

   public Style()
   {
      super();
   }

   public Style(String id, String name)
   {
      this.id = id;
      this.name = name;
   }

   private String id;
   private String name;
   private String subgenre;
   private String genre;
   
   @Id
   public String getId()
   {
      return id;
   }
   
   public void setId(String id)
   {
      this.id = id;
   }
   
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }

   public String getSubgenre()
   {
      return subgenre;
   }

   public void setSubgenre(String subgenre)
   {
      this.subgenre = subgenre;
   }


   public String getGenre()
   {
      return genre;
   }

   public void setGenre(String genre)
   {
      this.genre = genre;
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
      Style other = (Style) obj;
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
      return "Style [id=" + id + ", name=" + name + ", subgenre=" + subgenre + ", genre=" + genre + "]";
   }
   
   
}
