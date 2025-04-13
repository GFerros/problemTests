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
}
