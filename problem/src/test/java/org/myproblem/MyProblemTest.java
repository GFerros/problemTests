package org.myproblem;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


//Note that these tests rely on the ProblemBuilderTests to be passed. Also despite the use of
//

public class MyProblemTest {

    @Nested
    class DefaultValueTests { //Test Case 1

        @Test
        public void MockValues() {
            Problem problem = mock(Problem.class);
            when(problem.getType()).thenReturn(URI.create("about:blank"));
            when(problem.getTitle()).thenReturn(null);
            when(problem.getStatus()).thenReturn(null);
            when(problem.getDetail()).thenReturn(null);
            when(problem.getInstance()).thenReturn(null);
            when(problem.getParameters()).thenReturn(Collections.emptyMap());

            assertEquals("about:blank", problem.getType().toString());
            assertNull(problem.getTitle());
            assertNull(problem.getStatus());
            assertNull(problem.getDetail());
            assertNull(problem.getInstance());
            assertEquals(Collections.emptyMap(), problem.getParameters());

        }

        @Test
        public void RealValues() {
            Problem problem = Problem.builder().build();
            assertEquals("about:blank", problem.getType().toString());
            assertNull(problem.getTitle());
            assertNull(problem.getStatus());
            assertNull(problem.getDetail());
            assertNull(problem.getInstance());
            assertEquals(Collections.emptyMap(), problem.getParameters());
        }
    }

    @Nested
    class ValueOfTests { //Test Case 2-4

        @Nested
        class BasicInputTests { //Test Case 2

            @Test
            void withStatusOnly() { //valueOf with status only as parameter
                Problem problem = Problem.valueOf(Status.NOT_FOUND);

                assertEquals(404, Objects.requireNonNull(problem.getStatus()).getStatusCode());
                //Note that the title is set to the status's reason phrase automatically
                assertEquals("Not Found", problem.getTitle());
                assertNull(problem.getDetail());
                assertNull(problem.getInstance());
                assertEquals(Problem.DEFAULT_TYPE, problem.getType());
                assertEquals(Collections.emptyMap(), problem.getParameters());
            }

            @Test
            void withStatusAndDetail() { //valueOf with status and detail as parameters
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "Order 123");

                assertEquals(404, Objects.requireNonNull(problem.getStatus()).getStatusCode());
                assertEquals("Order 123", problem.getDetail());
                assertNull(problem.getInstance());
                assertEquals(Problem.DEFAULT_TYPE, problem.getType());
                assertEquals(Collections.emptyMap(), problem.getParameters());
            }

            @Test
            void withStatusAndInstance() {
                URI instance = URI.create("https://example.org/orders/123");
                Problem problem = Problem.valueOf(Status.NOT_FOUND, instance);

                assertEquals(404, Objects.requireNonNull(problem.getStatus()).getStatusCode());
                assertNull(problem.getDetail());
                assertEquals(instance, problem.getInstance());
                assertEquals(Problem.DEFAULT_TYPE, problem.getType());
                assertEquals(Collections.emptyMap(), problem.getParameters());
            }


            @Test
            void withStatusDetailAndInstance() {
                URI instance = URI.create("https://example.org/orders/123");
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "Order 123", instance);

                assertEquals(404, Objects.requireNonNull(problem.getStatus()).getStatusCode());
                assertEquals("Order 123", problem.getDetail());
                assertEquals(instance, problem.getInstance());
                assertEquals(Problem.DEFAULT_TYPE, problem.getType());
                assertEquals(Collections.emptyMap(), problem.getParameters());
            }


            @Nested
            class InvalidInputTests {

                @Test
                void shouldHandleInvalidURI() {
                    assertThrows(IllegalArgumentException.class, () ->
                            Problem.valueOf(Status.NOT_FOUND, URI.create("invalid uri")));
                }

                @Test
                void shouldHandleMalformedURI() {
                    assertThrows(IllegalArgumentException.class, () ->
                            Problem.valueOf(Status.NOT_FOUND, URI.create("http://example.org/{invalid}")));
                }


            }
        }
    }
}
