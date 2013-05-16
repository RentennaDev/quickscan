package co.deepthought.quickscan;

import org.json.simple.JSONValue;

import java.io.*;
import java.util.*;

/**
 * Example with main method to demonstrate features
 */
public class Driver {

    public static void main(String[] args) {
        final Random generator = new Random();

        long start, end;
        start = System.nanoTime();
        final Map<String, Map<String, Object>> data = Driver.readData();
        end = System.nanoTime();
        System.out.println("Parsed JSON in: " + (end-start));
        System.out.println("Number Properties: " + data.size());

        start = System.nanoTime();
        final List<Document> documents = Driver.parseDocuments(data.values());
        end = System.nanoTime();
        System.out.println("Produced documents in: " + (end-start));
        System.out.println("Number Listings: " + documents.size());

        start = System.nanoTime();
        final Index index = new Index(documents);
        end = System.nanoTime();
        System.out.println("Indexed in: " + (end-start));

        final Query query = new Query();
        query.filterFieldRange("price", 1500, 2500);
        query.filterFieldMin("bedCount", generator.nextInt(3));
        query.filterTagsAll("amenity:doorman", "amenity:elevator");
        query.filterTagsAny("area:flatiron", "area:harlem");
        query.setPreference("age", 1);
        query.setPreference("score:1", 0.2);
        query.setPreference("score:2", 0.2);
        query.setPreference("score:3", 0.2);
        query.setPreference("score:4", 0.2);
        query.setPreference("score:5", 0.2);

        // Let's warm up hotspot
        for(int trial = 0; trial < 100; trial++) {
            index.scan(query, 100);
        }

        long sum = 0;
        for(int trial = 0; trial < 100; trial++) {
            start = System.nanoTime();
            Collection<String> result = index.scan(query, 100);
            end = System.nanoTime();
            sum += (end-start);
        }
        System.out.println("Full query:" + sum/100);

//        Driver.printDocuments(data, result);
    }

    private static void printDocuments(final Map<String, Map<String, Object>> data, final Collection<String> ids) {
        for(final String id : ids) {
            System.out.println(data.get(id));
        }
    }

    private static Map<String, Map<String, Object>> readData() {
        // Don't fucking miss googling "How to read a file in Java"
        try {
            final Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>();
            final FileInputStream fstream = new FileInputStream("realData.jl");
            final DataInputStream inputStream = new DataInputStream(fstream);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                final Map<String, Object> property = (Map<String, Object>) JSONValue.parse(line);
                final List<String> areas = (List<String>) property.get("areas");
                final List<?> listings = (List<?>) property.get("listings");
                // limit to nyc for now!
                if(areas.contains("nyc") && !listings.isEmpty()) {
                    data.put((String)property.get("id"), property);
                }
            }
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Document> parseDocuments(final Collection<Map<String, Object>> properties) {
        final int now = (int)(System.currentTimeMillis()/1000);
        final List<Document> documents = new ArrayList<Document>();
        for(final Map<String, Object> property : properties) {
            final String id = (String) property.get("id");
            final double latitude = (Double) property.get("latitude");
            final double longitude = (Double) property.get("longitude");
            final List<String> areas = (List<String>) property.get("areas");
            final List<String> amenities = (List<String>) property.get("amenities");
            final List<List<Object>> scoreComponents = (List<List<Object>>) property.get("scoreComponents");
            final List<Map<String, Object>> listings = (List<Map<String, Object>>) property.get("listings");


            for(final Map<String, Object> listing : listings) {
                final Document document = new Document(id);

                //numerical fields
                document.addField("latitude", (int)(100000 * latitude));
                document.addField("longitude", (int)(100000 * longitude));
                final long bedCount = (Long)(listing.get("bedCount"));
                document.addField("bedCount", (int)bedCount);
                final long price = (Long)(listing.get("price"));
                document.addField("price", (int)price);

                // tags
                for(final String area : areas) {
                    document.addTag("area:" + area);
                }
                for(final String amenity : amenities) {
                    document.addTag("amenity:" + amenity);
                }

                // scores
                final double timeDiff = now - (Long)(listing.get("openDate"));
                document.addScore("age", 1. / (Math.floor(timeDiff / 60. / 60. / 24.)), 1.);
                for(final List<Object> scoreComponent : scoreComponents) {
                    //OMFG
                    double score;
                    final Object scoreObject = scoreComponent.get(1);
                    if(scoreObject.getClass() == Long.class) {
                        score = (double)(long)(Long)scoreObject;
                    }
                    else {
                        score = (Double)scoreObject;
                    }

                    double confidence;
                    final Object confidenceObject = scoreComponent.get(2);
                    if(confidenceObject.getClass() == Long.class) {
                        confidence = (double)(long)(Long)confidenceObject;
                    }
                    else {
                        confidence = (Double)confidenceObject;
                    }

                    document.addScore(
                        "score:" + scoreComponent.get(0),
                        score/100,
                        confidence
                    );
                }

                documents.add(document);
            }
        }
        return documents;
    }

}