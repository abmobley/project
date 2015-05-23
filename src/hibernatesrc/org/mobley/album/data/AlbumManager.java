package org.mobley.album.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicArtist;
import org.mobley.album.allmusic.AllMusicDisc;
import org.mobley.album.allmusic.AllMusicRelease;
import org.mobley.album.allmusic.AllMusicTrack;
import org.mobley.album.spotify.SpotifyAlbum;
import org.mobley.album.spotify.SpotifyAlbumSearchResult;
import org.mobley.album.spotify.SpotifyTrack;
import org.mobley.album.spotify.SpotifyUtil;
import org.mobley.album.util.StringUtil;

public class AlbumManager
{

   public static Album getAlbum(String id)
   {
      Album album = null;
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      album = (Album) session.get(Album.class, id);

      session.getTransaction().commit();

      return album;
   }

   public static Album findAlbumFromURL(String url)
   {
      Album album = null;
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      album = (Album) session.createQuery("from Album as album where album.url = :url").setParameter("url", url).uniqueResult();

      Hibernate.initialize(album.getArtists());
      Hibernate.initialize(album.getReleases());
      for (Release r : album.getReleases())
      {
         Hibernate.initialize(r.getTracks());
      }

      session.getTransaction().commit();

      return album;
   }

   public static Album saveAlbum(AllMusicAlbum allMusicAlbum)
   {
      Album album = null;
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      album = (Album) session.get(Album.class, allMusicAlbum.getId());

      if (album == null)
      {
         album = convertAlbum(allMusicAlbum);
         session.save(album);
      }

      session.getTransaction().commit();

      return album;

   }

   public static List<String> findAlbumsNotProcessed()
   {
      List<String> idsNotInSpotify = new ArrayList<String>();
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      Query q = session.createQuery("select album.url from Album album where album.timesSkipped > 0 order by album.timesSkipped desc");

      idsNotInSpotify.addAll(q.list());

      session.flush();
      session.getTransaction().commit();

      return idsNotInSpotify;

   }

   public static void incrementTimesSkipped(String id)
   {

      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      Album album = (Album) session.get(Album.class, id);
      album.setTimesSkipped(album.getTimesSkipped() + 1);

      session.flush();
      session.getTransaction().commit();
   }

   public static Set<String> findIdsNotInDB(Set<String> ids)
   {

      Set<String> idsNotInDB = new HashSet<String>(ids);
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      Query q = session.createQuery("from Album album where album.id in (:idsSet)");
      q.setParameterList("idsSet", ids);

      List albums = q.list();
      Iterator iter = albums.iterator();

      while (iter.hasNext())
      {
         Album album = (Album) iter.next();
         idsNotInDB.remove(album.getId());
      }

      session.flush();
      session.getTransaction().commit();
      return idsNotInDB;
   }

   public static boolean supplementWithSpotifyInformation(Album album, String spotifyid)
   {
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      session.load(album, album.getId());

      Set<String> processedIds = new HashSet<String>();
      List<Release> releasesToProcess = new ArrayList<Release>();
      Release mainRelease = null;
      for (Release release : album.getReleases())
      {
         if (release.getSpotifyid() != null)
         {
            processedIds.add(release.getSpotifyid());
         }
         else
         {
            if (!release.getTracks().isEmpty())
            {
               if (release.isMain())
               {
                  mainRelease = release;
               }
               else
               {
                  releasesToProcess.add(release);
               }
            }
         }
      }

      boolean success = false;
      if (mainRelease != null)
      {
         if (supplementWithSpotifyInformation(mainRelease, album.getArtists(), spotifyid, processedIds))
         {
            success = true;
         }
      }

      for (Release release : releasesToProcess)
      {
         if (supplementWithSpotifyInformation(release, album.getArtists(), spotifyid, processedIds))
         {
            success = true;
         }
      }

      if (success)
      {
         album.setTimesSkipped(0);
         session.update(album);
      }

      session.flush();
      session.getTransaction().commit();

      return success;
   }

   private static boolean supplementWithSpotifyInformation(Release release, Set<Artist> artists, String spotifyid, Set<String> processedIds)
   {
      boolean success = false;
      try
      {

         // check if there was a spotify id in AllMusic
         // if so check if it matches
         if (spotifyid != null && !processedIds.contains(spotifyid))
         {
            SpotifyAlbum spotifyAlbum = SpotifyUtil.getAlbumFromId(spotifyid);

            if (spotifyAlbum != null)
            {
               if (SpotifyUtil.isAvailableInUS(spotifyAlbum.getAvailable_markets()))
               {
                  if (compareSpotifyIds(release, spotifyAlbum))
                  {
                     success = setTrackDurations(release, spotifyAlbum);
                     if (!success)
                     {
                        System.out.println("Failed to set track durations from SpotifyAlbum.");
                     }
                     else
                     {
                        release.setSpotifyid(spotifyid);
                        processedIds.add(spotifyid);
                     }
                  }
                  else
                  {
                     System.out.println("AllMusicRelease and SpotifyAlbum do not have same spotifyids");
                  }
               }
               else
               {
                  System.out.println("Spotify album is not available in US: " + spotifyid);
               }
            }
            else
            {
               System.out.println("Could not find spotify album from id " + "spotify:album:" + spotifyid);
            }
         }

         // if that wasn't successful try searching spotify for album using
         // title and artist
         if (!success)
         {
            SpotifyAlbumSearchResult result = SpotifyUtil.searchAlbum(release.getTitle(), artists);
            if (result != null && result.getAlbums().getItems() != null && result.getAlbums().getItems().length > 0)
            {
               for (SpotifyAlbum sa : result.getAlbums().getItems())
               {
                  SpotifyAlbum spotifyAlbum = SpotifyUtil.getAlbum(sa.getHref());
                  if (!processedIds.contains(spotifyAlbum.getId()))
                  {
                     if (compare(release, spotifyAlbum))
                     {
                        setSpotifyIdsAndDurations(release, spotifyAlbum.getId(), SpotifyUtil.getTracks(spotifyAlbum));
                        success = true;
                        processedIds.add(spotifyAlbum.getId());
                        break;
                     }
                  }
               }
            }
            else
            {
               System.out.println("Could not find matching spotify album. Title=" + release.getTitle() + ", artists=" + artists);
            }

         }
      }
      catch (Exception e)
      {
         System.out.println("Exception while trying to supplement album with spotify information.");
         e.printStackTrace();
         success = false;
      }

      return success;
   }

   private static boolean compareSpotifyIds(Release release, SpotifyAlbum spotifyAlbum)
   {
      Set<String> allMusicTrackIds = new HashSet<String>();
      Set<String> spotifyTrackIds = new HashSet<String>();

      for (Track track : release.getTracks())
      {
         allMusicTrackIds.add(track.getSpotifyId());
      }

      for (SpotifyTrack track : SpotifyUtil.getTracks(spotifyAlbum))
      {
         spotifyTrackIds.add(track.getId());
      }

      return allMusicTrackIds.equals(spotifyTrackIds);
   }

   private static boolean compare(Release release, SpotifyAlbum spotifyAlbum)
   {
      System.out.println("spotify:album:" + spotifyAlbum.getId());
      boolean areEqual = compareAlbumTitles(release.getTitle(), spotifyAlbum.getName());

      if (!compareTracks(release.getTracks(), SpotifyUtil.getTracks(spotifyAlbum)))
      {
         areEqual = false;
      }

      return areEqual;
   }

   private static boolean compareTracks(List<Track> tracks, List<SpotifyTrack> spotifyTracks)
   {
      boolean areEqual = tracks.size() == spotifyTracks.size();

      if (areEqual)
      {
         System.out.println("Number of tracks are the same: " + tracks.size());
      }
      else
      {
         System.out.println("Number of tracks are not the same: " + tracks.size() + "," + spotifyTracks.size());
      }

      for (int i = 0; i < tracks.size() && i < spotifyTracks.size(); i++)
      {
         Track track = tracks.get(i);
         SpotifyTrack spotifyTrack = spotifyTracks.get(i);
         String allMusicTrackName = track.getTitle().trim();
         String spotifyTrackName = spotifyTrack.getName().trim();
         System.out.println(i + " " + allMusicTrackName + " " + spotifyTrackName);
         if (!StringUtil.compareStrings(allMusicTrackName, spotifyTrackName))
         {
            System.out.println(allMusicTrackName + " does not equal " + spotifyTrackName);
            if (areEqual)
            {
               areEqual = false;
            }
         }
      }

      return areEqual;
   }

   private static boolean compareAlbumTitles(String allMusicTitle, String spotifyTitle)
   {
      System.out.println("Comparing " + allMusicTitle + " and " + spotifyTitle);
      boolean areEqual = StringUtil.compareStrings(allMusicTitle.trim(), spotifyTitle.trim());
      if (areEqual)
      {
         System.out.println("Titles are the same.");
      }
      else
      {
         System.out.println("Titles are not the same.");
      }
      return areEqual;
   }

   public static void setSpotifyIdsAndDurations(Release release, String spotifyAlbumId, List<SpotifyTrack> spotifyTracks)
   {
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      session.load(release, release.getId());
      release.setSpotifyid(spotifyAlbumId);
      for (int j = 0; j < release.getTracks().size() && j < spotifyTracks.size(); j++)
      {
         SpotifyTrack spotifyTrack = spotifyTracks.get(j);
         Track track = release.getTracks().get(j);
         track.setSpotifyId(spotifyTrack.getId());
         track.setDuration(spotifyTrack.getDuration_ms());
      }
      
      session.update(release);
      
      session.getTransaction().commit();
   }

   private static boolean setTrackDurations(Release release, SpotifyAlbum spotifyAlbum)
   {
      boolean success = true;
      Map<String, Integer> durations = new HashMap<String, Integer>();
      for (SpotifyTrack track : SpotifyUtil.getTracks(spotifyAlbum))
      {
         durations.put(track.getId(), track.getDuration_ms());
      }
      for (Track track : release.getTracks())
      {
         Integer d = durations.get(track.getSpotifyId());
         if (d != null)
         {
            track.setDuration(d);
         }
         else
         {
            success = false;
         }
      }
      return success;
   }

   private static Album convertAlbum(AllMusicAlbum allMusicAlbum)
   {

      Album album = new Album();
      album.setId(allMusicAlbum.getId());
      album.setUrl(allMusicAlbum.getUrl());
      album.setTitle(allMusicAlbum.getTitle());
      album.setRating(allMusicAlbum.getRating());
      album.setTimesSkipped(1);

      Set<Artist> artists = new HashSet<Artist>();

      for (AllMusicArtist allMusicArtist : allMusicAlbum.getArtists())
      {
         Artist artist = new Artist();
         artist.setId(allMusicArtist.getId());
         artist.setName(allMusicArtist.getName());
         artist.setUrl(allMusicArtist.getUrl());
         artists.add(artist);
      }

      album.setArtists(artists);

      List<Release> releases = new ArrayList<Release>();

      for (AllMusicRelease allMusicRelease : allMusicAlbum.getReleases())
      {

         Release release = new Release();
         release.setId(allMusicRelease.getId());
         release.setReleaseDate(allMusicRelease.getDate());
         release.setFormat(allMusicRelease.getFormat());
         release.setImageSrc(allMusicRelease.getImageSrc());
         release.setLabel(allMusicRelease.getLabel());
         release.setMain(allMusicRelease.isMain());
         release.setTitle(allMusicRelease.getTitle());

         List<Track> tracks = new ArrayList<Track>();

         for (AllMusicDisc disc : allMusicRelease.getDiscs())
         {
            for (AllMusicTrack track : disc.getTracks())
            {
               Track theTrack = new Track();
               TrackPK pk = new TrackPK();
               pk.setId(track.getId());
               pk.setMovement(track.getMovement());
               theTrack.setPk(pk);
               theTrack.setPick(track.isPick());
               theTrack.setDuration(track.getDuration());
               theTrack.setTitle(track.getTitle());
               theTrack.setSpotifyId(track.getSpotifyId());
               theTrack.setRelease(release);
               tracks.add(theTrack);
            }
         }
         release.setTracks(tracks);
         release.setAlbum(album);
         releases.add(release);
      }

      album.setReleases(releases);
      album.setImagesrc(allMusicAlbum.getImageSrc());

      Set<Genre> genres = new HashSet<Genre>();
      for (Map.Entry<String, String> entry : allMusicAlbum.getGenres().entrySet())
      {
         Genre genre = new Genre();
         genre.setId(entry.getKey());
         genre.setName(entry.getValue());
         genres.add(genre);
      }
      album.setGenres(genres);

      Set<Style> styles = new HashSet<Style>();
      for (Map.Entry<String, String> entry : allMusicAlbum.getStyles().entrySet())
      {
         Style style = new Style();
         style.setId(entry.getKey());
         style.setName(entry.getValue());
         styles.add(style);
      }
      album.setStyles(styles);

      Set<Mood> moods = new HashSet<Mood>();
      for (Map.Entry<String, String> entry : allMusicAlbum.getMoods().entrySet())
      {
         Mood mood = new Mood();
         mood.setId(entry.getKey());
         mood.setName(entry.getValue());
         moods.add(mood);
      }
      album.setMoods(moods);

      Set<Theme> themes = new HashSet<Theme>();
      for (Map.Entry<String, String> entry : allMusicAlbum.getThemes().entrySet())
      {
         Theme theme = new Theme();
         theme.setId(entry.getKey());
         theme.setName(entry.getValue());
         themes.add(theme);
      }
      album.setThemes(themes);

      return album;
   }

   public static void main(String[] args) throws IOException
   {
      System.out.println(AlbumManager.findAlbumFromURL("http://www.allmusic.com/album/daydream-nation-mw0000652888"));
   }

   public static void setProcessed(Album album)
   {
      Session session = HibernateUtil.getCurrentSession();
      session.beginTransaction();

      session.load(album, album.getId());
      
      album.setTimesSkipped(0);
      
      session.update(album);
      
      String delete = "delete Release where album_id = :albumid and spotifyid is null";
      int deletedEntities = session.createQuery( delete )
            .setString( "albumid", album.getId() )
            .executeUpdate();
      System.out.println("Deleted " + deletedEntities + " releases for " + album.getTitle() );
      session.getTransaction().commit();
   }
}
