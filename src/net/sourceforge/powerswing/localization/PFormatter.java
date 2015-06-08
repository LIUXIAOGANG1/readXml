package net.sourceforge.powerswing.localization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PFormatter {

   private Pattern pattern;
   private Pattern subpattern;
    
   public PFormatter(){
       pattern = Pattern.compile(".*?\\|(.*?)\\|.*");
       subpattern = Pattern.compile("(.*?),(.*?),(.*?),(.*?)");
   }
   
   public String format(String theString, Object[] values){
       boolean done = false;
       while (!done){
           Matcher p = pattern.matcher(theString);
           if (p.matches()){
               Matcher s = subpattern.matcher(p.group(1));
               if (s.matches()){
	               int field = Integer.parseInt(s.group(1).trim());
	               String type = s.group(2).trim();
	               String ifEmpty = s.group(3).trim();
	               String ifNotEmpty = s.group(4).trim();
	               if (type.equals("cwformat") && values.length > field){
	                   if (values[field].toString().length() == 0){
	                       String toReplace = "\\|" + s.group(0) + "\\|";
	                       toReplace = toReplace.replaceAll("\\{", "\\\\\\{");
	                       toReplace = toReplace.replaceAll("\\}", "\\\\\\}");
	                       theString = theString.replaceFirst(toReplace, ifEmpty);
	                   }
	                   else{
	                       String toReplace = "\\|" + s.group(0) + "\\|";
	                       toReplace = toReplace.replaceAll("\\{", "\\\\\\{");
	                       toReplace = toReplace.replaceAll("\\}", "\\\\\\}");
	                       theString = theString.replaceFirst(toReplace, ifNotEmpty);
	                   }
	               }
	               else{
	                   done = true;
	               }
               }
               else{
                   done = true;
               }
           }
           else{
               done = true;
           }
       }
       return theString;
   }
   
   public static void main(String[] args) {
       PFormatter c = new PFormatter();
       System.out.println(c.format("abcd|1,cwformat,or {1},and {1}|eefgfksdhsfhafdfhaf|5,cwformat,or {5},and{5}|", new Object[]{" ", " ", " ", " ", " ", " "}));
   }
}
