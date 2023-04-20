package com.ivancea.MTGRules.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class RulesSearchUtilsTest {
    @TestFactory
    fun tokenize() = listOf(
        """abc""" to setOf("abc"), // Single word without quotes
        """"abc""" to setOf("abc"), // Single word with quotes
        """a b c""" to setOf("a", "b", "c"), // Multiple words without quotes
        """a "b c"""" to setOf("a", "b c"), // Multiple words with quotes
        """a"bc"""" to setOf("a", "bc"), // Multiple words with quotes without spaces
        """a \"b c\"""" to setOf("a", "\"b", "c\""), // Escaped quotes
        """a "b c""" to setOf("a", "b c")) // Unclosed quotes
        .map { (input, expected) ->
            DynamicTest.dynamicTest("when tokenizing '$input' then I get $expected") {
                val actual = RulesSearchUtils.tokenize(input)

                assertEquals(expected, actual)
            }
        }
}