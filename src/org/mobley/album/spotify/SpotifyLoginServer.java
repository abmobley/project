package org.mobley.album.spotify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.mobley.album.data.Track;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

public class SpotifyLoginServer {

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JsonFactory GSON_FACTORY = new GsonFactory();
	private static final String TOKEN_SERVER_URL = "https://accounts.spotify.com/api/token";
	private static final String AUTHORIZATION_SERVER_URL =
		      "https://accounts.spotify.com/authorize";
	private static final String CLIENT_ID = "a0f2b0a7c6dc4afcb2fd446b678546cf";
	private static final String CLIENT_SECRET_KEY = "66830c479a864025a72face55827f806";
	private static final String[] SCOPES = {"user-read-private","user-read-email","playlist-read-private","playlist-modify-public","playlist-modify-private"};
	
	
	private static AuthorizationCodeFlow getAuthorizationCodeFlow() {
		AccessMethod accessMethod = BearerToken.authorizationHeaderAccessMethod();
		GenericUrl tokenServerUrl = new GenericUrl(TOKEN_SERVER_URL);
		HttpExecuteInterceptor clientAuthentication = new ClientParametersAuthentication(CLIENT_ID,CLIENT_SECRET_KEY);
		return new AuthorizationCodeFlow.
				Builder(accessMethod, HTTP_TRANSPORT, GSON_FACTORY, tokenServerUrl, clientAuthentication, 
						CLIENT_ID, AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList(SCOPES)).build();
	}
	
	private static LocalServerReceiver getVerificationCodeReceiver() {
		return new LocalServerReceiver.Builder().setHost(
		        "localhost").setPort(8888).build();
	}
	
	public static Credential authorize() throws IOException {
		AuthorizationCodeFlow flow = getAuthorizationCodeFlow();
		LocalServerReceiver receiver = getVerificationCodeReceiver();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("abmobley");
	}

	public static void replacePlaylist(List<Track> tracks, String playlistid, final Credential credential) throws Exception {
		HttpRequestFactory requestFactory =
				HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {

		          public void initialize(HttpRequest request) throws IOException {
		            credential.initialize(request);
		        }
				});
		    // make requestspotify:user:abmobley:playlist:4o6miyAYi9K03xpoKn5qLA
		GenericUrl url = new GenericUrl("https://api.spotify.com/v1/users/abmobley/playlists/" + playlistid + "/tracks");
		String uris = getTracksUris(tracks);
		System.out.println("Setting uris: " + uris);
		url.set("uris", uris);
		    HttpRequest request =
		        requestFactory.buildPutRequest(url, null);
		    request.setThrowExceptionOnExecuteError(false);
		    HttpResponse response = request.execute();
		    System.out.println(response.getStatusCode() + " " + response.getStatusMessage());
	}
	
	private static String getTracksUris(List<Track> tracks) {
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < tracks.size(); i++) {
			if(i > 0) b.append(',');
			b.append("spotify:track:").append(tracks.get(i).getSpotifyid());
		}
		return b.toString();
	}
}
