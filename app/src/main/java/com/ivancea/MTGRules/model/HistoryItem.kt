package com.ivancea.MTGRules.model

import java.io.Serializable

data class HistoryItem(
    val type: Type,
    val value: Serializable?
) {
    enum class Type {
        Ignored,
        Rule,
        Search,
        Random
    }
}
