package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.HavingFields;
import co.deepthought.quickscan.store.Result;
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
            final Result result = new Result("r-" + i, "a", null);
            this.addTags("rtag-", i, result);
            result.addField("field-1", 20);
            result.addField("field-2", i);
            result.addScore("score-1", false, i);
            result.addScore("score-2", (i % 4) == 0, i);

            for(int j = 0; j < i; j++) {
                final Document document = result.createDocument("d-" + i + "-" + j);
                this.addTags("dtag-", i, document);
                document.addField("subfield-" + (j%4), j);
                result.addScore("score-3", false, i);
            }

            this.mapper.inspect(result);
        }
    }

    @Test
    public void testFieldMapping() {
        final Set<String> expected = new HashSet<>();
        expected.add("field-1");
        expected.add("field-2");
        expected.add("subfield-0");
        expected.add("subfield-1");
        expected.add("subfield-2");
        expected.add("subfield-3");
        Assert.assertEquals(expected, this.mapper.getDistinctFields());
    }

    @Test
    public void testTagMapping() {
        final Set<String> tags = this.mapper.getDistinctTags();
        Assert.assertEquals(200, tags.size());
        Assert.assertTrue(tags.contains("rtag-0"));
        Assert.assertTrue(tags.contains("rtag-99"));
        Assert.assertTrue(tags.contains("dtag-0"));
        Assert.assertTrue(tags.contains("dtag-99"));
        Assert.assertFalse(tags.contains("fake"));
    }

    @Test
    public void testScoreMapping() {
        final Set<String> expected = new HashSet<>();
        expected.add("score-1");
        expected.add("score-2");
        expected.add("score-3");
        Assert.assertEquals(expected, this.mapper.getDistinctScores());

        final Collection<Double> score1s = this.mapper.getScores().get("score-1");
        Assert.assertEquals(100, score1s.size());
        final Collection<Double> score2s = this.mapper.getScores().get("score-2");
        Assert.assertEquals(75, score2s.size());
        final Collection<Double> score3s = this.mapper.getScores().get("score-3");
        Assert.assertEquals(4950, score3s.size()); // n * (n - 1) / 2
    }

    private void addTags(final String prefix, final int count, final HavingFields target) {
        for(int j = 0; j <= count; j++) {
            target.addTag(prefix + j);
        }
    }

}
