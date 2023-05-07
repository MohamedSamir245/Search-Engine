//import com.google.common.base.CharMatcher;
//import org.jsoup.Jsoup;
//
//
//public class tstparagraph {
//
//    public static void main(String[] args) {
//        String searchedWord="mokhtar",res="";
//        int ind;
//
//
//        String s="<p>ismail ramadan <br>mokhtar <h1>shaheen</h1> osman</p>";
//        s=s.replaceAll("<[^>]*>", "");
//
//        String str = Jsoup.clean(s, Whitelist.none().addTags("p","span","li","h1","h2","h3","h4","h5","h6"));
//        str=str.replaceAll("<[^>]*>","*");
//        String strArr[]= str.split("/*");
//        System.out.println(str);
//        for (String sss : strArr)
//        {
//            if(sss.contains(searchedWord)) //searching for mokhtar
//            {
//                ind=sss.indexOf(searchedWord);
//                res=sss.substring(ind,ind+searchedWord.length()-1);
//                break;
//            }
//
//        }
//        System.out.println(res);
//    }
//
//}
