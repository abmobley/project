package org.mobley.album.util;

import java.text.Collator;

public class StringUtil
{

   private static final Collator COLLATOR = Collator.getInstance();
   
   static
   {
      COLLATOR.setStrength(Collator.PRIMARY);
      COLLATOR.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
   }
   
   public static boolean compareStrings(String s1, String s2)
   {
      return COLLATOR.equals(s1, s2);
   }
   
   public static void main(String[] args)
   {
      System.out.println(StringUtil.compareStrings("José González","Jose Gonzalez"));
      System.out.println(StringUtil.compareStrings("José gonzález","jose Gonzalez"));
   }
   
}
