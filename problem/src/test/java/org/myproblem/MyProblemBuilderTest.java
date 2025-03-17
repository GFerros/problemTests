package org.myproblem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Nested;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.zalando.problem.Status.BAD_REQUEST;

public class MyProblemBuilderTest {

    @ParameterizedTest
    @CsvSource({"type, custom", "title, custom", "status, custom", "detail, custom", "instance, custom"})
    public void ReservedKeysCantBeUsedTest(String key, String value) {
        ProblemBuilder builder = Problem.builder();
        assertThrows(IllegalArgumentException.class, () -> builder.with(key, value));
    }

    @Nested
    class CreateTypicalProblemTests {

        // Shared setup for all tests
        private Problem createTypicalProblem() {
            return Problem.builder()
                    .withType(URI.create("https://example.org/error"))
                    .withTitle("Error")
                    .withStatus(BAD_REQUEST)
                    .withInstance(URI.create("https://example.com/"))
                    .with("add", "1")
                    .with("extra", null)
                    .build();
        }

        // Individual tests for each property
        @Test
        void builderSetsTypeCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals("https://example.org/error", problem.getType().toString());
        }

        @Test
        void builderSetsTitleCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals("Error", problem.getTitle());
        }

        @Test
        void builderSetsStatusCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals(400, Objects.requireNonNull(problem.getStatus()).getStatusCode());
        }

        @Test
        void builderSetsInstanceCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals("https://example.com/", Objects.requireNonNull(problem.getInstance()).toString());
        }

        @Test
        void builderAddsNonNullParameters() {
            Problem problem = createTypicalProblem();
            assertEquals("1", problem.getParameters().get("add"));
        }

        @Test
        void builderHandlesNullParameters() {
            Problem problem = createTypicalProblem();
            assertNull(problem.getParameters().get("extra"));
        }
    }

    @Test
    void BuiltProblemIsImmutableTest() {
        Problem problem = Problem.builder()
                .withTitle("Error")
                .withStatus(Status.BAD_REQUEST)
                .build();

        assertEquals("Error", problem.getTitle());
        assertEquals(400, Objects.requireNonNull(problem.getStatus()).getStatusCode());
        assertThrows(UnsupportedOperationException.class, () -> problem.getParameters().put("test", "value"));
    }
}
