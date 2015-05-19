package org.mobley.album.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;

public class SpotifyUtil
{

   private static final String SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search?q=";
   private static final String SPOTIFY_ARTIST_FILTER = "artist:";
   private static final String SPOTIFY_ALBUM_FILTER = "album:";
   private static final String SPOTITY_SPACE = "%20";
   private static final String SPOTIFY_ALBUM_TYPE = "&type=album";
   private static final String SPOTIFY_MARKET_FILTER = "&market=US";
   private static final String SPOTIFY_ALBUM_URI = "https://api.spotify.com/v1/albums/";
   public static final Gson GSON = new Gson();
   private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
   private static JsonFactory GSON_FACTORY = new GsonFactory();
   
   public static SpotifyAlbumSearchResult searchAlbum(String title, String artist) throws Exception
   {
      String query = SPOTIFY_SEARCH_URL + SPOTIFY_ALBUM_FILTER + 
            convertString(title) + SPOTITY_SPACE + SPOTIFY_ARTIST_FILTER + 
            convertString(artist) +  SPOTIFY_ALBUM_TYPE + SPOTIFY_MARKET_FILTER;

      SpotifyAlbumSearchResult result = null;
      
      
		HttpRequestFactory requestFactory =
				HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {

		          public void initialize(HttpRequest request) throws IOException {
		        	  request.setParser(new JsonObjectParser(GSON_FACTORY));
		        }
				});
		    // make request
		GenericUrl url = new GenericUrl(query);
		    HttpRequest request =
		        requestFactory.buildGetRequest(url);
		    request.setThrowExceptionOnExecuteError(true);
		    
		    HttpResponse response = request.execute();
		    if (response.isSuccessStatusCode()) {


		        BufferedReader in = new BufferedReader(
		        new InputStreamReader(response.getContent()));
		      result = GSON.fromJson(in, SpotifyAlbumSearchResult.class);
		    }

		      return result;
   }
   
   public static String getSpotifyAlbumURI(String spotifyid) {
	   return SPOTIFY_ALBUM_URI + spotifyid;
   }
   
	public static List<SpotifyTrack> getTracks(SpotifyAlbum album) {

		List<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>(album
				.getTracks().getTotal());
		getTracks(album.getTracks(), tracks);
		return tracks;
	}
   
   private static void getTracks(SpotifyTracksPagingObject pagingObject, List<SpotifyTrack> tracks) {
	   tracks.addAll(Arrays.asList(pagingObject.getItems()));
	   if(pagingObject.getNext() != null) {
		   try {
			SpotifyTracksPagingObject next = getSpotifyTracksPagingObject(pagingObject.getNext());
			   getTracks(next, tracks);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
   }
   
	public static SpotifyTracksPagingObject getSpotifyTracksPagingObject(
			String href) throws Exception {
		URL url = new URL(href);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));

		SpotifyTracksPagingObject pagingObject = GSON.fromJson(in,
				SpotifyTracksPagingObject.class);

		try {
			in.close();
		} catch (Exception e) {
		}

		return pagingObject;
	}
   
   public static SpotifyAlbum getAlbum(String href) throws Exception
   {
      URL url = new URL(href);
      BufferedReader in = new BufferedReader(
      new InputStreamReader(url.openStream()));

      SpotifyAlbum album = GSON.fromJson(in, SpotifyAlbum.class);
      
      try
      {
         in.close();
      }
      catch (Exception e)
      {
      }
      
      return album;
   }
   
   private static String convertString(String s) throws UnsupportedEncodingException 
   {
      return URLEncoder.encode(s.trim().toLowerCase(), "ISO-8859-1");
   }
   
   public static void main(String[] args) throws Exception
   {
      SpotifyAlbum album = getAlbum(getSpotifyAlbumURI("4H87tuNGBfs7opAEpYokit"));
      List<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>(album.getTracks().getTotal());
      getTracks(album.getTracks(),tracks);
      for(SpotifyTrack track : tracks) {
    	  System.out.println(track);
      }
      System.out.println(tracks.size());
   }

	public static boolean isAvailableInUS(String[] available_markets) {
		boolean b = false;
		if(available_markets != null && available_markets.length > 0) {
			b = Arrays.asList(available_markets).contains("US");
		}
		return b;
	}

}
