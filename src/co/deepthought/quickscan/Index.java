package co.deepthought.quickscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a precomputed index of documents
 */
public class Index {

    public final static int LONG_BITS = 64;
    public final static int MAX_TAGS = LONG_BITS * LONG_BITS;
    public final static int PAGE_SIZE = LONG_BITS * LONG_BITS;

    private final Map<String, Integer> fieldIndexes;
    private final Map<String, Integer> scoreIndexes;
    private final Map<String, Integer> tagIndexes;

    private final IndexPage[] pages;

    public Index(final List<Document> documents) {
        this.fieldIndexes = new HashMap<String, Integer>();
        this.scoreIndexes = new HashMap<String, Integer>();
        this.tagIndexes = new HashMap<String, Integer>();
        this.processIdentifiers(documents);

        final String[] indexIdentifiers = new String[documents.size()];
        final int[][] indexFields = new int[this.fieldIndexes.size()][documents.size()];
        final long[][] indexTags = new long[this.pageCount(tagIndexes.size(), Index.LONG_BITS)][documents.size()];
        // TODO: index scores
        this.indexDocuments(documents, indexIdentifiers, indexFields, indexTags);

        this.pages = new IndexPage[this.pageCount(documents.size(), Index.PAGE_SIZE)];
        for(int pageIndex = 0; pageIndex < this.pages.length; pageIndex++) {
            this.pages[pageIndex] = new IndexPage(pageIndex, indexIdentifiers, indexFields, indexTags);
        }
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
            final long[][] indexTags) {
        int documentCounter = 0;
        for(final Document document : documents) {
            indexIdentifiers[documentCounter] = document.identifier;

            final int[] normalizedFields = this.normalizeFields(document);
            for(int fieldIndex = 0; fieldIndex < normalizedFields.length; fieldIndex++) {
                indexFields[fieldIndex][documentCounter] = normalizedFields[fieldIndex];
            }

            final long[] normalziedTags = this.normalizeTags(document);
            for(int tagIndex = 0; tagIndex < normalziedTags.length; tagIndex++) {
                indexTags[tagIndex][documentCounter] = normalziedTags[tagIndex];
            }

            // TODO: normalize and copy scores

            documentCounter++;
        }
    }

    private int pageCount(final int numItems, final int numPerPage) {
        return (int) Math.ceil(numItems / numPerPage);
    }

    private void processIdentifiers(final List<Document> documents) {
        for(final Document document : documents) {
            this.addIdentifiersIfAbsent(document.fields.keySet(), this.fieldIndexes);
            this.addIdentifiersIfAbsent(document.scores.keySet(), this.scoreIndexes);
            this.addIdentifiersIfAbsent(document.tags, this.tagIndexes);
        }

        if(this.tagIndexes.size() >= Index.MAX_TAGS) {
            throw new TooManyTagsException();
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
            normalized[tagIndex / Index.LONG_BITS] |= (1 << (tagIndex % Index.LONG_BITS));
        }
        return normalized;
    }

}