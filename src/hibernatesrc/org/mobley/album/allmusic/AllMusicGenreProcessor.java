package org.mobley.album.allmusic;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicGenreProcessor
{

   public static void main(String[] args) throws Exception
   {
      Document doc = AllMusicUtil.getDocument("http://www.allmusic.com/genres");
      Elements elements = doc.select(".genre h3 a");
      for(Element element : elements) {
         String href = element.attr("href");
         String genreId = AllMusicUtil.getIdFromUrl(href);
         System.out.println("Processing genre: " + genreId);
         Document genreDoc = null;
         int attempt = 0;
         while(genreDoc == null && attempt < 5) {
            attempt++;
            try
            {
               genreDoc = AllMusicUtil.getDocument(href);
               
               boolean hasSubGenres = false;
               Elements subgenres = genreDoc.select(".subgenres > li > .genre-links");
               for(Element subgenre : subgenres) {
                  processSubgenre(subgenre, true);
                  hasSubGenres = true;
               }
               subgenres = genreDoc.select(".subgenres > li > .genre-parent");
               for(Element subgenre : subgenres) {
                  processSubgenre(subgenre, false);
                  hasSubGenres = true;
               }
               if(!hasSubGenres) {
                  System.out.println("Has no subgenres so looking for styles directly under genre.");
                  List<String> styles = new ArrayList<String>();
                  Elements styleElements = genreDoc.select(".styles a");
                  for(Element styleElement : styleElements) {
                     styles.add(AllMusicUtil.getIdFromUrl(styleElement.attr("href")));
                  }
                  System.out.println(null + ": " + styles);
               }
            }
            catch (Exception e)
            {

            }
         }
         System.out.println();
      }
   }

   private static void processSubgenre(Element subgenre, boolean isStyle)
   {
      String name = subgenre.ownText();
      List<String> styles = new ArrayList<String>();
      if(isStyle) styles.add(AllMusicUtil.getIdFromUrl(subgenre.attr("href")));
      Elements styleElements = subgenre.parent().select("ul.styles li a");
      for(Element styleElement : styleElements) {
         styles.add(AllMusicUtil.getIdFromUrl(styleElement.attr("href")));
      }
      System.out.println(name + ": " + styles);
   }

}
