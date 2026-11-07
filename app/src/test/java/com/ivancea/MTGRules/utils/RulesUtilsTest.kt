package com.ivancea.MTGRules.utils

import com.ivancea.MTGRules.services.RulesService
import com.ivancea.MTGRules.model.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.stream.Collectors

internal class RulesUtilsTest {
    @Test
    fun findRule() {
        val provision = Rule("100.1.", "Provision")
        val lettered = Rule("100.1a", "Lettered provision")
        val term = Rule("Creature", "Definition")
        val glossary = Rule("Glossary", "", mutableListOf(term))
        val rules = listOf(
            Rule("1.", "Section", mutableListOf(
                Rule("100.", "Heading", mutableListOf(provision, lettered)),
            )),
            glossary,
        )

        assertSame(provision, RuleUtils.findRule(rules, "100.1."))
        assertSame(provision, RuleUtils.findRule(rules, "100.1"))
        assertSame(lettered, RuleUtils.findRule(rules, "100.1a"))
        assertSame(term, RuleUtils.findRule(rules, "Creature"))
        assertSame(glossary, RuleUtils.findRule(rules, "Glossary"))
        // An exact glossary ID takes priority over a numeric ID with an optional dot.
        val numericTerm = Rule("100", "Exact glossary ID")
        glossary.subRules.add(numericTerm)
        assertSame(numericTerm, RuleUtils.findRule(rules, "100"))
        assertThrows(IllegalArgumentException::class.java) { RuleUtils.findRule(rules, " ") }
        assertThrows(NoSuchElementException::class.java) { RuleUtils.findRule(rules, "Unknown") }
        assertThrows(NoSuchElementException::class.java) { RuleUtils.findRule(rules, "999.1") }
        assertThrows(NoSuchElementException::class.java) { RuleUtils.findRule(emptyList(), "1") }
    }

    @Test
    fun getRuleAndSubsections() {
        val rulesText =
            this.javaClass.classLoader?.getResource("rules.txt")?.readText()!!
        val rules = RulesParser.loadRules(rulesText, RulesService.rulesSources[0])!!

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
