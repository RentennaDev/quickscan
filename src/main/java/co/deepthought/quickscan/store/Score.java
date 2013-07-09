package co.deepthought.quickscan.store;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable()
public class Score {

    public static enum Valence {
        NEUTRAL, POSITIVE, NEGATIVE
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, index = true)
    private Document document;

    @DatabaseField(canBeNull = false, index = true)
    private String name;

    @DatabaseField(canBeNull = false, index = true)
    private Valence valence;

    @DatabaseField(canBeNull = false)
    private double value;

    private Score() {

    }

    public Score(final String name, final Valence valence, final double value) {
        this.name = name;
        this.valence = valence;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Valence getValence() {
        return this.valence;
    }

    public double getValue() {
        return this.value;
    }

}
