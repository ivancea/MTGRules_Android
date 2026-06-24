package com.ivancea.MTGRules.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RulesServiceTest {
    @Test
    fun notRepeatedRules() {
        val rulesSources = RulesService.rulesSources

        val totalDistinctRules = rulesSources
            .map { it.date }
            .distinct()
            .count()

        assertEquals(rulesSources.size, totalDistinctRules, "Repeated rules")
    }
}
