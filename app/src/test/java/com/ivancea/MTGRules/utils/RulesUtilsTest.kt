package com.ivancea.MTGRules.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.streams.toList

internal class RulesUtilsTest {
    @Test
    fun getRuleAndSubsections() {
        val rulesReader =
            this.javaClass.classLoader?.getResource("rules.txt")?.openStream()?.reader()!!
        val rules = RulesParser.loadRules(rulesReader)!!

        // Remove all rules recursively that end with a letter
        rules.forEach {
            it.subRules.forEach {
                it.subRules.removeIf {
                    it.title.last().isLetter()
                }
            }
        }

        rules.flatMap { RuleUtils.flatten(it).toList() }
            .filter { it.title[0].isLetter() || it.title.endsWith(".") }.forEach {
                val result = RuleUtils.getRuleAndSubsections(rules, it.title)
                val expected = RuleUtils.flatten(it).toList()

                assertEquals(expected, result)
            }
    }
}