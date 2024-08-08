package com.ivancea.MTGRules.services

import android.content.Context
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.model.RulesDiff
import com.ivancea.MTGRules.model.RulesSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.regex.Pattern
import javax.inject.Inject

class RulesComparisonService @Inject constructor(@param:ApplicationContext private val context: Context) {
    fun compareRules(
        source: RulesSource,
        target: RulesSource,
        sourceRules: List<Rule>,
        targetRules: List<Rule>
    ): RulesDiff {
        val changes = mutableListOf<RulesDiff.ChangedRule>()

        for (r1 in sourceRules) {
            compare(r1, findRule(r1.title, targetRules))?.let {
                changes.add(it)
            }
            for (r2 in r1.subRules) {
                compare(r2, findRule(r2.title, targetRules))?.let {
                    changes.add(it)
                }
                for (r3 in r2.subRules) {
                    compare(r3, findRule(r3.title, targetRules))?.let {
                        changes.add(it)
                    }
                }
            }
        }

        for (r1 in targetRules) {
            var rule = findRule(r1.title, sourceRules)
            if (rule == null) {
                changes.add(RulesDiff.ChangedRule(null, r1))
            }
            for (r2 in r1.subRules) {
                rule = findRule(r2.title, sourceRules)
                if (rule == null) {
                    changes.add(RulesDiff.ChangedRule(null, r2))
                }
                for (r3 in r2.subRules) {
                    rule = findRule(r3.title, sourceRules)
                    if (rule == null) {
                        changes.add(RulesDiff.ChangedRule(null, r3))
                    }
                }
            }
        }

        changes.sort()

        return RulesDiff(source, target, changes)
    }

    private fun compare(from: Rule?, to: Rule?): RulesDiff.ChangedRule? {
        if (to == null) {
            return RulesDiff.ChangedRule(from, null)
        }
        if (from == null) {
            return RulesDiff.ChangedRule(null, to)
        }

        if (from.title != to.title) {
            throw IllegalArgumentException("Titles do not match")
        }

        if (getRuleWithoutNumbers(from.text) != getRuleWithoutNumbers(to.text)) {
            return RulesDiff.ChangedRule(from, to)
        }

        return null
    }

    companion object {
        private val rulePattern: Pattern = Pattern.compile(
            "\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letters>[a-z](?:-[a-z])?)?)?\\b",
            Pattern.CASE_INSENSITIVE
        )

        private fun getRuleWithoutNumbers(rule: String): String {
            return rulePattern.matcher(rule).replaceAll("@")
        }

        private fun findRule(title: String, rules: List<Rule>): Rule? {
            for (rule in rules) {
                if (rule.title == title) {
                    return rule
                }

                val matchingChildRule = findRule(title, rule.subRules)

                if (matchingChildRule != null) {
                    return matchingChildRule
                }
            }

            return null
        }
    }
}
