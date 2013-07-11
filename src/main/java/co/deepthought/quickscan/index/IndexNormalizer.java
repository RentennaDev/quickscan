package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;

import java.util.ArrayList;
import java.util.List;

public class IndexNormalizer {

    private final IndexMapper indexMapper;

    private final List<String> resultIds;
    private final List<long[]> tags;
    private final List<double[]> fields;
    private final List<double[]> scores;

    public IndexNormalizer(final IndexMapper indexMapper) {
        this.indexMapper = indexMapper;
        this.resultIds = new ArrayList<String>();
        this.tags = new ArrayList<long[]>();
        this.fields = new ArrayList<double[]>();
        this.scores = new ArrayList<double[]>();
    }

    public int getSize() {
        return this.resultIds.size();
    }

    public void indexDocument(final Document document) {
        this.resultIds.add(document.getResultId());
        this.tags.add(this.indexMapper.normalizeTags(document.getTagNames()));
        this.fields.add(this.indexMapper.normalizeFields(document.getFieldValues(), Double.NaN));
        this.scores.add(this.indexMapper.normalizeScores(document.getScoreValues(), Double.NaN));
    }

    public IndexShard normalize() {
        final int size = this.resultIds.size();
        if(size > 0) {
            return new IndexShard(
                this.normalizeResultIds(),
                this.normalizeTags(),
                this.normalizeFields(),
                this.normalizeScores()
            );
        }
        else {
            return null;
        }
    }

    public String[] normalizeResultIds() {
        return this.resultIds.toArray(new String[this.getSize()]);
    }

    public double[][] normalizeFields() {
        return IndexNormalizer.transpose(this.fields.toArray(new double[this.getSize()][]));
    }

    public double[][] normalizeScores() {
        return IndexNormalizer.transpose(this.scores.toArray(new double[this.getSize()][]));
    }

    public long[][] normalizeTags() {
        return IndexNormalizer.transpose(this.tags.toArray(new long[this.getSize()][]));
    }

    public static double[][] transpose(final double[][] source) {
        // big time copy/pasta, for primitive performance
        final double[][] sink = new double[source[0].length][source.length];
        for(int i = 0; i < source.length; i++) {
            final double[] current = source[i];
            for(int j = 0; j < current.length; j++) {
                sink[j][i] = current[j];
            }
        }
        return sink;
    }

    public static long[][] transpose(final long[][] source) {
        // big time copy/pasta, for primitive performance
        final long[][] sink = new long[source[0].length][source.length];
        for(int i = 0; i < source.length; i++) {
            final long[] current = source[i];
            for(int j = 0; j < current.length; j++) {
                sink[j][i] = current[j];
            }
        }
        return sink;
    }

}
