//import com.google.common.base.CharMatcher;
//import org.jsoup.Jsoup;
//import org.jsoup.safety.Safelist;
//
//
//public class tstparagraph {
//
//    public static void main(String[] args) {
//        String searchedWord="shaheen",res="";
//        int ind;
//
//
//        String s="<p>ismail ramadan <br>mokhtar <h1>shaheen</h1> osman</p>";
//        s=s.replaceAll("<[^>]*>", "");
//
//        String str = Jsoup.clean(s, Safelist.none().addTags("p","span","li","h1","h2","h3","h4","h5","h6"));
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


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class tstparagraph {

    public static String generateSnippet(String url, String searchTerm) throws Exception {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsContainingOwnText(searchTerm);
        String snippet = "";
        for (org.jsoup.nodes.Element element : elements) {
            String text = element.text();
            if (text.contains(searchTerm)) {
                snippet = text;
                break;
            }
        }
        return snippet;
    }

    public static void main(String[] args) throws Exception {
        String url = "https://www.tyrereviews.com";
        String searchTerm = "rate";
        String snippet = tstparagraph.generateSnippet(url, searchTerm);
        System.out.println(snippet);
    }

}
