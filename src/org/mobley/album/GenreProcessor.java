package org.mobley.album;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mobley.album.allmusic.AllMusicUtil;
import org.mobley.album.data.Genre;
import org.mobley.album.data.Style;
import org.mobley.album.data.StyleManager;

public class GenreProcessor
{

   public static void main(String[] args) throws Exception
   {
      processGenres();
   }

   private static void processGenres() throws Exception
   {
      Document doc = AllMusicUtil.getDocument("http://www.allmusic.com/genres");
      Elements elements = doc.select(".genre h3 a");

      for (Element element : elements)
      {

         String href = element.attr("href");
         String id = AllMusicUtil.getIdFromUrl(href);
         String name = element.ownText().trim();

         System.out.println("Getting genre: " + id + ", " + name);
         Genre genre = StyleManager.getGenre(id, name);

         System.out.println("Processing genre: " + genre);
         Document genreDoc = AllMusicUtil.getDocument(href);

         try
         {
            boolean hasSubGenres = false;

            Elements subgenres = genreDoc.select(".subgenres > li > .genre-links");

            for (Element subgenre : subgenres)
            {

               processSubgenre(genre, subgenre, true);

               hasSubGenres = true;

            }

            subgenres = genreDoc.select(".subgenres > li > .genre-parent");

            for (Element subgenre : subgenres)
            {

               processSubgenre(genre, subgenre, false);

               hasSubGenres = true;

            }

            if (!hasSubGenres)
            {

               System.out.println("Has no subgenres so looking for styles directly under genre.");

               Set<Style> styles = new HashSet<Style>();

               Elements styleElements = genreDoc.select(".styles a");

               for (Element styleElement : styleElements)
               {

                  String styleId = AllMusicUtil.getIdFromUrl(styleElement.attr("href"));
                  String styleName = styleElement.ownText().trim();
                  styles.add(StyleManager.getStyle(styleId, styleName));

               }

               if (!styles.isEmpty())
               {
                  StyleManager.setSubgenreforStyles(genre, null, styles);
               }

            }

         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
         }

         System.out.println();
      }
   }

   private static void processSubgenre(Genre genre, Element subgenre, boolean isStyle) throws Exception

   {

      String name = subgenre.ownText();

      Set<Style> styles = new HashSet<Style>();

      if (isStyle) {
         String id = AllMusicUtil.getIdFromUrl(subgenre.attr("href"));
         styles.add(StyleManager.getStyle(id,name));
      }

      Elements styleElements = subgenre.parent().select("ul.styles li a");

      for (Element styleElement : styleElements)
      {

         String id = AllMusicUtil.getIdFromUrl(styleElement.attr("href"));
         String styleName = styleElement.ownText().trim();
         styles.add(StyleManager.getStyle(id,styleName));

      }

      StyleManager.setSubgenreforStyles(genre, name, styles);
   }
}
