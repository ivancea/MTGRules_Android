package com.ivancea.MTGRules.appfunctions

import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesSource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDate

internal class RulesQueriesTest {
    private val edition = "2026-11-06"
    private val rules = listOf(
        Rule("1.", "General", mutableListOf(
            Rule("100.", "Game concepts", mutableListOf(
                Rule("100.1.", "Combat damage happens during the damage step."),
                Rule("100.1a", "Damage during combat is not always combat damage."),
                Rule("100.2.", "A player draws a card.\nExample: Draw one card."),
            )),
        )),
        Rule("2.", "Parts"),
        Rule("3.", "Card Types"),
        Rule("4.", "Zones"),
        Rule("5.", "Turn Structure"),
        Rule("Glossary", "", mutableListOf(Rule("Ability", "A characteristic of an object."))),
    )

    @Test
    fun `root pagination uses exclusive end and document order`() {
        val page = RulesQueries.list(edition, rules, null, 2, 3)
        assertEquals(listOf("3.", "4.", "5."), page.rules.map { it.id })
        assertEquals(listOf("Card Types", "Zones", "Turn Structure"), page.rules.map { it.title })
        assertEquals(6, page.total)
        assertEquals(5, page.nextOffset)
        assertEquals(edition, page.rulesetId)
        val last = RulesQueries.list(edition, rules, null, page.nextOffset!!, 3)
        assertEquals(listOf("Glossary"), last.rules.map { it.id })
        assertNull(last.nextOffset)
    }

    @Test
    fun `listing is shallow and numeric dots are optional`() {
        val section = RulesQueries.list(edition, rules, "1", 0, 50)
        assertEquals(listOf(RuleSummary("100.", "Game concepts")), section.rules)
        val provisions = RulesQueries.list(edition, rules, "100.", 0, 50)
        assertEquals(listOf("100.1.", "100.1a", "100.2."), provisions.rules.map { it.id })
        assertTrue(provisions.rules.all { it.title.isEmpty() })
        assertTrue(RulesQueries.list(edition, rules, "100.1", 0, 50).rules.isEmpty())
    }

    @Test
    fun `glossary lists names and gets individual definitions`() {
        assertEquals(listOf(RuleSummary("Ability", "Ability")),
            RulesQueries.list(edition, rules, "Glossary", 0, 50).rules)
        assertEquals(RuleDetails("Ability", "Ability", "A characteristic of an object."),
            RulesQueries.get(edition, rules, "Ability").rule)
    }

    @Test
    fun `getting a node never includes descendants and keeps examples`() {
        assertEquals("General", RulesQueries.get(edition, rules, "1.").rule.description)
        val leaf = RulesQueries.get(edition, rules, "100.2").rule
        assertEquals("100.2.", leaf.id)
        assertEquals("", leaf.title)
        assertEquals("A player draws a card.\nExample: Draw one card.", leaf.description)
    }

    @Test
    fun `pagination handles bounds and integer overflow`() {
        assertNull(RulesQueries.list(edition, rules, null, 0, Int.MAX_VALUE).nextOffset)
        assertTrue(RulesQueries.list(edition, rules, null, Int.MAX_VALUE, 50).rules.isEmpty())
        assertTrue(RulesQueries.list(edition, emptyList(), null, 0, 50).rules.isEmpty())
        assertThrows(IllegalArgumentException::class.java) {
            RulesQueries.list(edition, rules, null, -1, 50)
        }
        for (limit in listOf(0, -1)) {
            assertThrows(IllegalArgumentException::class.java) {
                RulesQueries.list(edition, rules, null, 0, limit)
            }
        }
    }

    @Test
    fun `unknown IDs never fall back to another node`() {
        for (id in listOf("999", "Unknown term", "100.99")) {
            assertThrows(NoSuchElementException::class.java) {
                RulesQueries.get(edition, rules, id)
            }
            assertThrows(NoSuchElementException::class.java) {
                RulesQueries.list(edition, rules, id, 0, 50)
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            RulesQueries.get(edition, rules, "")
        }
    }

    @Test
    fun `search preserves phrases AND matching and tree order`() {
        assertEquals(listOf("100.1."),
            RulesQueries.search(edition, rules, "COMBAT damage step").rules.map { it.id })
        assertEquals(listOf("100.1.", "100.1a"),
            RulesQueries.search(edition, rules, "combat damage").rules.map { it.id })
        assertEquals(listOf("100.1.", "100.1a"),
            RulesQueries.search(edition, rules, "\"combat damage\"").rules.map { it.id })
        assertTrue(RulesQueries.search(edition, rules, "\"damage during\"").rules.any { it.id == "100.1a" })
        assertEquals(listOf("Ability"), RulesQueries.search(edition, rules, "characteristic").rules.map { it.id })
        assertEquals(11, RulesQueries.search(edition, rules, "").rules.size)
        assertTrue(RulesQueries.search(edition, rules, "nonexistent").rules.isEmpty())
    }

    @Test
    fun `search pagination preserves matches and handles bounds`() {
        val all = RulesQueries.search(edition, rules, "", limit = Int.MAX_VALUE)
        val first = RulesQueries.search(edition, rules, "", limit = 3)
        val second = RulesQueries.search(edition, rules, "", offset = 3, limit = 3)
        assertEquals(all.rules.take(3), first.rules)
        assertEquals(all.rules.drop(3).take(3), second.rules)
        assertEquals(11, first.total)
        assertEquals(3, first.nextOffset)
        assertEquals(6, second.nextOffset)
        val last = RulesQueries.search(edition, rules, "", offset = 10, limit = 3)
        assertEquals(1, last.rules.size)
        assertNull(last.nextOffset)
        val beyond = RulesQueries.search(edition, rules, "", offset = Int.MAX_VALUE)
        assertTrue(beyond.rules.isEmpty())
        assertEquals(11, beyond.total)
        assertNull(beyond.nextOffset)
        val noMatches = RulesQueries.search(edition, rules, "nonexistent")
        assertEquals(0, noMatches.total)
        assertNull(noMatches.nextOffset)
        for ((offset, limit) in listOf(-1 to 50, 0 to 0, 0 to -1)) {
            assertThrows(IllegalArgumentException::class.java) {
                RulesQueries.search(edition, rules, "", offset, limit)
            }
        }
        val many = List(60) { Rule("Term$it", "Definition") }
        val defaults = RulesQueries.search(edition, many, "")
        assertEquals(50, defaults.rules.size)
        assertEquals(60, defaults.total)
        assertEquals(50, defaults.nextOffset)
    }

    @Test
    fun `source selection defaults to latest and supports historical editions`() {
        val sources = listOf("2015-01-23", edition).map {
            RulesSource(URI("https://example.com/rules"), LocalDate.parse(it), Charsets.UTF_8)
        }
        assertEquals(sources.last(), RulesQueries.resolveSource(sources, null))
        assertEquals(sources.first(), RulesQueries.resolveSource(sources, "2015-01-23"))
        assertThrows(IllegalArgumentException::class.java) {
            RulesQueries.resolveSource(sources, "latest")
        }
        assertThrows(NoSuchElementException::class.java) {
            RulesQueries.resolveSource(sources, "2020-01-01")
        }
    }
}
