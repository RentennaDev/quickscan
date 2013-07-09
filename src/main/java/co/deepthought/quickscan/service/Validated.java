package co.deepthought.quickscan.service;

public abstract class Validated {

    public static class Failure extends Exception {

        private final String message;

        public Failure(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

    }

    public abstract void validate() throws Failure;

    protected void validateNonNull(final Object value, final String name) throws Failure {
        if(value == null) {
            throw new Failure(name + " must be non-null");
        }
    }

}
