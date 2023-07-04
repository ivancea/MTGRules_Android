package com.ivancea.MTGRules.utils

import com.ivancea.MTGRules.model.Rule
import java.util.stream.Stream
import kotlin.streams.toList

object RuleUtils {
    @JvmStatic
    fun flatten(rule: Rule): Stream<Rule> {
        return Stream.concat(
            Stream.of(rule),
            rule.subRules.stream()
                .flatMap { subRule: Rule ->
                    flatten(
                        subRule
                    )
                }
        )
    }

    @JvmStatic
    fun getRuleAndSubsections(rules: List<Rule>, title: String): List<Rule> {
        val foundRules = getRuleAndParents(rules, title).toList()

        if (foundRules.isEmpty()) {
            return emptyList()
        }

        val rule = foundRules.last()

        // It's a simple rule if:
        // - It doesn't end with a ".", so it's a sub-section itself (Glossary or "123.4a")
        // - It has 4 characters or less ("1.", "123.")
        // - It's not a leaf rule, and so it has no sub-sections
        if (rule.title.last() != '.' || rule.title.length <= 4 || foundRules.size < 3) {
            return listOf(rule)
        }

        // It may have sub-sections ("100.1" -> "100.1a", "100.1b", ...)

        val parent = foundRules[foundRules.size - 2]
        val indexInParent = parent.subRules.indexOfFirst { it.title == title }

        val titleWithoutDot = title.substring(0, title.length - 1)

        val subSections = parent.subRules
            .subList(indexInParent + 1, parent.subRules.size)
            .takeWhile { it.title.startsWith(titleWithoutDot) }

        return listOf(rule) + subSections
    }

    @JvmStatic
    fun getRuleAndParents(rules: List<Rule>, title: String): Stream<Rule> {
        if (!title[0].isDigit()) {
            val glossaryRule = rules.last()
            val foundRule =
                glossaryRule.subRules.find { (glossaryRuleTitle): Rule -> glossaryRuleTitle == title }

            return if (foundRule != null) {
                Stream.of(glossaryRule, foundRule)
            } else {
                Stream.of(glossaryRule)
            }
        }

        return rules.stream()
            .flatMap { rule: Rule ->
                if (rule.title[0] != title[0]) {
                    return@flatMap Stream.empty()
                }
                if (rule.title == title) {
                    return@flatMap Stream.of(rule)
                }

                Stream.concat(
                    Stream.of(rule),
                    rule.subRules.stream()
                        .flatMap subRuleFlatmap@{ subRule: Rule ->
                            if (subRule.title.substring(0, 3) != title.substring(0, 3)) {
                                return@subRuleFlatmap Stream.empty()
                            }
                            if (subRule.title == title) {
                                return@subRuleFlatmap Stream.of(
                                    subRule
                                )
                            }
                            Stream.concat(
                                Stream.of(subRule),
                                subRule.subRules.stream()
                                    .filter { (subRuleTitle): Rule -> subRuleTitle == title }
                            )
                        }
                )
            }
    }
}