package co.deepthought.quickscan.store;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable()
public class Tag {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, index = true)
    private Document document;

    @DatabaseField(canBeNull = false, index = true)
    private String name;

    private Tag() {

    }

    public Tag(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Tag(" + this.name + ") for " + this.document;
    }

}
