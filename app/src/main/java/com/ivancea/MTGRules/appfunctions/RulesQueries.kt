package com.ivancea.MTGRules.appfunctions

import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesSource
import com.ivancea.MTGRules.utils.RuleUtils
import com.ivancea.MTGRules.utils.RulesSearchUtils

/** Read-only operations over the existing parsed tree; never changes UI state. */
internal object RulesQueries {
    fun resolveSource(sources: List<RulesSource>, id: String?): RulesSource {
        if (id == null) return sources.last()
        if (!id.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw IllegalArgumentException("rulesetId must be YYYY-MM-DD.")
        }
        return sources.find { it.date.toString() == id }
            ?: throw NoSuchElementException("No bundled ruleset has ID '$id'.")
    }

    fun list(rulesetId: String, rules: List<Rule>, parentId: String?, offset: Int, limit: Int): RulesPage {
        require(offset >= 0) { "offset must be non-negative." }
        require(limit > 0) { "limit must be positive." }
        val children = if (parentId == null) rules else RuleUtils.findRule(rules, parentId).subRules
        val page = children.drop(offset).take(limit)
        val end = offset.toLong() + page.size
        return RulesPage(
            rulesetId,
            page.map { RuleSummary(it.title, heading(it)) },
            children.size,
            if (end < children.size) end.toInt() else null,
        )
    }

    fun get(rulesetId: String, rules: List<Rule>, id: String) =
        RuleResult(rulesetId, details(RuleUtils.findRule(rules, id)))

    fun search(rulesetId: String, rules: List<Rule>, query: String, offset: Int = 0, limit: Int = 50): RulesSearchResult {
        require(offset >= 0) { "offset must be non-negative." }
        require(limit > 0) { "limit must be positive." }
        val matches = RulesSearchUtils.search(query, rules)
        val page = matches.drop(offset).take(limit)
        val end = offset.toLong() + page.size
        return RulesSearchResult(
            rulesetId,
            page.map(::details),
            matches.size,
            if (end < matches.size) end.toInt() else null,
        )
    }

    private fun heading(rule: Rule): String = when {
        rule.title.firstOrNull()?.isDigit() != true -> rule.title
        rule.title.matches(Regex("\\d{1,3}\\.")) -> rule.text
        else -> ""
    }

    private fun details(rule: Rule) = RuleDetails(rule.title, heading(rule), rule.text)
}
