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

    @Nested
    class SpecialValueTests { //Test Case 8

        @ParameterizedTest
        @CsvSource({"type, custom", "title, custom", "status, custom", "detail, custom", "instance, custom"})
        public void ReservedKeysCantBeUsedTest(String key, String value) {
            ProblemBuilder builder = Problem.builder();
            assertThrows(IllegalArgumentException.class, () -> builder.with(key, value));
        }

        @Test
        void shouldHandleReservedParameterNames() {
            assertThrows(IllegalArgumentException.class, () ->
                    Problem.builder().with("type", "custom-type"));

            assertThrows(IllegalArgumentException.class, () ->
                    Problem.builder().with("title", "custom-title"));

            assertThrows(IllegalArgumentException.class, () ->
                    Problem.builder().with("status", "custom-status"));
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

    @Nested
    class BasicInputTests { //Test Case 9
        // Individual tests for each property
        @Test
        void shouldBuildURICorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals("https://example.org/error", problem.getType().toString());
        }

        @Test
        void shouldBuildTitleCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals("Error", problem.getTitle());
        }

        @Test
        void shouldBuildStatusCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals(400, Objects.requireNonNull(problem.getStatus()).getStatusCode());
        }

        @Test
        void shouldBuildInstanceCorrectly() {
            Problem problem = createTypicalProblem();
            assertEquals("https://example.com/", Objects.requireNonNull(problem.getInstance()).toString());
        }

        @Test
        void shouldBuildAddsParameters() {
            Problem problem = createTypicalProblem();
            assertEquals("1", problem.getParameters().get("add"));
        }

        @Test
        void shouldBuildWithNullParameter() {
            Problem problem = Problem.builder()
                    .withStatus(Status.BAD_REQUEST)
                    .with("nullParam", null)
                    .build();
            assertNull(problem.getParameters().get("nullParam"));
        }

        @Test
        void shouldBuildWithEmptyParameter() {
            Problem problem = Problem.builder()
                    .withStatus(Status.BAD_REQUEST)
                    .with("emptyParam", "")
                    .build();
            assertEquals("", problem.getParameters().get("emptyParam"));
        }
    }


}
