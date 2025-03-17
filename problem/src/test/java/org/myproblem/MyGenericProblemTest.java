package org.myproblem;

import org.junit.jupiter.api.Test;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyGenericProblemTest {
    @Test
    void NotInstantiable() {
        assertThrows(IllegalAccessException.class, () -> {
            Class<?> reflectedClass = Class.forName("org.zalando.problem.GenericProblems");
            reflectedClass.getDeclaredConstructor().newInstance();
        });
    }
    //Used Reflection to test the private constructor which is a way to test private constructors which
    // are not accessible directly.
    //Copilot helped introduce and help me learn the concept of Reflection in Java.

    @Test
    void CreateProblemBuilderWithStatus() throws Exception {
        Status status = Status.BAD_REQUEST;
        Class<?> reflectedClass = Class.forName("org.zalando.problem.GenericProblems");

        Method createMethod = reflectedClass.getDeclaredMethod("create", StatusType.class);

        // Invoke the create method with the status parameter
        createMethod.setAccessible(true);
        ProblemBuilder builder = (ProblemBuilder) createMethod.invoke(null, status);

        // Verify the results
        assertEquals(status.getReasonPhrase(), builder.build().getTitle());
        assertEquals(status, builder.build().getStatus());
    }
}
