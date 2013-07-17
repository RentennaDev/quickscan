package co.deepthought.quickscan;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.*;
import com.sleepycat.persist.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tester {

    @Entity
    public static class Parent {
        @PrimaryKey
        public String name;
        @SecondaryKey(relate = Relationship.MANY_TO_ONE)
        public String shard;
        public int age;
        public Parent() {}
    }


    public static void main(String[] args) throws DatabaseException {
        final Random random = new Random();

        final EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(false);
        final String dbName = "/tmp/db-" + random.nextInt();
        final File file = new File(dbName);
        file.mkdir();

        System.out.println(dbName);
        final Environment env = new Environment(file, envConfig);

        final StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(false);
        final EntityStore store = new EntityStore(env, "Store", storeConfig);

        final PrimaryIndex<String, Parent> index = store.getPrimaryIndex(String.class, Parent.class);
        final SecondaryIndex<String, String, Parent> second = store.getSecondaryIndex(index, String.class, "shard");

        System.out.println("here i am rock me like herman caine");

        final Parent p1 = new Parent();
        p1.age = 12;
        p1.name = "Charlie";
        p1.shard = "cats";
        index.put(p1);

        final Parent p3 = new Parent();
        p3.age = 14;
        p3.name = "Charlie";
        p3.shard = "rats";
        index.put(p3);

        final Parent p5 = index.get("Charlie");
        System.out.println(p5.age);
    }

}
