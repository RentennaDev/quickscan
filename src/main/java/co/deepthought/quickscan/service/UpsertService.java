package co.deepthought.quickscan.service;

import java.util.Map;

public class UpsertService
        extends BaseService<UpsertService.Input, UpsertService.Output> {

    public static class Input extends Validated {

        public static class Score {
            public int valence;
            public double value;
            public Score() {}
        }
        public String documentId;
        public String resultId;
        public String shardId;
        public String[] tags;
        public Map<String, Double> fields;
        public Map<String, Score> scores;
        public Input() {}

        @Override
        public void validate() throws Failure{
            this.validateNonNull(this.documentId, "documentId");
            this.validateNonNull(this.documentId, "resultId");
            this.validateNonNull(this.documentId, "shardId");
            for(final String tag : this.tags) {
                this.validateNonNull(tag, "tags[]");
            }
            for(final Double field : this.fields.values()) {
                this.validateNonNull(field, "fields[]");
            }
            for(final Score score : this.scores.values()) {
                this.validateNonNull(score, "scores[]");
            }
        }

    }

    public static class Output {
        public boolean success;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Output handle(final Input inputObject) {
        return null;
    }

}