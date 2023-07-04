package com.ivancea.MTGRules.model

data class Rule(
    var title: String,
    var text: String,
) {
    val subRules = ArrayList<Rule>()
}
