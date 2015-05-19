package org.mobley.album.spotify;

public class SpotifyCopyright
{

   private String text;
   private String type;
   
   public String getText()
   {
      return text;
   }
   
   public String getType()
   {
      return type;
   }

   @Override
   public String toString()
   {
      return "SpotifyCopyright [text=" + text + ", type=" + type + "]";
   }
   
   
}
