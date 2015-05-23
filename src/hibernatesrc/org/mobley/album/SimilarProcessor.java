package org.mobley.album;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicSimilarAlbum;
import org.mobley.album.allmusic.AllMusicUtil;
import org.mobley.album.data.Album;
import org.mobley.album.data.AlbumManager;

public class SimilarProcessor
{

   private static final String SIMILAR_URL_START = "http://www.allmusic.com/album/ajax/";
   private static final String SIMILAR_URL_FINISH = "/similar/listview";
   private static final String ALBUM_URL_START = "http://www.allmusic.com/album/";
   
   public static void processSimilarAlbums(String url,int number) throws Exception
   {
      Document albumDoc = AllMusicUtil.getDocument(url);
      
      Album album = AlbumManager.getAlbum(AllMusicUtil.getIdFromUrl(url));
      
      if(album == null)
      {
         System.out.println("Adding new album from: " + url);
         AllMusicAlbum allMusicAlbum = AllMusicUtil.getAlbum(albumDoc);
         album = AlbumManager.saveAlbum(allMusicAlbum);
         if(album != null)
         {
            number--;
         }
      }
      else if(album.getTimesSkipped() > 0)
      {
         AlbumManager.incrementTimesSkipped(album.getId());
      }
      
      int index = ALBUM_URL_START.length();
      processNew(albumDoc.location().substring(index),0,number);
   }
   
   
   
   private static void processNew(String url, int index, int number) throws Exception {
      if (index >= number)
         return;

      String similarUrl = SIMILAR_URL_START + url + SIMILAR_URL_FINISH;
      Document doc = AllMusicUtil.getDocument(similarUrl);
      NavigableSet<AllMusicSimilarAlbum> sorted = AllMusicUtil.getSimilarAlbums(doc);
      
      Set<String> ids = new HashSet<String>();
      for(AllMusicSimilarAlbum similarAlbum : sorted) {
         ids.add(similarAlbum.getId());
      }
      if(ids.isEmpty()) {

         System.out.println("No similar found: " + doc.location());
         return;
      }
      Iterator<AllMusicSimilarAlbum> iterator = sorted.descendingIterator();
      String next = null;
      while(iterator.hasNext()) {
         AllMusicSimilarAlbum nextAlbum = iterator.next();
         String nextUrl = nextAlbum.getHref();
         Album album = AlbumManager.getAlbum(nextAlbum.getId());
         
         if(album == null)
         {
            System.out.println("Adding new album from: " + nextUrl);
            AllMusicAlbum allMusicAlbum = AllMusicUtil.getAlbum(AllMusicUtil.getDocument(nextUrl));
            album = AlbumManager.saveAlbum(allMusicAlbum);
            next = nextUrl;
            break;
         }
         else if(album.getTimesSkipped() > 0)
         {
            AlbumManager.incrementTimesSkipped(album.getId());
         }
      }

      if (next != null) {
         index++;
         int i = ALBUM_URL_START.length();
         processNew(next.substring(i), index, number);
      } else {
         System.out.println("No similar found.");
      }
   }

   public static void main(String[] args)
   {
      try
      {
         Set<String> urls = new HashSet<String>();

         SimilarProcessor.processSimilarAlbums("http://www.allmusic.com/album/odelay-mw0000647922",5);
         System.out.println(urls);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
