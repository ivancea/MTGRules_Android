package com.ivancea.MTGRules.model

sealed class VisibleRules {
    data class Rules(val rules: List<Rule>) : VisibleRules()
    data class Diff(val diff: RulesDiff) : VisibleRules()
    object Empty : VisibleRules()
}