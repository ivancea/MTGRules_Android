package com.ivancea.MTGRules.utils

import com.ivancea.MTGRules.model.Rule
import java.util.stream.Stream

object RuleUtils {
    fun flatten(rule: Rule): Stream<Rule> {
        return Stream.concat(
            Stream.of(rule),
            rule.subRules.stream()
                .flatMap { rule: Rule ->
                    flatten(
                        rule
                    )
                }
        )
    }
}