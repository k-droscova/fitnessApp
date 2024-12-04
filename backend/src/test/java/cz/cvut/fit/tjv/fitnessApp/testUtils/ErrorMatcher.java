package cz.cvut.fit.tjv.fitnessApp.testUtils;

import org.springframework.test.web.servlet.ResultMatcher;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ErrorMatcher {

    private ErrorMatcher() {
        // Prevent instantiation since the methods are static
    }

    /**
     * Creates a ResultMatcher that matches the expected error message in the JSON response.
     *
     * @param expectedMessage The expected error message.
     * @return A ResultMatcher for verifying the error message.
     */
    public static ResultMatcher matchesErrorMessage(String expectedMessage) {
        return jsonPath("$.errorMessage", is(expectedMessage));
    }

    public static ResultMatcher containsErrorMessage(String expectedMessage) {
        return jsonPath("$.errorMessage", containsString(expectedMessage));
    }
}
