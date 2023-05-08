import javax.swing.text.Document;
import java.net.URL;
import java.util.*;
public class Ranker{
    Indexer ind;
    List<Document>pages;
    List<String>queries;
    public Ranker(Indexer index,List<Document>pg,List<String>words){
        ind=index;
        pages=pg;
        queries=words;
    }

    //utility functions
    public String gethost(String str){
        try{
            URL url=new URL(str);
            String ret=url.getHost();
            return ret;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //TF-IDF        term frequency(t,d)      *       inverse document frequency
    //              number of times the              log((1+n)/1+df(d,t))+1
    //              term t appears in the            n = # of documents
    //              document d                       df(d,t) = document frequency of the term t

    /*

    We need to rank the websites according to the query words
    Calculating the Tf-IDF would need the generation of a certain page score
    To generate the score we need to filter the words in the query and count the number and weight of each word in the page

     */
    public double page_score(Document doc){         //calculating the score for each document stand alone
        String url=gethost(String.valueOf(doc.getDefaultRootElement()));    //should return the url of the document
        for(int i=0;i<queries.size();i++){
            //String
        }
        return 0.0;
    }
}