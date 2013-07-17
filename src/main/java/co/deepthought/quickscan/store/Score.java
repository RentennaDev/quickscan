package co.deepthought.quickscan.store;

import com.sleepycat.persist.model.Persistent;

/**
 * The ORM class for scores being stored.
 */
@Persistent
public class Score {

    private String name;
    private boolean phantom;
    private double value;

    private Score() {}

    public Score(final String name, final boolean phantom, final double value) {
        this.name = name;
        this.phantom = phantom;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public boolean getPhantom() {
        return this.phantom;
    }

    public double getValue() {
        return this.value;
    }

}
