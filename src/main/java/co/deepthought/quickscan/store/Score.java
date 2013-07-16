package co.deepthought.quickscan.store;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

/**
 * The ORM class for scores being stored in the sqlite store.
 */
@DatabaseTable()
public class Score {

    public static class Dao extends BaseDaoImpl<Score, Integer> {
        public Dao(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource, Score.class);
        }
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, index = true)
    private Document document;

    @DatabaseField(canBeNull = false, index = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private boolean phantom;

    @DatabaseField(canBeNull = false)
    private double value;

    private Score() {

    }

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
