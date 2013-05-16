package co.deepthought.quickscan;

import java.util.*;

/**
 * Holds a precomputed index of documents
 */
public class Index {

    public final static int LONG_BITS = 64;
    public final static int PAGE_SIZE = LONG_BITS * LONG_BITS;
    public final static int SCORE_BUCKETS = 256;

    protected final Map<String, Integer> fieldIndexes;
    protected final Map<String, Integer> scoreIndexes;
    protected final Map<String, Integer> tagIndexes;

    protected final IndexPage[] pages;

    public Index(final List<Document> documents) {
        this.fieldIndexes = new HashMap<String, Integer>();
        this.scoreIndexes = new HashMap<String, Integer>();
        this.tagIndexes = new HashMap<String, Integer>();
        this.processIdentifiers(documents);

        final String[] indexIdentifiers = new String[documents.size()];
        final int[][] indexFields = new int[this.fieldIndexes.size()][documents.size()];
        final long[][] indexTags = new long[this.pageCount(1+tagIndexes.size(), Index.LONG_BITS)][documents.size()];
        final int scoreCount = this.scoreIndexes.size();
        final double[] indexScores = new double[2*scoreCount*documents.size()];
        this.indexDocuments(documents, indexIdentifiers, indexFields, indexTags, indexScores, scoreCount);

        this.pages = new IndexPage[this.pageCount(documents.size(), Index.PAGE_SIZE)];
        for(int pageIndex = 0; pageIndex < this.pages.length; pageIndex++) {
            this.pages[pageIndex] = new IndexPage(
                pageIndex,
                indexIdentifiers,
                indexFields,
                indexTags,
                indexScores,
                scoreCount);
        }
    }

    public int pageCount(final int numItems, final int numPerPage) {
        return (int) Math.ceil(numItems / (double) numPerPage);
    }

    /**
     * Scan the index using the query and return up to limit results.
     * TODO: skipping?
     */
    public Collection<String> scan(final Query query, final int limit) {

        final NormalizedQuery normalizedQuery = new NormalizedQuery(this, query);

        // this solution would not work if we wanted to parallellize the algo, we'd need to create a bucket for each
        final List<String>[] buckets = (List<String>[]) new List[Index.SCORE_BUCKETS]; // I don't miss shit like this either
        for(int i = 0; i < Index.SCORE_BUCKETS; i++) {
            buckets[i] = new LinkedList<String>();
        }

        for(final IndexPage page : this.pages) {
            page.scan(normalizedQuery, buckets);
        }

        final Set<String> response = new LinkedHashSet<String>(); //LinkedHash because we want the best for each id
        for(int i = Index.SCORE_BUCKETS - 1; i >= 0; i--) {
            for(final String matchId : buckets[i]) {
                if(response.size() < limit) {
                    response.add(matchId);
                }
            }
        }

        return response;
    }

    private void addIdentifiersIfAbsent(final Iterable<String> names, final Map<String, Integer> nameMap) {
        for (final String name : names) {
            if(!nameMap.containsKey(name)) {
                nameMap.put(name, nameMap.size());
            }
        }
    }

    private void indexDocuments(
            final List<Document> documents,
            final String[] indexIdentifiers,
            final int[][] indexFields,
            final long[][] indexTags,
            final double[] indexScores,
            final int  scoreCount) {
        int documentCounter = 0;
        for(final Document document : documents) {
            indexIdentifiers[documentCounter] = document.identifier;

            final int[] normalizedFields = this.normalizeFields(document);
            for(int fieldIndex = 0; fieldIndex < normalizedFields.length; fieldIndex++) {
                indexFields[fieldIndex][documentCounter] = normalizedFields[fieldIndex];
            }

            final long[] normalizedTags = this.normalizeTags(document);
            for(int tagIndex = 0; tagIndex < normalizedTags.length; tagIndex++) {
                indexTags[tagIndex][documentCounter] = normalizedTags[tagIndex];
            }

            final int scoreStart = 2 * scoreCount * documentCounter;
            final double[] normalizedScores = this.normalizeScores(document, scoreCount);
            for(int scoreIndex = 0; scoreIndex < 2 * scoreCount; scoreIndex++) {
                indexScores[scoreStart + scoreIndex] = normalizedScores[scoreIndex];
            }

            documentCounter++;
        }
    }

    private void processIdentifiers(final List<Document> documents) {
        for(final Document document : documents) {
            this.addIdentifiersIfAbsent(document.fields.keySet(), this.fieldIndexes);
            this.addIdentifiersIfAbsent(document.scores.keySet(), this.scoreIndexes);
            this.addIdentifiersIfAbsent(document.tags, this.tagIndexes);
        }
    }

    private int[] normalizeFields(final Document document) {
        final int[] normalized = new int[this.fieldIndexes.size()];
        for(Map.Entry<String, Integer> fieldEntry : document.fields.entrySet()) {
            normalized[this.fieldIndexes.get(fieldEntry.getKey())] = fieldEntry.getValue();
        }
        return normalized;
    }

    private long[] normalizeTags(final Document document) {
        final long[] normalized = new long[this.pageCount(tagIndexes.size(), Index.LONG_BITS)];
        for(final String tag : document.tags) {
            final int tagIndex = tagIndexes.get(tag);
            normalized[tagIndex / Index.LONG_BITS] |= (1L << (tagIndex % Index.LONG_BITS));
        }
        return normalized;
    }

    private double[] normalizeScores(final Document document, final int scoreCount) {
        final double[] normalized = new double[2*scoreCount];
        for(Map.Entry<String, Double> scoreEntry : document.scores.entrySet()) {
            normalized[2*this.scoreIndexes.get(scoreEntry.getKey())] = scoreEntry.getValue();
        }
        for(Map.Entry<String, Double> scoreConfidenceEntry : document.scoreConfidences.entrySet()) {
            normalized[1+2*this.scoreIndexes.get(scoreConfidenceEntry.getKey())] = scoreConfidenceEntry.getValue();
        }
        return normalized;
    }

}