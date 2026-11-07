package com.ivancea.MTGRules.appfunctions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.test.platform.app.InstrumentationRegistry
import com.ivancea.MTGRules.services.RulesService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/** Exercises the generated entry point with the real bundled resources, without changing UI state. */
@RequiresApi(36)
class RulesAppFunctionsTest {
    private lateinit var service: RulesAppFunctionService
    private lateinit var rulesService: RulesService

    @BeforeEach
    fun setup() {
        assumeTrue(Build.VERSION.SDK_INT >= 36)
        rulesService = RulesService(InstrumentationRegistry.getInstrumentation().targetContext)
        service = RulesAppFunctionService().also { it.rulesService = rulesService }
    }

    @Test
    fun defaultsAndPagination() = runBlocking {
        val editions = service.listRulesets()
        assertEquals(rulesService.rulesSources.map { it.date.toString() }, editions.map { it.id })
        val page = service.listRules(offset = 2, limit = 3)
        assertEquals(editions.last().id, page.rulesetId)
        assertEquals(listOf("3.", "4.", "5."), page.rules.map { it.id })
        assertEquals(listOf("Card Types", "Zones", "Turn Structure"), page.rules.map { it.title })
        val glossary = service.listRules(parentRuleId = "Glossary")
        assertEquals(50, glossary.rules.size)
        assertEquals(50, glossary.nextOffset)
        val rest = service.listRules(parentRuleId = "Glossary", offset = 50)
        assertTrue(glossary.rules.map { it.id }.intersect(rest.rules.map { it.id }.toSet()).isEmpty())
    }

    @Test
    fun historicalEditionsAndSingleRuleContent() = runBlocking {
        for (source in listOf(rulesService.rulesSources.first(), rulesService.latestRulesSource)) {
            val id = source.date.toString()
            val root = service.listRules(rulesetId = id)
            assertEquals(id, root.rulesetId)
            val section = service.getRule(id, "1")
            assertEquals("1.", section.rule.id)
            assertEquals("Game Concepts", section.rule.title)
            val definition = service.getRule(id, "Ability")
            assertTrue(definition.rule.description.isNotBlank())
            assertEquals(id, service.searchRules("\"combat damage\"", id).rulesetId)
            assertTrue(service.searchRules("\"combat damage\"", id).rules.isNotEmpty())
        }
    }

    @Test
    fun searchPagination() = runBlocking {
        val first = service.searchRules("")
        assertEquals(50, first.rules.size)
        assertEquals(50, first.nextOffset)
        val second = service.searchRules("", offset = 50)
        assertEquals(first.total, second.total)
        assertTrue(first.rules.map { it.id }.intersect(second.rules.map { it.id }.toSet()).isEmpty())
        val last = service.searchRules("", offset = first.total - 1)
        assertEquals(1, last.rules.size)
        assertNull(last.nextOffset)
    }

    @Test
    fun invalidRequestsHaveTypedErrors() {
        assertThrows(AppFunctionInvalidArgumentException::class.java) {
            runBlocking { service.searchRules("combat", offset = -1) }
        }
        assertThrows(AppFunctionInvalidArgumentException::class.java) {
            runBlocking { service.searchRules("combat", limit = 0) }
        }
        assertThrows(AppFunctionInvalidArgumentException::class.java) {
            runBlocking { service.listRules(limit = 0) }
        }
        assertThrows(AppFunctionInvalidArgumentException::class.java) {
            runBlocking { service.getRule(id = "") }
        }
        assertThrows(AppFunctionElementNotFoundException::class.java) {
            runBlocking { service.getRule(id = "999.999") }
        }
    }
}
