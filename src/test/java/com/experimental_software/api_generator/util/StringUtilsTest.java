package com.experimental_software.api_generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://docs.brickhub.dev/brick-syntax#built-in-lambdas>Built-in Lambdas | docs.brickhub.dev</a>
 */
public class StringUtilsTest {

    @Test
    public void testPascalCaseTransformation() {
        assertEquals("HelloWorld", StringUtils.toPascalCase("HELLO_WORLD"));
        assertEquals("HelloWorld", StringUtils.toPascalCase("HelloWorld"));
        assertEquals("Iso8601Date", StringUtils.toPascalCase("Iso8601_date"));
        assertEquals("List<ReferenceRange<DvCount>>", StringUtils.toPascalCase("List<REFERENCE_RANGE<DV_COUNT>>"));
        assertEquals("Hash<FooBar,SpamEggs>", StringUtils.toPascalCase("Hash<FOO_BAR,SPAM_EGGS>"));
    }

    @Test
    public void testFailPascalCaseTransformation() {
        assertThrows(IllegalArgumentException.class, () -> StringUtils.toPascalCase("Hash<FOO_BAR,SPAM_EGGS"));
    }

    @Test
    public void testLowerCamelCaseTransformation() {
        assertEquals("helloWorld", StringUtils.toLowerCamelCase("hello_world"));
    }
}
