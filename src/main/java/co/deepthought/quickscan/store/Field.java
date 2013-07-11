package co.deepthought.quickscan.store;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

@DatabaseTable()
public class Field {

    public static class Dao extends BaseDaoImpl<Field, Integer> {
        public Dao(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource, Field.class);
        }
    }

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
