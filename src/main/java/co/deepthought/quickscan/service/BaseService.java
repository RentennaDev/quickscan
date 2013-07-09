package co.deepthought.quickscan.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class BaseService<InputType extends Validated, OutputType> {

    public BaseService() {}

    public static class FailureOutput {
        public String error;
        public String status;
        public FailureOutput(final String error) {
            this.status = "failure";
            this.error = error;
        }
    }

    public String handleJson(final String inputJson) {
        final Gson gson = new Gson();
        final InputType inputObject;
        try {
            inputObject = this.validateInput(inputJson);
        }
        catch (final Validated.Failure failure) {
            final FailureOutput output = new FailureOutput(failure.getMessage());
            return gson.toJson(output);
        }

        final OutputType outputObject = this.handle(inputObject);
        return gson.toJson(outputObject);
    }

    public InputType validateInput(final String inputJson) throws Validated.Failure {
        // TODO: handle error conditions?
        // JsonSyntaxException
        // Failure
        final Gson gson = new Gson();
        try {
            final InputType inputObject = gson.fromJson(inputJson, this.getInputClass());
            inputObject.validate();
            return inputObject;
        }
        catch (final JsonSyntaxException failure) {
            throw new Validated.Failure(failure.getMessage());
        }
    }

    public abstract Class<InputType> getInputClass();
    public abstract OutputType handle(final InputType inputObject);

}