package co.deepthought.quickscan.store;

import com.sleepycat.persist.model.Persistent;

import java.util.*;

/**
 * The ORM class for documents being stored.
 */
@Persistent
public class Document extends HavingFields {

    private String id;

    private Document() {}

    public Document(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}