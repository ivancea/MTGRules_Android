package com.ivancea.MTGRules.utils

import com.ivancea.MTGRules.model.Rule
import kotlin.streams.toList

object RulesSearchUtils {
    @JvmStatic
    fun search(query: String, rules: List<Rule>?): List<Rule> {
        if (rules == null) {
            return ArrayList()
        }

        val tokens = tokenize(query)

        val filteredRules = rules.stream()
            .flatMap(RuleUtils::flatten)
            .filter { (title, text) ->
                tokens.all { token ->
                    title.contains(token, ignoreCase = true) || text.contains(token, ignoreCase = true)
                }
            }
            .toList()

        return filteredRules
    }

    @JvmStatic
    fun tokenize(query: String): Set<String> {
        val tokens = HashSet<String>()

        // The negative lookbehind avoids matching escaped backslashes
        val blocks = query.split("(?<!\\\\)\"".toRegex()).toList()

        for (i in blocks.indices) {
            // Convert escaped quotes to quotes
            val block = blocks[i]
                .replace("\\\\\"".toRegex(), "\"")

            if (i % 2 == 0) {
                // Even blocks are non-quoted blocks
                // Split them by spaces and add the words
                for (word in block.split("\\s+".toRegex())) {
                    if (word.isNotEmpty()) {
                        tokens.add(word)
                    }
                }
            } else {
                // Odd blocks are quoted blocks
                // Add them as a single token
                if (block.isNotEmpty()) {
                    tokens.add(block)
                }
            }
        }

        return tokens
    }
}