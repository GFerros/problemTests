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

    @Nested
    class BoundaryTests { //Test Case 10
        @Test
        void shouldHandleEmptyDetail() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail("")
                    .build();
            assertEquals("", problem.getDetail());
        }

        @Test
        void shouldHandleSingleCharDetail() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail("z")
                    .build();
            assertEquals("z", problem.getDetail());
        }

        @Test
        void shouldHandleDefaultDetail() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail("example")
                    .build();
            assertEquals("example", problem.getDetail());
        }

        @Test
        void shouldHandleVeryLongDetail() {
            String longDetail = "z".repeat(10000);
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail(longDetail)
                    .build();
            assertEquals(longDetail, problem.getDetail());
        }

        @Test
        void shouldHandleEmptyURI() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withInstance(URI.create(""))
                    .build();
            assertEquals("", Objects.requireNonNull(problem.getInstance()).toString());
        }

        @Test
        void shouldHandleRootURI() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withInstance(URI.create("/"))
                    .build();
            assertEquals("/", Objects.requireNonNull(problem.getInstance()).toString());
        }

        @Test
        void shouldHandleDefaultURI() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withInstance(URI.create("https://example.org"))
                    .build();
            assertEquals("https://example.org", Objects.requireNonNull(problem.getInstance()).toString());
        }

        @Test
        void shouldHandleVeryLongURI() {
            String longPath = "/" + "z".repeat(1000);
            URI instance = URI.create("https://example.org" + longPath);
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withInstance(instance)
                    .build();
            assertEquals(instance.toString(), Objects.requireNonNull(problem.getInstance()).toString());
        }

        @Test
        void shouldHandleEmptyParameters() { //minimum length parameters
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .build();
            assertTrue(problem.getParameters().isEmpty());
        }

        @Test
        void shouldHandleOneParameter() { //minimum + 1 length parameters
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .with("key", "value")
                    .build();
            assertEquals(1, problem.getParameters().size());
        }

        @Test
        void shouldHandleMultipleParameters() { //defaultlength parameters
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .with("key1", "value1")
                    .with("key2", "value2")
                    .build();
            assertEquals(2, problem.getParameters().size());
            assertEquals("value1", problem.getParameters().get("key1"));
            assertEquals("value2", problem.getParameters().get("key2"));
        }


        @Test
        void shouldHandleManyCustomParameters() { //maximum length parameters
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .with("custom1", "value1")
                    .with("custom2", "value2")
                    .with("custom3", "value3")
                    .with("custom4", "value4")
                    .with("custom5", "value5")
                    .build();

            assertEquals(5, problem.getParameters().size());
            assertEquals("value1", problem.getParameters().get("custom1"));
            assertEquals("value5", problem.getParameters().get("custom5"));
        }



        @Test
        void shouldHandleEmptyParameterValue() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .with("key", "")
                    .build();
            assertEquals("", problem.getParameters().get("key"));
        }

        @Test
        void shouldHandleLongParameterValue() {
            String longValue = "a".repeat(1000);
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .with("key", longValue)
                    .build();
            assertEquals(longValue, problem.getParameters().get("key"));
        }
    }

    @Nested
    class EdgeCases { //Test Case 11

        @Test
        void shouldHandleUTF8Detail() {
            String utf8Detail = "𝄞".repeat(5000);
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail(utf8Detail)
                    .build();
            assertEquals(utf8Detail, problem.getDetail());
        }

        @Test
        void shouldHandleUnicodeCharacters() {
            String unicodeDetail = "问题详情";
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail(unicodeDetail)
                    .build();
            assertEquals(unicodeDetail, problem.getDetail());
        }

        @Test
        void shouldHandleSpecialCharacters() {
            String specialChars = "!@#$%^&*()_+-=[]{}|;:'\",.<>/?";
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .withDetail(specialChars)
                    .build();
            assertEquals(specialChars, problem.getDetail());
        }

        @Test
        void shouldHandleNullParameters() {
            Problem problem = createTypicalProblem();
            assertNull(problem.getParameters().get("extra"));
        }

        @Test
        void shouldBuildWithNumericParameter() {
            Problem problem = Problem.builder()
                    .withStatus(Status.BAD_REQUEST)
                    .with("count", 42)
                    .build();
            assertEquals(42, problem.getParameters().get("count"));
        }

        @Test
        void shouldBuildWithBooleanParameter() {
            Problem problem = Problem.builder()
                    .withStatus(Status.BAD_REQUEST)
                    .with("isValid", true)
                    .build();
            assertEquals(true, problem.getParameters().get("isValid"));
        }

        @Test
        void shouldHandleDuplicateParameters() {
            Problem problem = Problem.builder()
                    .withStatus(Status.NOT_FOUND)
                    .with("key", "value1")
                    .with("key", "value1")
                    .build();
            assertEquals(1, problem.getParameters().size());
        }

        @Test
        void shouldHandleAllNullFields() {
            Problem problem = Problem.builder()
                    .withType(null)
                    .withTitle(null)
                    .withStatus(null)
                    .withDetail(null)
                    .withInstance(null)
                    .build();

            assertEquals("about:blank", problem.getType().toString());
            assertNull(problem.getTitle());
            assertNull(problem.getStatus());
            assertNull(problem.getDetail());
            assertNull(problem.getInstance());
            assertTrue(problem.getParameters().isEmpty());
        }
    }


}
