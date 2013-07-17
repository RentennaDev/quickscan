package co.deepthought.quickscan.store;


import com.sleepycat.persist.model.Persistent;

/**
 * The ORM class for tags being stored.
 */
@Persistent
public class Tag {

    private String name;

    private Tag() {}

    public Tag(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}