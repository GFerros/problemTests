package org.myproblem;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


//Note that these tests rely on the ProblemBuilderTests to be passed. Also despite the use of
//

public class MyProblemTest {

    @Nested
    class DefaultValueTests {

        @Test
        public void MockValues() {
            Problem problem = mock(Problem.class);
            when(problem.getType()).thenReturn(URI.create("about:blank"));
            when(problem.getTitle()).thenReturn(null);
            when(problem.getStatus()).thenReturn(null);
            when(problem.getDetail()).thenReturn(null);
            when(problem.getInstance()).thenReturn(null);

            assertEquals("about:blank", problem.getType().toString());
            assertNull(problem.getTitle());
            assertNull(problem.getStatus());
            assertNull(problem.getDetail());
            assertNull(problem.getInstance());
        }

        @Test
        public void RealValues() {
            Problem problem = Problem.builder().build();
            assertEquals("about:blank", problem.getType().toString());
            assertNull(problem.getTitle());
            assertNull(problem.getStatus());
            assertNull(problem.getDetail());
        }
    }
    @Nested
    class ValueOfTests {

        @Test
        void withStatusOnly() {
            Problem problem = Problem.valueOf(Status.NOT_FOUND);

            assertEquals(404, Objects.requireNonNull(problem.getStatus()).getStatusCode());
            //Note that the title is set to the status's reason phrase automatically
            assertEquals("Not Found", problem.getTitle());
            assertNull(problem.getDetail());
            assertNull(problem.getInstance());
            assertEquals(Problem.DEFAULT_TYPE, problem.getType());
        }

        @Test
        void withStatusAndDetail() {
            Problem problem = Problem.valueOf(Status.NOT_FOUND, "Order 123");

            assertEquals(404, Objects.requireNonNull(problem.getStatus()).getStatusCode());
            assertEquals("Order 123", problem.getDetail());
            assertNull(problem.getInstance());
        }

        @Test
        void withStatusAndInstance() {
            URI instance = URI.create("https://example.org/orders/123");
            Problem problem = Problem.valueOf(Status.NOT_FOUND, instance);

            assertEquals(instance, problem.getInstance());
            assertNull(problem.getDetail());
        }

        @Test
        void withStatusDetailAndInstance() {
            URI instance = URI.create("https://example.org/orders/123");
            Problem problem = Problem.valueOf(Status.NOT_FOUND, "Order 123", instance);

            assertEquals("Order 123", problem.getDetail());
            assertEquals(instance, problem.getInstance());
        }
    }

    @Nested
    class ToStringTests {

        //Note that for the Problem.valueOf(StatusType) method, the title is set to the status's reason phrase
        //which is done automatically in Problem.valueOf to be compliant with RFC 7807 standards

        @Test
        void withStatus() {
            Problem problem = Problem.valueOf(Status.NOT_FOUND);
            String result = Problem.toString(problem);

            assertEquals("about:blank{404, Not Found}", result);
        }

        @Test
        void withStatusAndDetail() {
            Problem problem = Problem.valueOf(Status.NOT_FOUND, "Order 123");
            String result = Problem.toString(problem);

            assertEquals("about:blank{404, Not Found, Order 123}", result);
        }

        @Test
        void withStatusAndInstance() {
            URI instance = URI.create("https://example.org/");
            Problem problem = Problem.valueOf(Status.NOT_FOUND, instance);
            String result = Problem.toString(problem);

            assertEquals("about:blank{404, Not Found, instance=https://example.org/}", result);
        }

        @Test
        void withStatusDetailAndInstance() {
            URI instance = URI.create("https://example.org/orders/123");
            Problem problem = Problem.valueOf(Status.NOT_FOUND, "Order 123", instance);
            String result = Problem.toString(problem);

            assertEquals("about:blank{404, Not Found, Order 123, instance=https://example.org/orders/123}", result);
        }

        @Test
        void withCustomTypeAndParameters() {
            Problem problem = Problem.builder()
                    .withType(URI.create("https://example.org/error"))
                    .withStatus(Status.UNPROCESSABLE_ENTITY)
                    .withTitle("Validation Error")
                    .withDetail("Name is required")
                    .withInstance(URI.create("/users/123"))
                    .with("field", "name")
                    .with("code", "VALIDATION_001")
                    .build();

            String result = Problem.toString(problem);
            assertEquals(
                    "https://example.org/error{422, Validation Error, Name is required, instance=/users/123, field=name, code=VALIDATION_001}",
                    result
            );
        }

        @Test
        void omitsNullFields() {
            Problem problem = Problem.builder()
                    .withStatus(Status.BAD_REQUEST)
                    .build();

            //Note that .build does not automatically set the title to the status's reason phrase
            //which is done automatically in Problem.valueOf to be compliant with RFC 7807 standards
            String result = Problem.toString(problem);
            assertEquals("about:blank{400}", result);
        }
    }

}
