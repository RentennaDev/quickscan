package co.deepthought.quickscan.store;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable()
public class Field {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, index = true)
    private Document document;

    @DatabaseField(canBeNull = false, index = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private double value;

    private Field() {

    }

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
