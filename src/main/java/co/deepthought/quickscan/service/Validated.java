package co.deepthought.quickscan.service;

public abstract class Validated {

    public abstract void validate() throws ServiceFailure;

    protected void validateNonNull(final Object value, final String name) throws ServiceFailure {
        if(value == null) {
            throw new ServiceFailure(name + " must be non-null");
        }
    }

}
