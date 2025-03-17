package org.myproblem;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.zalando.problem.Problem.DEFAULT_TYPE;

//Note that the Getter Tests here are applicable to all the other types of Problems as
//.build(), which is used for every type of Problem, makes a DefaultProblem which extends AbstractThrowableProblem
//so the getters are the same for all types of Problems as well as the tests for all the Problems are
//an indirect test for DefaultProblem

public class MyAbstractThrowableProblemTopDownRealTest {

    // Concrete test implementation
    static class TestProblem extends AbstractThrowableProblem {
        TestProblem(@Nullable URI type,
                    @Nullable String title,
                    @Nullable StatusType status,
                    @Nullable String detail,
                    @Nullable URI instance,
                    @Nullable ThrowableProblem cause,
                    @Nullable Map<String, Object> parameters) {
            super(type, title, status, detail, instance, cause, parameters);
        }
    }

    private TestProblem problem;
    private final URI testType = URI.create("https://example.org/problem");
    private final StatusType testStatus = Status.BAD_REQUEST;
    private final URI testInstance = URI.create("/resources/123");
    private final ThrowableProblem testCause = new TestProblem(null, null, null, null, null, null, null);


    @Nested
    class ConstructorTests {

        @Test
        void HandleAllNullAndUseDefaultType() {
            problem = new TestProblem(null, null, null, null, null, null, null);

            assertEquals(DEFAULT_TYPE, problem.getType());
            assertNull(problem.getTitle());
            assertNull(problem.getStatus());
            assertNull(problem.getDetail());
            assertNull(problem.getInstance());
            assertTrue(problem.getParameters().isEmpty());
        }

        @Test
        void SetAllFields() {
            problem = new TestProblem(
                    testType,
                    "Validation Error",
                    testStatus,
                    "Missing required field",
                    testInstance,
                    testCause,
                    Map.of("field", "email")
            );

            assertEquals(testType, problem.getType());
            assertEquals("Validation Error", problem.getTitle());
            assertEquals(400, Objects.requireNonNull(problem.getStatus()).getStatusCode());
            assertEquals("Missing required field", problem.getDetail());
            assertEquals(testInstance, problem.getInstance());
            assertEquals("email", problem.getParameters().get("field"));
            assertEquals(testCause, problem.getCause());
        }
    }

    @Nested
    class GetterTests {

        @BeforeEach
        void setup() {
            problem = new TestProblem(
                    testType,
                    "Test Title",
                    testStatus,
                    "Test Detail",
                    testInstance,
                    testCause,
                    Map.of("key", "value")
            );
        }

        @Test
        void ReturnCorrectType() {
            assertEquals(testType, problem.getType());
        }

        @Test
        void ReturnCorrectTitle() {
            assertEquals("Test Title", problem.getTitle());
        }

        @Test
        void ReturnCorrectStatus() {
            assertEquals(400, Objects.requireNonNull(problem.getStatus()).getStatusCode());
        }

        @Test
        void ReturnCorrectDetail() {
            assertEquals("Test Detail", problem.getDetail());
        }

        @Test
        void ReturnCorrectInstance() {
            assertEquals(testInstance, problem.getInstance());
        }

        @Test
        void ReturnUnmodifiableParameters() {
            assertThrows(UnsupportedOperationException.class, () ->
                    problem.getParameters().put("newKey", "value")
            );
        }
    }

    @Nested
    class ParameterTests {

        @Test
        void InitializeEmptyParametersWhenNull() {
            problem = new TestProblem(null, null, null, null, null, null, null);
            assertEquals(Collections.emptyMap(), problem.getParameters());
        }

        @Test
        void SetProvidedParameters() {
            Map<String, Object> params = new java.util.LinkedHashMap<>();
            params.put("page", 2);
            params.put("size", 20);

            problem = new TestProblem(null, null, null, null, null, null, params);

            assertEquals(2, problem.getParameters().get("page"));
            assertEquals(20, problem.getParameters().get("size"));

        }

        @Test
        void AllowAddingParametersViaSetMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            problem = new TestProblem(null, null, null, null, null, null, null);

            // Use reflection to access the `set` method
            Method setMethod = AbstractThrowableProblem.class
                    .getDeclaredMethod("set", String.class, Object.class);
            setMethod.setAccessible(true); // Bypass access checks
            setMethod.invoke(problem, "key", "value");

            assertEquals("value", problem.getParameters().get("key"));
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void PreserveParameterOrder() {
            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("first", 1);
            params.put("second", 2);

            problem = new TestProblem(null, null, null, null, null, null, params);

            Object[] keys = problem.getParameters().keySet().toArray();
            assertArrayEquals(new String[]{"first", "second"}, keys);
        }
    }
}
