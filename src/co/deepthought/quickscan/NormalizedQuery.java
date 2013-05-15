package co.deepthought.quickscan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scan an index against a particular query.
 */
public class NormalizedQuery {

    protected final List<TagFilter> conjunctiveTagFilters;
    protected final List<List<TagFilter>> disjunctiveTagFilters;
    protected final List<FieldFilter> maxFilters;
    protected final List<FieldFilter> minFilters;

    public NormalizedQuery(final Index index, final Query query) {
        this.maxFilters = this.normalizeFieldFilters(index, query.fieldMaxs);
        this.minFilters = this.normalizeFieldFilters(index, query.fieldMins);
        this.conjunctiveTagFilters = this.normalizeTagFilters(index, query.conjunctiveTags);
        this.disjunctiveTagFilters = new ArrayList<List<TagFilter>>();
        for(final Set<String> tags : query.disjunctiveTags) {
            this.disjunctiveTagFilters.add(this.normalizeTagFilters(index, tags));
        }
    }

    private List<FieldFilter> normalizeFieldFilters(final Index index, final Map<String, Integer> filters) {
        final List<FieldFilter> normalized = new ArrayList<FieldFilter>();
        for(final Map.Entry<String, Integer> filter : filters.entrySet()) {
            // TODO: if the field doesn't exist?
            normalized.add(new FieldFilter(index.fieldIndexes.get(filter.getKey()), filter.getValue()));
        }
        return normalized;
    }

    private List<TagFilter> normalizeTagFilters(final Index index, final Set<String> tags) {
        final List<TagFilter> normalized = new ArrayList<TagFilter>();
        final long[] masks = new long[index.pageCount(index.tagIndexes.size(), Index.LONG_BITS)];
        for(final String tag : tags) {
            if(index.tagIndexes.containsKey(tag)) {
                final int tagIndex = index.tagIndexes.get(tag);
                masks[tagIndex/Index.LONG_BITS] |= (1L << (tagIndex % Index.LONG_BITS));
            }
            else {
                // If the tag doesn't exist in the index, it'll never get matched.
                // Really, we should short-circuit, but this is a quick and painless way to ensure it
                // never gets satisfied by setting the highest bit (which is past Index.MAX_TAGS)
                masks[masks.length-1] |= (1L << (Index.LONG_BITS-1));
            }
        }
        for(int i = 0; i < masks.length; i++) {
            if(masks[i] != 0) {
                normalized.add(new TagFilter(i, masks[i]));
            }
        }
        return normalized;
    }
}
