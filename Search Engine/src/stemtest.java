import opennlp.tools.stemmer.PorterStemmer;

public class stemtest {
    public static void main(String [] args){
        PorterStemmer stemmer=new PorterStemmer();
        String word="skills";
        String stemmed=stemmer.stem(word);
        System.out.println(stemmed);
    }


}
