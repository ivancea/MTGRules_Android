package com.ivancea.MTGRules.appfunctions

import androidx.appfunctions.AppFunctionSerializable
import java.time.LocalDate

/** A bundled edition of the Magic Comprehensive Rules. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RulesetInfo(
    /** Stable edition ID, formatted YYYY-MM-DD. */
    val id: String,
    /** Effective date of the edition. */
    val date: LocalDate,
)

/** An immediate child in the rules tree, without its description or descendants. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RuleSummary(
    /** Canonical rule identifier, including punctuation, or glossary term. */
    val id: String,
    /** Section heading or glossary term. Empty for numbered provisions without a heading. */
    val title: String,
)

/** One node's content, without descendant content. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RuleDetails(
    /** Canonical rule identifier, including punctuation, or glossary term. */
    val id: String,
    /** Section heading or glossary term. Empty for numbered provisions without a heading. */
    val title: String,
    /** The node's own text, including examples; excludes descendants. */
    val description: String,
)

/** A page of immediate children in document order. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RulesPage(
    /** Resolved edition ID, formatted YYYY-MM-DD. */
    val rulesetId: String,
    /** Entries in this page, without descriptions or descendants. */
    val rules: List<RuleSummary>,
    /** Total number of immediate children, before pagination. */
    val total: Int,
    /** Zero-based offset for the next page, or null when finished. */
    val nextOffset: Int?,
)

/** A single rule and the edition it belongs to. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RuleResult(
    /** Resolved edition ID, formatted YYYY-MM-DD. */
    val rulesetId: String,
    /** The requested node's content only. */
    val rule: RuleDetails,
)

/** A page of search matches in document order, without descendant content. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RulesSearchResult(
    /** Resolved edition ID, formatted YYYY-MM-DD. */
    val rulesetId: String,
    /** Matching nodes in this page, including matching headings and glossary entries. */
    val rules: List<RuleDetails>,
    /** Total number of matching nodes, before pagination. */
    val total: Int,
    /** Zero-based offset for the next page, or null when finished. */
    val nextOffset: Int?,
)
