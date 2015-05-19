package org.mobley.album;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.mobley.album.allmusic.AllMusicAlbum;
import org.mobley.album.allmusic.AllMusicDisc;
import org.mobley.album.allmusic.AllMusicTrack;
import org.mobley.album.allmusic.AllMusicUtil;
import org.mobley.album.spotify.SpotifyAlbum;
import org.mobley.album.spotify.SpotifyAlbumSearchResult;
import org.mobley.album.spotify.SpotifyTrack;
import org.mobley.album.spotify.SpotifyUtil;

public class AlbumComparator
{
   public static boolean compareSpotifyIds(AllMusicAlbum allMusicAlbum, SpotifyAlbum spotifyAlbum) {
	   boolean areEqual = allMusicAlbum.getSpotifyId().equals(spotifyAlbum.getId());
	   
	   if(areEqual) {
		   Set<String> allMusicTrackIds = new HashSet<String>();
		   Set<String> spotifyTrackIds = new HashSet<String>();
		   
		   for(AllMusicDisc disc : allMusicAlbum.getDiscs())
		      {
		    	  for(AllMusicTrack track : disc.getTracks()) {
		    		  allMusicTrackIds.add(track.getSpotifyId());
		    	  }
		      }
		   
		   for(SpotifyTrack track : SpotifyUtil.getTracks(spotifyAlbum)) {
			   spotifyTrackIds.add(track.getId());
		   }
		   
		   areEqual = allMusicTrackIds.equals(spotifyTrackIds);
	   }
	   
	   return areEqual;
   }
   
   public static boolean compare(AllMusicAlbum allMusicAlbum, SpotifyAlbum spotifyAlbum)
   {
	   System.out.println("spotify:album:" + spotifyAlbum.getId());
      boolean areEqual = compareAlbumTitles(allMusicAlbum.getTitle(), spotifyAlbum.getName());
      
      List<AllMusicTrack> allMusicTracks = new ArrayList<AllMusicTrack>();
      
      for(AllMusicDisc disc : allMusicAlbum.getDiscs())
      {
    	  allMusicTracks.addAll(disc.getTracks());
      }
      
      if(!compareTracks(allMusicTracks, SpotifyUtil.getTracks(spotifyAlbum)))
      {
    	  areEqual = false;
      }
      
      return areEqual;
   }
   
   private static boolean compareAlbumTitles(String allMusicTitle, String spotifyTitle)
   {
      System.out.println("Comparing " + allMusicTitle + " and " + spotifyTitle);
      boolean areEqual = allMusicTitle.trim().equalsIgnoreCase(spotifyTitle.trim());
      if(areEqual)
      {
         System.out.println("Titles are the same.");
      }
      else
      {
         System.out.println("Titles are not the same.");
      }
      return areEqual;
   }
   
   private static boolean compareTracks(List<AllMusicTrack> allMusicTracks, List<SpotifyTrack> spotifyTracks)
   {
      boolean areEqual = allMusicTracks.size() == spotifyTracks.size();
      
      if(areEqual)
      {
         System.out.println("Number of tracks are the same: " + allMusicTracks.size());
      }
      else
      {
         System.out.println("Number of tracks are not the same: " + allMusicTracks.size() + "," + spotifyTracks.size());
      }
      
      for(int i = 0 ; i < allMusicTracks.size() && i < spotifyTracks.size(); i++)
      {
    	  AllMusicTrack allMusicTrack = allMusicTracks.get(i);
    	  SpotifyTrack spotifyTrack = spotifyTracks.get(i);
    	  String allMusicTrackName = allMusicTrack.getTitle().trim();
    	  String spotifyTrackName = spotifyTrack.getName().trim();
    	  System.out.println(i + " " + allMusicTrackName + " " + spotifyTrackName);
    	  if(!allMusicTrackName.equalsIgnoreCase(spotifyTrackName))
    	  {
    		  if(areEqual)
    		  {
    			  areEqual = false;
    		  }
    	  }
      }
      
      return areEqual;
   }

   public static void main(String[] args) throws Exception
   {
	  Document albumDoc = AllMusicUtil.getDocument("http://www.allmusic.com/album/untitled-23-mw0000816073");
      AllMusicAlbum allMusicAlbum = AllMusicUtil.getAlbum(albumDoc);
      
      SpotifyAlbumSearchResult result  = SpotifyUtil.searchAlbum(allMusicAlbum.getTitle(), allMusicAlbum.getArtists().get(0).getName());
      SpotifyAlbum spotifyAlbum = SpotifyUtil.getAlbum(result.getAlbums().getItems()[0].getHref());
      
      compare(allMusicAlbum, spotifyAlbum);
   }

}
