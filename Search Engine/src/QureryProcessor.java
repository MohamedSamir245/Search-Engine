import java.io.IOException;
import java.util.Arrays;

public class QureryProcessor {
    public static void main(String[] args) throws IOException {
        //TODO
        //take the query and assign it to (Query) variable
        String Query ="ismail ramadan \\\"shaheen ,'Mokhtar' .\"osman?";
        String Phrase="",tempWords;
        String wordsOnly[];
        Query=Query.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\'", "");
        Query=Query.replaceAll("/|\\\\|", "");
        Query=Query.replaceAll("©|»|-|\\{|}|=", "");
        String words__phrase[]=Query.split("\"");
        if(Query.indexOf("\"")!=Query.lastIndexOf("\""))
        {
            if(Query.startsWith("\""))
            {
                Phrase=words__phrase[1];
                wordsOnly= words__phrase[2].split(" ");
            }
            else {
                Phrase=words__phrase[1];
                tempWords=words__phrase[0];
                tempWords+=words__phrase[2];
                wordsOnly= tempWords.split(" ");

            }
        }
        else
        {
            Query=Query.replaceAll("\"", "");
            wordsOnly=Query.split(" ");
        }
        for(int i=0;i<wordsOnly.length+1;i++)
        {
            if(i==wordsOnly.length )
            {
                if(Phrase.length()!=0)
                    System.out.println(Phrase);
                continue;
            }
            System.out.println(wordsOnly[i]);
        }
    }

}
