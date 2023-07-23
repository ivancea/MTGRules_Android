package com.ivancea.MTGRules.utils

import com.ivancea.MTGRules.model.Rule
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

object RulesParser {
    @JvmStatic
    fun loadRules(reader: InputStreamReader): List<Rule>? {
        val rules = ArrayList<Rule>()

        val lines = BufferedReader(reader).use {
            it.lines()
                .map { text: String ->
                    sanitize(
                        text
                    )
                }
                .collect(Collectors.toList())
        }
        var lineIndex = 0
        var t: String
        do {
            t = lines[lineIndex++]
            if (lineIndex >= lines.size) {
                return null
            }
        } while (t != "Credits")
        var blankLines = 0
        while (lineIndex < lines.size) { // Rules
            t = lines[lineIndex++]
            if (t.length > 0) {
                if (blankLines > 0 && (t[0] < '1' || t[0] > '9')) { // Ended rules
                    break
                }
                if (t.indexOf(' ') - 1 >= 0 && t[t.indexOf(' ') - 1] == '.' && t.indexOf(
                        ' '
                    ) - 1 == t.indexOf('.')
                ) {
                    val pos = t.indexOf(' ')
                    val r = makeRule(
                        t.substring(0, pos),
                        t.substring(pos + 1)
                    )
                    if (t.indexOf('.') == 1) {
                        rules.add(r)
                    } else {
                        rules.last().subRules.add(r)
                    }
                } else {
                    if (blankLines == 0) {
                        val rule = rules.last().subRules.last().subRules.last()
                        rule.text = "${rule.text}\n$t"
                    } else {
                        val pos = t.indexOf(' ')
                        val r =
                            makeRule(
                                t.substring(0, pos),
                                t.substring(pos + 1)
                            )
                        rules.last().subRules.last().subRules.add(r)
                    }
                }
                blankLines = 0
            } else {
                blankLines++
            }
        }

        val glossaryTitle = lines[lineIndex - 1].trim { it <= ' ' }
        val glossary = makeRule(glossaryTitle, "")
        blankLines = 0
        var key = ""
        var value = ""
        while (lineIndex < lines.size) { // Glossary
            t = lines[lineIndex++]
            if (t.length > 0) {
                if (blankLines == 1) {
                    key = t
                } else {
                    if (value.length > 0) value += "\n"
                    value += t
                }
                blankLines = 0
            } else {
                if (key.length > 0) {
                    glossary.subRules.add(makeRule(key, value))
                }
                key = ""
                value = ""
                blankLines++
                if (blankLines >= 2) {
                    break
                }
            }
        }
        rules.add(glossary)
        return rules
    }

    private fun makeRule(title: String, text: String): Rule {
        // Fix sub-section rules that end in dot ("123.4a." -> "123.4a")
        if (title.endsWith(".") && title[title.length - 2].isLetter()) {
            return Rule(
                title.substring(0, title.length - 1),
                text
            )
        }

        return Rule(
            title,
            text
        )
    }

    private fun sanitize(text: String): String {
        return text
            .replace('“', '"')
            .replace('”', '"')
            .replace('’', '\'')
            .replace('—', '-')
            .replace('–', '-')
            .replace("^ $".toRegex(), "")
    }
}