package co.deepthought.quickscan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Example with main method to demonstrate features
 */
public class Driver {

    public static void main(String[] args) {
        long start, end;

        start = System.nanoTime();
        final Random generator = new Random();
        final List<Document> documents = new ArrayList<Document>();
        for(int i = 0; i < 8000; i++) {
            final Document document = new Document(Integer.toString(i));
            document.addField("value", generator.nextInt(1000));
            document.addField("value2", generator.nextInt(1000));
            documents.add(document);
        }
        end = System.nanoTime();
        System.out.println("Generate: " + (end - start));

        start = System.nanoTime();
        final Index index = new Index(documents);
        end = System.nanoTime();
        System.out.println("Index: " + (end - start));

        for(int i = 0; i < 20; i++) {
            final Query query = new Query();
            query.filterFieldMax("value", 500);
            query.filterFieldMax("value2", 500);
            start = System.nanoTime();
            index.scan(query);
            end = System.nanoTime();
            System.out.println("Scan: " + (end - start));
        }
    }

}