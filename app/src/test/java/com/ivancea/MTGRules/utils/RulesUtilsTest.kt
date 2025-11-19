package com.ivancea.MTGRules.utils

import com.ivancea.MTGRules.services.RulesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.stream.Collectors

internal class RulesUtilsTest {
    @Test
    fun getRuleAndSubsections() {
        val rulesReader =
            this.javaClass.classLoader?.getResource("rules.txt")?.openStream()?.reader()!!
        val rules = RulesParser.loadRules(rulesReader, RulesService.rulesSources[0])!!

        // Remove all rules recursively that end with a letter
        rules.forEach {
            it.subRules.forEach {
                it.subRules.removeIf {
                    it.title.last().isLetter()
                }
            }
        }

        rules.flatMap { RuleUtils.flatten(it).collect(Collectors.toList()) }
            .filter { it.title[0].isLetter() || it.title.endsWith(".") }.forEach {
                val result = RuleUtils.getRuleAndSubsections(rules, it.title)

                assertEquals(listOf(it), result)
            }
    }
}