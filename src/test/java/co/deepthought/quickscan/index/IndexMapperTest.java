package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class IndexMapperTest {

    private IndexMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new IndexMapper();
        for(int i = 0; i < 100; i++) {
            final Document document = new Document("r-" + i, "a", null);
            this.addTags("rtag-", i, document);
            document.addField("field-1", 20);
            document.addField("field-2", i);
            document.addScore("score-1", false, i);
            document.addScore("score-2", (i % 4) == 0, i);

            this.mapper.inspect(document);
        }
    }

    @Test
    public void testFieldMapping() {
        final Set<String> expected = new HashSet<>();
        expected.add("field-1");
        expected.add("field-2");
        Assert.assertEquals(expected, this.mapper.getDistinctFields());
    }

    @Test
    public void testTagMapping() {
        final Set<String> tags = this.mapper.getDistinctTags();
        Assert.assertEquals(100, tags.size());
        Assert.assertTrue(tags.contains("rtag-0"));
        Assert.assertTrue(tags.contains("rtag-99"));
        Assert.assertFalse(tags.contains("fake"));
    }

    @Test
    public void testScoreMapping() {
        final Set<String> expected = new HashSet<>();
        expected.add("score-1");
        expected.add("score-2");
        Assert.assertEquals(expected, this.mapper.getDistinctScores());

        final Collection<Double> score1s = this.mapper.getScores().get("score-1");
        Assert.assertEquals(100, score1s.size());
        final Collection<Double> score2s = this.mapper.getScores().get("score-2");
        Assert.assertEquals(75, score2s.size());
    }

    private void addTags(final String prefix, final int count, final Document target) {
        for(int j = 0; j <= count; j++) {
            target.addTag(prefix + j);
        }
    }

}
