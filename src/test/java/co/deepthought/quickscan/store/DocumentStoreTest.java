package co.deepthought.quickscan.store;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DocumentStoreTest {

    private DocumentStore store;

    @Before
    public void setUp() throws SQLException {
        this.store = new DocumentStore(":memory:");
    }

    @Test
    public void testGetDistinctFields() throws SQLException {
        final Document document1 = this.store.createDocument("A", "B", "C");
        document1.addField("baby", 0.2);
        document1.addField("cats", 0.2);
        this.store.persistDocument(document1);

        final Document document2 = this.store.createDocument("C", "OP", "C");
        document2.addField("baby", 0.2);
        document2.addField("rats", 0.2);
        this.store.persistDocument(document2);

        final Document document3 = this.store.createDocument("EE", "LMN", "D");
        document3.addField("baby", 0.2);
        document3.addField("mats", 0.2);
        this.store.persistDocument(document3);

        final Set<String> expected = new HashSet<String>();
        expected.add("baby");
        expected.add("cats");
        expected.add("rats");

        assertEquals(expected, this.store.getDistinctFields("C"));
    }

    @Test
    public void testGetDistinctScores() throws SQLException {
        final Document document1 = this.store.createDocument("A", "B", "C");
        document1.addScore("baby", Score.Valence.NEUTRAL, 0.2);
        document1.addScore("cats", Score.Valence.NEUTRAL, 0.2);
        this.store.persistDocument(document1);

        final Document document2 = this.store.createDocument("C", "OP", "C");
        document2.addScore("baby", Score.Valence.NEUTRAL, 0.2);
        document2.addScore("fats", Score.Valence.NEUTRAL, 0.2);
        document2.addScore("rats", Score.Valence.POSITIVE, 0.2);
        document2.addScore("cats", Score.Valence.POSITIVE, 0.2);
        this.store.persistDocument(document2);

        final Document document3 = this.store.createDocument("EE", "LMN", "D");
        document3.addScore("baby", Score.Valence.NEUTRAL, 0.2);
        document3.addScore("mats", Score.Valence.NEUTRAL, 0.2);
        this.store.persistDocument(document3);

        final Set<String> expected = new HashSet<String>();
        expected.add("baby");
        expected.add("cats");
        expected.add("fats");

        assertEquals(expected, this.store.getDistinctScores("C", Score.Valence.NEUTRAL));
    }

    @Test
    public void testGetDistinctTags() throws SQLException {
        final Document document1 = this.store.createDocument("A", "B", "C");
        document1.addTag("baby");
        document1.addTag("cats");
        this.store.persistDocument(document1);

        final Document document2 = this.store.createDocument("C", "OP", "C");
        document2.addTag("baby");
        document2.addTag("rats");
        this.store.persistDocument(document2);

        final Document document3 = this.store.createDocument("EE", "LMN", "D");
        document3.addTag("baby");
        document3.addTag("mats");
        this.store.persistDocument(document3);

        final Set<String> expected = new HashSet<String>();
        expected.add("baby");
        expected.add("cats");
        expected.add("rats");

        assertEquals(expected, this.store.getDistinctTags("C"));
    }

}
