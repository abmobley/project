package org.mobley.album;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.mobley.album.allmusic.AllMusicUtil;
import org.mobley.album.data.AlbumManager;

public class EditorsChoiceProcessor
{

   public static void main(String[] args)
   {
      Document doc = null;

      try
      {
         doc = AllMusicUtil.getDocument("http://www.allmusic.com/newreleases/editorschoice/march-2015");
      }
      catch (HttpStatusException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      if (doc != null)
      {
         Set<String> editorsChoiceUrls = AllMusicUtil.getEditorChoiceURLs(doc);
         for (String url : editorsChoiceUrls)
         {
            try
            {
               SimilarProcessor.processSimilarAlbums(url, 5);
            }
            catch (Exception e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }

         System.out.println(doc.location());
         System.out.println("finished");
      }
   }

}
