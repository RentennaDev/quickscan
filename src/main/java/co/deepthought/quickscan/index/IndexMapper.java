package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

/**
 * Inspects documents one-at-a-time to produce an IndexMap
 */
public class IndexMapper {

    final private Set<String> distinctFields;
    final private Set<String> distinctScores;
    final private Set<String> distinctTags;
    final private Multimap<String, Double> scores;

    public IndexMapper() {
        this.distinctFields = new HashSet<>();
        this.distinctScores = new HashSet<>();
        this.distinctTags = new HashSet<>();
        this.scores = ArrayListMultimap.create();
    }

    protected Set<String> getDistinctFields() {
        return this.distinctFields;
    }

    protected Set<String> getDistinctScores() {
        return this.distinctScores;
    }

    protected Set<String> getDistinctTags() {
        return this.distinctTags;
    }

    protected Multimap<String, Double> getScores() {
        return this.scores;
    }

    public void inspect(final Result result) {
        for(final Field field : result.getAllFields()) {
            this.distinctFields.add(field.getName());
        }

        for(final Tag tag : result.getAllTags()) {
            this.distinctTags.add(tag.getName());
        }

        for(final Score score: result.getAllScores()) {
            this.distinctScores.add(score.getName());
            if(!score.getPhantom()) {
                this.scores.put(score.getName(), score.getValue());
            }
        }
    }

    public IndexMap map() {
        return new IndexMap(
            this.distinctTags,
            this.distinctFields,
            this.distinctScores,
            this.scores
        );
    }

}