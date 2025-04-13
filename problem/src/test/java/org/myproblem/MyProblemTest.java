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

        @Nested
        class BoundaryTests { //Test Case 3

            @Test
            void shouldHandleEmptyDetail() { //minimum length detail
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "");
                assertEquals("", problem.getDetail());
            }

            @Test
            void shouldHandleOneCharDetail() { //minimum + 1 length detail
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "a");
                assertEquals("a", problem.getDetail());
            }

            @Test
            void shouldHandleDefaultDetail() { //default detail
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "about:blank");
                assertEquals("about:blank", problem.getDetail());
            }

            @Test
            void shouldHandleVeryLongDetail() { //maximum length detail
                String longDetail = "a".repeat(10000); // 10KB string
                Problem problem = Problem.valueOf(Status.NOT_FOUND, longDetail);
                assertEquals(longDetail, problem.getDetail());
            }

            @Test
            void shouldHandleEmptyURI() { //minimum length URI (instance)
                Problem problem = Problem.valueOf(Status.NOT_FOUND, URI.create(""));
                assertEquals(URI.create(""), problem.getInstance());
            }

            @Test
            void shouldHandleRootURI() { //minimum + 1 length URI (instance)
                Problem problem = Problem.valueOf(Status.NOT_FOUND, URI.create("/"));
                assertEquals(URI.create("/"), problem.getInstance());
            }

            @Test
            void shouldHandleDefaultURI() { //default URI (instance)
                Problem problem = Problem.valueOf(Status.NOT_FOUND, URI.create("about:blank"));
                assertEquals(URI.create("about:blank"), problem.getInstance());
            }

            @Test
            void shouldHandleVeryLongURI() { //maximum length URI (instance)
                String longPath = "/" + "a".repeat(1000);
                URI instance = URI.create("https://example.org" + longPath);
                Problem problem = Problem.valueOf(Status.NOT_FOUND, instance);
                assertEquals(instance, problem.getInstance());
            }
        }

        @Nested
        class EdgeCases { //Test Case 4
            @Test
            void shouldHandleUnicodeCharacters() {
                String unicodeDetail = "问题详情"; // Chinese characters
                Problem problem = Problem.valueOf(Status.NOT_FOUND, unicodeDetail);
                assertEquals(unicodeDetail, problem.getDetail());
            }

            @Test
            void shouldHandleSpecialCharactersInDetail() {
                String specialChars = "!@#$%^&*()_+-=[]{}|;:'\",.<>/?";
                Problem problem = Problem.valueOf(Status.NOT_FOUND, specialChars);
                assertEquals(specialChars, problem.getDetail());
            }

            @Test
            void shouldHandleWhitespaceOnlyDetail() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "   ");
                assertEquals("   ", problem.getDetail());
            }

            @Test
            void shouldHandleNullDetailWithInstance() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, null, URI.create("https://example.org"));
                assertNull(problem.getDetail());
                assertNotNull(problem.getInstance());
            }

            @Test
            void shouldHandleNullInstanceWithDetail() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "Some detail", null);
                assertNotNull(problem.getDetail());
                assertNull(problem.getInstance());
            }
        }
    }

    @Nested
    class ToStringTests { //Test Case 5-7

        //Note that for the Problem.valueOf(StatusType) method, the title is set to the status's reason phrase
        //which is done automatically in Problem.valueOf to be compliant with RFC 7807 standards
        @Nested
        class BasicInputTests { //Test Case 5
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

        @Nested
        class BoundaryTests { //Test Case 6
            @Test
            void shouldHandleEmptyDetail() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "");
                String result = Problem.toString(problem);
                assertEquals("about:blank{404, Not Found, }", result);
            }

            @Test
            void shouldHandleSingleCharDetail() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "a");
                String result = Problem.toString(problem);
                assertEquals("about:blank{404, Not Found, a}", result);
            }

            @Test
            void shouldHandleDefaultDetail() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, "about:blank");
                String result = Problem.toString(problem);
                assertEquals("about:blank{404, Not Found, about:blank}", result);
            }

            @Test
            void shouldHandleVeryLongDetail() {
                String longDetail = "a".repeat(10000);
                Problem problem = Problem.valueOf(Status.NOT_FOUND, longDetail);
                String result = Problem.toString(problem);
                assertTrue(result.contains(longDetail));
            }

            @Test
            void shouldHandleEmptyURI() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, URI.create(""));
                String result = Problem.toString(problem);
                assertEquals("about:blank{404, Not Found, instance=}", result);
            }

            @Test
            void shouldHandleRootURI() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, URI.create("/"));
                String result = Problem.toString(problem);
                assertEquals("about:blank{404, Not Found, instance=/}", result);
            }

            @Test
            void shouldHandleDefaultURI() {
                Problem problem = Problem.valueOf(Status.NOT_FOUND, URI.create("about:blank"));
                String result = Problem.toString(problem);
                assertEquals("about:blank{404, Not Found, instance=about:blank}", result);
            }

            @Test
            void shouldHandleVeryLongURI() {
                String longPath = "/" + "a".repeat(1000);
                URI instance = URI.create("https://example.org" + longPath);
                Problem problem = Problem.valueOf(Status.NOT_FOUND, instance);
                String result = Problem.toString(problem);
                assertTrue(result.contains(instance.toString()));
            }

            @Test
            void shouldHandleEmptyParameters() {
                Problem problem = Problem.builder()
                        .withStatus(Status.NOT_FOUND)
                        .build();
                String result = Problem.toString(problem);
                assertEquals("about:blank{404}", result);
            }

            @Test
            void shouldHandleSingleParameter() {
                Problem problem = Problem.builder()
                        .withStatus(Status.NOT_FOUND)
                        .with("key", "value")
                        .build();
                String result = Problem.toString(problem);
                assertTrue(result.contains("key=value"));
            }

            @Test
            void shouldHandleMultipleParameters() {
                Problem problem = Problem.builder()
                        .withStatus(Status.NOT_FOUND)
                        .with("key1", "value1")
                        .with("key2", "value2")
                        .with("key3", "value3")
                        .build();
                String result = Problem.toString(problem);
                assertTrue(result.contains("key1=value1"));
                assertTrue(result.contains("key2=value2"));
                assertTrue(result.contains("key3=value3"));
            }

            @Test
            void shouldHandleEmptyParameterValue() {
                Problem problem = Problem.builder()
                        .withStatus(Status.NOT_FOUND)
                        .with("key", "")
                        .build();
                String result = Problem.toString(problem);
                assertTrue(result.contains("key="));
            }

            @Test
            void shouldHandleLongParameterValue() {
                String longValue = "a".repeat(1000);
                Problem problem = Problem.builder()
                        .withStatus(Status.NOT_FOUND)
                        .with("key", longValue)
                        .build();
                String result = Problem.toString(problem);
                assertTrue(result.contains(longValue));
            }

        }
    }
}
