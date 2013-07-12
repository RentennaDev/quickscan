package co.deepthought.quickscan.store;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

/**
 * The ORM class for tags being stored in the sqlite store.
 */
@DatabaseTable()
public class Tag {

    public static class Dao extends BaseDaoImpl<Tag, Integer> {
        public Dao(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource, Tag.class);
        }
    }

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
