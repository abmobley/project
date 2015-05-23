package org.mobley.album.spotify;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class SpotifyLoginServer extends AbstractHandler
{

   private static final String SPOTIFY_AUTH_START = "https://accounts.spotify.com/authorize/?client_id=";
   private static final String SPOTIFY_CODE_RESPONSE_TYPE = "&response_type=code";
   private static final String SPOTIFY_REDIRECT_URI = "&redirect_uri=http://localhost:8888/callback";
   private static final String SPOTIFY_AUTH_CLIENT_ID = System.getProperty("spotify.auth.client.id");
   private static final String SPOTIFY_AUTH_CLIENT_SECRET = System.getProperty("spotify.auth.client.secret");
   private static Server server;

   private HttpClient httpClient;

   public SpotifyLoginServer() throws Exception
   {
      super();
      SslContextFactory sslContextFactory = new SslContextFactory();
      httpClient = new HttpClient(sslContextFactory);
      httpClient.start();
   }

   public static void startLoginServer() throws Exception
   {
      server = new Server(8888);
      server.setHandler(new SpotifyLoginServer());
      server.start();
      server.join();
   }

   public static void stopLoginServer() throws Exception {
      server.stop();
   }

   @Override
   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
   {
      if(request.getPathInfo().equals("/login")) {
         response.sendRedirect(SPOTIFY_AUTH_START + SPOTIFY_AUTH_CLIENT_ID + SPOTIFY_CODE_RESPONSE_TYPE + SPOTIFY_REDIRECT_URI);
      } else if(request.getPathInfo().equals("/callback")) {
         String code = request.getParameter("code");
         try
         {
            requestRefreshAndAccessCodes(code);
         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      } else {
         System.out.println(request.getPathInfo());
         System.out.println(request.getParameterMap());
      }

   }

   private void requestRefreshAndAccessCodes(String code) throws Exception  {
      System.out.println("Code="+code);
      org.eclipse.jetty.client.api.Request request = httpClient.POST("https://accounts.spotify.com/api/token")
            .param("grant_type", "authorization_code")
            .param("code", code)
            .param("redirect_uri", "http://localhost:8888/callback")
            .param("client_id", SPOTIFY_AUTH_CLIENT_ID)
            .param("client_secret", SPOTIFY_AUTH_CLIENT_SECRET);
      System.out.println(request.getMethod() + " " + request.getPath() + " " + request.getURI());
      ContentResponse response = request
         .send();

      System.out.println(response.getContentAsString());
   }


   public static void main(String[] args) throws Exception {
      startLoginServer();
   }

}
