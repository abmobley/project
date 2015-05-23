package org.mobley.album.data;

import java.io.Serializable;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class TrackPK implements Serializable
{
   public String id;
   public int movement;

   public TrackPK()
   {
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public int getMovement()
   {
      return movement;
   }

   public void setMovement(int movement)
   {
      this.movement = movement;
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
      TrackPK other = (TrackPK) obj;
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
      return "TrackPK [id=" + id + ", movement=" + movement + "]";
   }
   
   
}