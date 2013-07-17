package co.deepthought.quickscan.store;

import com.sleepycat.persist.model.Persistent;

/**
 * The ORM class for fields being stored.
 */
@Persistent
public class Field {

    private String name;
    private double value;

    private Field() {}

    public Field(final String name, final double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public double getValue() {
        return this.value;
    }

}
