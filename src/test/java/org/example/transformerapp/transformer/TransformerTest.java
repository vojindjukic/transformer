package org.example.transformerapp.transformer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransformerTest {

    @Test
    void testUppercaseTransformer() {
        Transformer transformer = new UppercaseTransformer();
        String result = transformer.transform("hello");
        assertEquals("HELLO", result);
    }

    @Test
    void testLowercaseTransformer() {
        Transformer transformer = new LowercaseTransformer();
        String result = transformer.transform("HELLO");
        assertEquals("hello", result);
    }

    @Test
    void testRemoveTransformer() {
        Transformer transformer = new RemoveTransformer("[aeiou]");
        String result = transformer.transform("hello");
        assertEquals("hll", result);
    }

    @Test
    void testReplaceTransformer() {
        Transformer transformer = new ReplaceTransformer("hello", "hi");
        String result = transformer.transform("hello world");
        assertEquals("hi world", result);
    }
}
