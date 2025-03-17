package org.myproblem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class MyThrowableProblemTest {

    // Real implementation for testing
    private static class TestThrowableProblem extends ThrowableProblem {
        TestThrowableProblem(ThrowableProblem cause) {
            super(cause);
        }
    }

    private ThrowableProblem problemWithCause;
    private ThrowableProblem rootProblem;

    @BeforeEach
    void setup() {
        rootProblem = Problem.valueOf(Status.BAD_REQUEST);
        problemWithCause = new TestThrowableProblem(rootProblem);
    }

    @Nested
    class GetMessageTests {

        @Test
        void CombineTitleAndDetail() {
            ThrowableProblem problem = Problem.builder()
                    .withTitle("Unauthorized")
                    .withDetail("Missing API key")
                    .build();

            assertEquals("Unauthorized: Missing API key", problem.getMessage());
        }

        @Test
        void ReturnTitleWhenDetailIsNull() {
            ThrowableProblem problem = Problem.builder()
                    .withTitle("Not Found")
                    .build();

            assertEquals("Not Found", problem.getMessage());
        }

        @Test
        void ReturnDetailWhenTitleIsNull() {
            ThrowableProblem problem = Problem.builder()
                    .withDetail("Item 123 missing")
                    .build();

            assertEquals("Item 123 missing", problem.getMessage());
        }

        @Test
        void ReturnEmptyStringWhenTitleAndDetailAreNull() {
            ThrowableProblem problem = Problem.builder().build();
            assertEquals("", problem.getMessage());
        }
    }

    @Nested
    class GetCauseTests {

        @Test
        void ReturnCorrectCause() {
            assertEquals(rootProblem, problemWithCause.getCause());
        }

        @Test
        void ReturnNullWhenNoCause() {
            ThrowableProblem problem = Problem.builder().build();
            assertNull(problem.getCause());
        }

        @Test
        void MaintainCauseType() {
            ThrowableProblem cause = Problem.valueOf(Status.INTERNAL_SERVER_ERROR);
            ThrowableProblem problem = Problem.builder()
                    .withCause(cause)
                    .build();

            assertInstanceOf(ThrowableProblem.class, problem.getCause());
        }
    }

    @Nested
    class ToStringTests {

        @Test
        void UseProblemToStringImplementation() {
            ThrowableProblem problem = Problem.builder()
                    .withType(URI.create("https://example.org/error"))
                    .withStatus(Status.NOT_FOUND)
                    .withDetail("User 123 not found")
                    .build();

            String expected = Problem.toString(problem);
            assertEquals(expected, problem.toString());
        }

    }

    @Nested
    class ConstructorTests {

        @Test
        void InitializeWithCause() {
            ThrowableProblem problem = new TestThrowableProblem(rootProblem);
            assertEquals(rootProblem, problem.getCause());
        }

        @Test
        void HandleNullCause() {
            ThrowableProblem problem = new TestThrowableProblem(null);
            assertNull(problem.getCause());
        }

        @Test
        void ProcessStackTrace() {
            ThrowableProblem problem = new TestThrowableProblem(null);
            assertTrue(problem.getStackTrace().length > 0,
                    "Should have processed stack trace");
        }
    }
}
