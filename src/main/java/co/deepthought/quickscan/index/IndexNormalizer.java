package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexNormalizer {

    private final IndexMapper indexMapper;

    private final List<String> resultIds;
    private final List<long[]> tags;
    private final List<double[]> fields;
    private final List<double[]> neutralScores;
    private final List<double[]> negativeScores;
    private final List<double[]> positiveScores;

    public IndexNormalizer(final IndexMapper indexMapper) {
        this.indexMapper = indexMapper;
        this.resultIds = new ArrayList<String>();
        this.tags = new ArrayList<long[]>();
        this.fields = new ArrayList<double[]>();
        this.neutralScores = new ArrayList<double[]>();
        this.negativeScores = new ArrayList<double[]>();
        this.positiveScores = new ArrayList<double[]>();
    }

    public int getSize() {
        return this.resultIds.size();
    }

    public void indexDocument(final Document document) {
        resultIds.add(document.getResultId());
        tags.add(indexMapper.normalizeTags(document.getTagNames()));
        fields.add(indexMapper.normalizeFields(document.getFieldValues(), Double.NaN));
        this.indexScores(document, this.neutralScores, Score.Valence.NEUTRAL);
        this.indexScores(document, this.negativeScores, Score.Valence.NEGATIVE);
        this.indexScores(document, this.positiveScores, Score.Valence.POSITIVE);
    }

    public void indexScores(final Document document, final List<double[]> scores, final Score.Valence valence) {
        final Map<String, Double> scoreValues = document.getScoreValues(valence);
        final double[] normalizedScoes = this.indexMapper.normalizeScores(scoreValues, valence, Double.NaN);
        scores.add(normalizedScoes);
    }

    public IndexShard normalize() {
        final int size = this.resultIds.size();
        if(size > 0) {
            final double[][][] scores = this.normalizeScores();
            return new IndexShard(
                this.normalizeResultIds(),
                this.normalizeTags(),
                this.normalizeFields(),
                scores[0],
                scores[1],
                scores[2]
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

    public double[][][] normalizeScores() {
        final double[][][] scores = new double[3][][];
        final int size = this.getSize();
        scores[0] = IndexNormalizer.transpose(this.neutralScores.toArray(new double[size][]));
        scores[1] = IndexNormalizer.transpose(this.negativeScores.toArray(new double[size][]));
        scores[2] = IndexNormalizer.transpose(this.positiveScores.toArray(new double[size][]));
        return scores;
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
