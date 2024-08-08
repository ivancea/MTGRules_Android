package com.ivancea.MTGRules.model

data class RulesDiff(
    val sourceRulesSource: RulesSource,
    val targetRulesSource: RulesSource,

    val changes: List<ChangedRule>,
) {
    data class ChangedRule(
        val sourceRule: Rule?,
        val targetRule: Rule?,
    ): Comparable<ChangedRule> {
        init {
            if(sourceRule == null && targetRule == null) {
                throw IllegalArgumentException("Both sourceRule and targetRule cannot be null")
            }
        }

        val title: String
            get() = sourceRule?.title ?: targetRule!!.title

        override fun compareTo(other: ChangedRule): Int {
            return title.compareTo(other.title)
        }
    }
}
