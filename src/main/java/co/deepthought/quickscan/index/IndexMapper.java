package co.deepthought.quickscan.index;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.Field;
import co.deepthought.quickscan.store.Score;
import co.deepthought.quickscan.store.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * Inspects documents one-at-a-time to produce an IndexMap
 */
public class IndexMapper {

    final Set<String> distinctFields;
    final Set<String> distinctScores;
    final Set<String> distinctTags;
    final Multimap<String, Double> scores;

    public IndexMapper() {
        this.distinctFields = new HashSet<String>();
        this.distinctScores = new HashSet<String>();
        this.distinctTags = new HashSet<String>();
        this.scores = ArrayListMultimap.create();
    }

    public void inspect(final Document document) {
        for(final Field field : document.getFields()) {
            this.distinctFields.add(field.getName());
        }

        for(final Tag tag : document.getTags()) {
            this.distinctTags.add(tag.getName());
        }

        for(final Score score: document.getScores()) {
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
            this.distinctScores
        );
    }

}