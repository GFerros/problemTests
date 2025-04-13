package org.myproblem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Nested;
import org.zalando.problem.Status;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStatusTest {
    @ParameterizedTest
    @CsvSource({
            "100, Continue",
            "101, Switching Protocols",
            "102, Processing",
            "103, Checkpoint",
            "200, OK",
            "201, Created",
            "202, Accepted",
            "203, Non-Authoritative Information",
            "204, No Content",
            "205, Reset Content",
            "206, Partial Content",
            "207, Multi-Status",
            "208, Already Reported",
            "226, IM Used",
            "300, Multiple Choices",
            "301, Moved Permanently",
            "302, Found",
            "303, See Other",
            "304, Not Modified",
            "305, Use Proxy",
            "307, Temporary Redirect",
            "308, Permanent Redirect",
            "400, Bad Request",
            "401, Unauthorized",
            "402, Payment Required",
            "403, Forbidden",
            "404, Not Found",
            "405, Method Not Allowed",
            "406, Not Acceptable",
            "407, Proxy Authentication Required",
            "408, Request Timeout",
            "409, Conflict",
            "410, Gone",
            "411, Length Required",
            "412, Precondition Failed",
            "413, Request Entity Too Large",
            "414, Request-URI Too Long",
            "415, Unsupported Media Type",
            "416, Requested Range Not Satisfiable",
            "417, Expectation Failed",
            "418, I'm a teapot",
            "422, Unprocessable Entity",
            "423, Locked",
            "424, Failed Dependency",
            "426, Upgrade Required",
            "428, Precondition Required",
            "429, Too Many Requests",
            "431, Request Header Fields Too Large",
            "451, Unavailable For Legal Reasons",
            "500, Internal Server Error",
            "501, Not Implemented",
            "502, Bad Gateway",
            "503, Service Unavailable",
            "504, Gateway Timeout",
            "505, HTTP Version Not Supported",
            "506, Variant Also Negotiates",
            "507, Insufficient Storage",
            "508, Loop Detected",
            "509, Bandwidth Limit Exceeded",
            "510, Not Extended",
            "511, Network Authentication Required"
    })
    public void CorrectCodesTest(int code, String reason) throws NoSuchFieldException, IllegalAccessException {
        Class<Status> statusClass = Status.class;

        // Get the private static final STATUSES field
        Field statusesField = statusClass.getDeclaredField("STATUSES");
        statusesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, Status> statuses = (Map<Integer, Status>) statusesField.get(null);

        if (statuses.containsKey(code)) {
            Status status1 = statuses.get(code);
            assertEquals(status1.getReasonPhrase(), reason);
        }
        else {
            throw new IllegalArgumentException("There is no known status for this code (" + code + ").");
        }

    }

    @Test
    public void ToStringTest() {
        assertEquals("404 Not Found", Status.NOT_FOUND.toString());
        assertEquals("400 Bad Request", Status.BAD_REQUEST.toString());
    }

    @Nested
    class ValueOfTests {
        @Test
        public void ValidValues() {
            assertEquals("Not Found", Status.valueOf(404).getReasonPhrase());
            assertEquals(404, Status.valueOf(404).getStatusCode());
        }

        @Test
        public void InvalidValues() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Status.valueOf(111));
            assertEquals("There is no known status for this code (111).", thrown.getMessage());

            IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> Status.valueOf(999));
            assertEquals("There is no known status for this code (999).", thrown2.getMessage());
        }
    }

}
