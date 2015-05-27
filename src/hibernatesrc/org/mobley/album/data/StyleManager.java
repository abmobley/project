package org.mobley.album.data;

import java.util.Set;

import org.hibernate.Session;

public class StyleManager
{

   public static Genre getGenre(String id, String name)
   {

      Genre genre = null;
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      genre = (Genre) session.get(Genre.class, id);

      if (genre == null)
      {
         genre = new Genre(id, name);
         session.save(genre);
      }

      session.getTransaction().commit();

      return genre;
   }

   public static void setSubgenreforStyles(Genre genre, String name, Set<Style> styles)
   {

      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      for (Style style : styles)
      {
         session.load(style, style.getId());
         style.setGenre(genre.getId());
         if (name == null)
         {
            style.setSubgenre(style.getName());
         }
         else
         {
            style.setSubgenre(name);
         }
         session.update(style);
      }

      session.getTransaction().commit();
   }

   public static Style getStyle(String id, String name)
   {

      Style style = null;
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      style = (Style) session.get(Style.class, id);

      if (style == null)
      {
         style = new Style(id, name);
         session.save(style);
      }

      session.getTransaction().commit();

      return style;
   }

}
