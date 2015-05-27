package org.mobley.album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.util.security.Credential;
import org.mobley.album.data.Album;
import org.mobley.album.data.AlbumManager;
import org.mobley.album.data.Release;
import org.mobley.album.data.Track;

public class ListRandomizer
{

   private static final String POP_ROCK = "MA0000002613";
   private static final String JAZZ = "MA0000002674";
   private static final String ELECTRONIC = "MA0000002572";
   private static final String BLUES = "MA0000002467";
   private static final String COUNTRY = "MA0000002532";
   private static final String FOLK = "MA0000002592";
   private static final String INTERNATIONAL = "MA0000002660";
   private static final String NEW_AGE = "MA0000002745";
   private static final String R_AND_B = "MA0000002809";
   private static final String RAP = "MA0000002816";
   private static final String REGGAE = "MA0000002820";
   private static final String LATIN = "MA0000002692";
   private static final String AVANT_GARDE = "MA0000012170";
   private static final String EASY_LISTENING = "MA0000002567";
   private static final String VOCAL = "MA0000011877";

   private static final double TOTAL_TIME_ALL_LISTS = 10 * 60 * 60 * 1000; // 10

   // hours
   // in
   // millis

   /**
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception
   {
      // 0 Pop/Rock
      // 1 Jazz
      // 2 Electronic
      // 3 Country/Blues/Folk
      // 4 International/Latin/Avant-Garde
      // 5 R&B/Rap/Reggae
      // 6 New Age/Easy Listening/Vocal
      // Credential credential = SpotifyLoginServer.authorize();
      List<Playlist> playlists = createPlaylistDefinitions();

      double total = 0;
      for (Playlist playlist : playlists)
      {
         Set<Album> albums = new HashSet<Album>();
         if (playlist.hasSubGenres())
         {
            albums = AlbumManager.getAlbumsForSubgenres(playlist.subGenres);
         }
         else
         {
            albums = AlbumManager.getAlbumsForGenre(playlist.genreId);
         }

         playlist.setAlbums(albums);

         total += playlist.getDuration();

      }

      for (Playlist playlist : playlists)
      {

         System.out.println(playlist.title + " " + playlist.getDuration());
      }

      System.out.println("Total duration " + total);
      Set<Track> tracksUsed = new HashSet<Track>();
      for (Playlist playlist : playlists)
      {
         System.out.println("Creating " + playlist.getTitle() + ": " + (playlist.getDuration() / total));
         createPlaylist(playlist, total, tracksUsed, null);
      }
   }

   private static void createPlaylist(Playlist playlist, double total, Set<Track> tracksUsed, Credential credential) throws Exception
   {
      double duration = (playlist.getDuration() / total) * TOTAL_TIME_ALL_LISTS;
      double playlistDuration = 0;
      List<Track> tracks = new ArrayList<Track>();
      List<Track> trackList = new ArrayList<Track>();
      Map<String,Album> albums = new HashMap<String,Album>();

      for (Album album : playlist.getAlbums())
      {
         for(Release release : album.getReleases()) {
            for(Track track : release.getTracks()) {
         if (track.getDuration() > 0 && !tracksUsed.contains(track))
         {
            albums.put(track.getPk().getId(), album);
            short rating = album.getRating();
            if (track.isPick())
               rating++;
            for (int i = 0; i < rating; i++)
            {
               tracks.add(track);
            }
         }
         }
         }
      }

      while (playlistDuration < duration)
      {
         int index = (int) (Math.random() * tracks.size());
         Track track = tracks.get(index);
         if (!trackList.contains(track))
         {
            playlistDuration += track.getDuration();
            trackList.add(track);
            System.out.println(track.getTitle());
         }
      }

      List<Track> tracksToAdd = new ArrayList<Track>();
      for (Track track : trackList)
      {
         Album album = albums.get(track.getPk().getId());
         if (track.getSpotifyId() != null && track.getSpotifyId().trim().length() > 0)
            tracksToAdd.add(track);
         System.out.println(track.getTitle() + " " + album.getTitle() + " " + track.getDuration() + " " + track.getSpotifyId());
      }

      /*try
      {
         SpotifyLoginServer.replacePlaylist(tracksToAdd, playlist.getId(), credential);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }*/
   }

   private static List<Playlist> createPlaylistDefinitions()
   {
      List<Playlist> playlists = new ArrayList<Playlist>();

      Playlist alternative = new Playlist("Alternative", "27Puf66ulrlpZOdkbrXPs1", POP_ROCK);
      alternative.addSubGenre("Alternative/Indie Rock");
      playlists.add(alternative);

      Playlist art = new Playlist("Art", "42D0dBTFk3AktH8lHNIU7S", POP_ROCK);
      art.addSubGenre("Art-Rock/Experimental");
      playlists.add(art);

      Playlist rootsRock = new Playlist("Roots Rock", "7py5V2Zi4aA9NDBHJipBXn", POP_ROCK);
      rootsRock.addSubGenre("British Invasion");
      rootsRock.addSubGenre("Psychedelic/Garage");
      rootsRock.addSubGenre("Rock & Roll/Roots");
      playlists.add(rootsRock);

      Playlist euro = new Playlist("Euro", "5gymiLHAAnMiXYjoRN6KEc", POP_ROCK);
      euro.addSubGenre("Dance");
      euro.addSubGenre("Europop");
      euro.addSubGenre("Foreign Language Rock");
      playlists.add(euro);

      Playlist singerSongwriter = new Playlist("Singer/Songwriter", "7Fj09d7RiK1lIubaeVeaGa", POP_ROCK);
      singerSongwriter.addSubGenre("Folk/Country Rock");
      singerSongwriter.addSubGenre("Singer/Songwriter");
      playlists.add(singerSongwriter);

      Playlist hard = new Playlist("Hard", "26cgnmFkCUdZQBmIWmzfBN", POP_ROCK);
      hard.addSubGenre("Hard Rock");
      hard.addSubGenre("Heavy Metal");
      playlists.add(hard);

      Playlist pop = new Playlist("Pop", "60hZAqxfS2MO2Sve06dUFf", POP_ROCK);
      pop.addSubGenre(null);
      pop.addSubGenre("Pop/Rock");
      pop.addSubGenre("Soft Rock");
      playlists.add(pop);

      Playlist punk = new Playlist("Punk/New Wave", "5juEZhOo35y0rWnUfYvg7V", POP_ROCK);
      punk.addSubGenre("Punk/New Wave");
      playlists.add(punk);

      Playlist classicJazz = new Playlist("Classic Jazz", "0zGZcVZQtYayza2mWOJiX2", JAZZ);
      classicJazz.addSubGenre("Big Band/Swing");
      classicJazz.addSubGenre("New Orleans/Classic Jazz");
      playlists.add(classicJazz);

      Playlist bop = new Playlist("Bop", "5lIIdugqgbmjZtHCuRFWYi", JAZZ);
      bop.addSubGenre("Bop");
      bop.addSubGenre("Cool");
      bop.addSubGenre("Hard Bop");
      playlists.add(bop);

      Playlist freeJazz = new Playlist("Free Jazz", "745JBMnVGjF1CXt4jEexEN", JAZZ);
      freeJazz.addSubGenre("Free Jazz");
      playlists.add(freeJazz);

      Playlist jazzInstrument = new Playlist("Jazz Instrument", "4a8SVneyNUcUV7nSHJoFUz", JAZZ);
      jazzInstrument.addSubGenre(null);
      jazzInstrument.addSubGenre("Jazz Instrument");
      playlists.add(jazzInstrument);

      Playlist fusion = new Playlist("Fusion", "4eFb19zrKWy03fIacMncEO", JAZZ);
      fusion.addSubGenre("Fusion");
      fusion.addSubGenre("Latin Jazz/World Fusion");
      fusion.addSubGenre("Soul Jazz/Groove");
      playlists.add(fusion);

      Playlist contemporaryJazz = new Playlist("Contemporary Jazz", "5lP2GtoLcLLli638qfvYTF", JAZZ);
      contemporaryJazz.addSubGenre("Contemporary Jazz");
      playlists.add(contemporaryJazz);

      Playlist electronica = new Playlist("Electronica", "6H43U64Pa5LFeoEX0qbvmS", ELECTRONIC);
      electronica.addSubGenre(null);
      electronica.addSubGenre("Electronica");
      playlists.add(electronica);

      Playlist downtempo = new Playlist("Downtempo", "6xUvIT9qQeb5Q3zYxjZ8ky", ELECTRONIC);
      downtempo.addSubGenre("Downtempo");
      downtempo.addSubGenre("Experimental Electronic");
      playlists.add(downtempo);

      Playlist club = new Playlist("Club", "5kaSnMINyEAGtojYJiKV2S", ELECTRONIC);
      club.addSubGenre("House");
      club.addSubGenre("Jungle/Drum'n'Bass");
      club.addSubGenre("Techno");
      club.addSubGenre("Trance");
      playlists.add(club);

      playlists.add(new Playlist("Avant-Garde", "76vxgsOISVMwFq2zSKh2sd", AVANT_GARDE));
      playlists.add(new Playlist("Blues", "5zHPKA1FqQ54UcVLdMMRX6", BLUES));
      playlists.add(new Playlist("Country", "56Yzzf7TGOA2zPHVlgH2KZ", COUNTRY));
      playlists.add(new Playlist("Easy Listening", "4d8MzRTfDn2Y5pONIBM8CD", EASY_LISTENING));
      playlists.add(new Playlist("Folk", "5tuK0tB9rXdWKOZIZzIebs", FOLK));
      playlists.add(new Playlist("International", "3p2tVoRgqiDef0UjPRlJqM", INTERNATIONAL));
      playlists.add(new Playlist("Latin", "3FU3zvqL9b8MKeS6DhuCup", LATIN));
      playlists.add(new Playlist("New Age", "00NZRiHQQnQnBdYFpXwmy8", NEW_AGE));
      playlists.add(new Playlist("R&B", "21YraBzrGv9Xv2AK2UabmZ", R_AND_B));
      playlists.add(new Playlist("Rap", "5kMvwZXTlLWAcMwGJqc4uC", RAP));
      playlists.add(new Playlist("Reggae", "4A4Be0V5zuy0I5FHi2qsCd", REGGAE));
      playlists.add(new Playlist("Vocal", "1AlxCZSyJrHJ0Hda1ikxIt", VOCAL));
      return playlists;
   }

   private static class Playlist
   {
      private final String id;
      private final String genreId;
      private final String title;
      private final Set<String> subGenres = new HashSet<String>();
      private Set<Album> albums = new HashSet<Album>();
      private long duration = 0;

      public Playlist(String title, String id, String genreId)
      {
         this.id = id;
         this.genreId = genreId;
         this.title = title;
      }

      public Set<Album> getAlbums()
      {
         return albums;
      }

      public void setAlbums(Set<Album> albums)
      {
         this.albums = albums;

         for (Album album : albums)
         {
            for (Release release : album.getReleases())
            {
               for (Track track : release.getTracks())
               {
                  duration += track.getDuration();

               }
            }
         }
      }

      public String getId()
      {
         return id;
      }

      public String getTitle()
      {
         return title;
      }

      public long getDuration()
      {
         return duration;
      }

      public boolean hasSubGenres()
      {
         return !subGenres.isEmpty();
      }

      public void addSubGenre(String subGenre)
      {
         subGenres.add(subGenre);
      }

      public boolean containsSubGenre(String subGenre)
      {
         return subGenres.contains(subGenre);
      }

      public String getGenreId()
      {
         return genreId;
      }

   }
}
