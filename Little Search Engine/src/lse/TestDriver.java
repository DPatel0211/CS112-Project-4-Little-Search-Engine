package lse;
import java.io.*;
import java.util.*;


public class TestDriver {
    public static void main(String[] args)
    throws FileNotFoundException {

        Scanner scan = new Scanner(System.in);
        LittleSearchEngine foo = new LittleSearchEngine();
        foo.makeIndex("docs.txt", "noisewords.txt");


        System.out.println("Searching for words 'town', 'bright'...");
        System.out.println();
        try {
            System.out.println(foo.top5search("town", "bright"));

        } catch (Exception e) {
            System.out.println("ERROR");
        }

        /*
        int count = 0;
        for (String key : foo.keywordsIndex.keySet()) {
            System.out.println(key + foo.keywordsIndex.get(key));
            count++;
        }
        System.out.println(count);
         */

        /*
        HashMap<String,Occurrence> tuh = foo.loadKeywordsFromDocument("WowCh1.txt");
        HashMap<String,Occurrence> bar = foo.loadKeywordsFromDocument("AliceCh1.txt");
        System.out.println("Keywords in WowCh1: " + tuh.size());
        System.out.println("Keywords in AliceCh1: " + bar.size());
        System.out.println("Total:" + (tuh.size() + bar.size()));
         */

       /*
       foo.mergeKeywords(bar);
       System.out.println(bar);
       System.out.println();
       System.out.println(foo.keywordsIndex);
       */
       scan.close();
    }
}
