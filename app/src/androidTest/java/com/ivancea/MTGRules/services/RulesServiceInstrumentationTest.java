package com.ivancea.MTGRules.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ivancea.MTGRules.constants.Symbols;
import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RulesServiceInstrumentationTest {

	private static final Pattern NUMERIC_RULE_PATTERN = Pattern.compile("^\\d+\\..*$");

	private static RulesService rulesService;

	@BeforeAll
	public static void setup() {
		Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		rulesService = new RulesService(context);
	}

	public static Stream<Arguments> getRulesSources() {
		return rulesService.getRulesSources().stream()
			.map(rulesSource -> Arguments.of(Named.of(
				"Rules " + rulesSource.getDate(),
				rulesSource
			)));
	}

	@Test
	public void anyRules() {
		assertThat(rulesService.getRulesSources(), not(empty()));
	}

	@ParameterizedTest
	@MethodSource("getRulesSources")
	public void validRules(RulesSource rulesSource) {
		List<Rule> rules = rulesService.loadRules(rulesSource);

		assertNotNull(rules, "Load rules " + rulesSource.getDate());

		checkGlossary(rules);

		List<Rule> rulesWithoutGlossary = rules.subList(0, rules.size() - 1);

		Map<String, Integer> repeatedRules = new HashMap<>();

		checkRulesRecursively(rulesWithoutGlossary, repeatedRules, null);

		var repeatedRuleKeys = repeatedRules.entrySet().stream()
			.filter(entry -> entry.getValue() > 1)
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());

		// Rules 17/09/2024 have repeated keys
		if (!rulesSource.getDate().equals(LocalDate.of(2024, 9, 17))) {
			assertThat(
				"Repeated rules",
				repeatedRuleKeys,
				empty()
			);
		}
	}

	@ParameterizedTest
	@MethodSource("getRulesSources")
	public void allSymbolsMapped(RulesSource rulesSource) {
		List<Rule> rules = rulesService.loadRules(rulesSource);
		List<String> missingSymbols = new ArrayList<>();

		for (Rule rule : rules) {
			checkSymbols(rule, missingSymbols);
		}

		assertThat(
			"Missing symbols",
			missingSymbols,
			empty()
		);
	}

	private static final Pattern SYMBOL_PATTERN = Pattern.compile("\\{(?<symbol>[\\w/]+)\\}");
	private static final Set<String> IGNORED_SYMBOLS = new HashSet<String>(){{
		add("8"); // Not found
		add("rN"); // Saga without roman numeral: Not found in SVG
		add("rN1"); // Saga with roman numeral: Not found with the number
		add("rN2"); // Saga with roman numeral: Not found with the number
	}};

	private void checkSymbols(Rule rule, List<String> missingSymbols) {
		Matcher matcher = SYMBOL_PATTERN.matcher(rule.getText());

		while (matcher.find()) {
			String symbol = matcher.group("symbol");

			if (IGNORED_SYMBOLS.contains(symbol)) {
				continue;
			}

			if (rule.getTitle().equals("201.5b") && symbol.equals("BB")) {
				// Type in rules
				continue;
			}

			if (!Symbols.getDrawablesBySymbol().containsKey(symbol)) {
				missingSymbols.add(symbol);
			}
		}

		for (Rule subRule : rule.getSubRules()) {
			checkSymbols(subRule, missingSymbols);
		}
	}

	private void checkGlossary(List<Rule> rules) {
		Rule glossaryRule = rules.get(rules.size() - 1);

		// This check should be removed or modified after adding translations
		assertEquals(
			"Glossary",
			glossaryRule.getTitle(),
			"Check glossary rule name in English"
		);

		assertFalse(
			NUMERIC_RULE_PATTERN.matcher(glossaryRule.getTitle()).matches(),
			"Glossary is the last rule"
		);

		for (Rule glossaryEntry : glossaryRule.getSubRules()) {
			assertFalse(
				NUMERIC_RULE_PATTERN.matcher(glossaryEntry.getTitle()).matches(),
				"Glossary entries aren't numbered"
			);
		}
	}

	private void checkRulesRecursively(List<Rule> rules, Map<String, Integer> repeatedRules, @Nullable Rule parentRule) {
		Integer parentRuleNumber = parentRule == null
			? null
			: Integer.parseInt(parentRule.getTitle().split("\\.")[0]);
		Integer lastRuleNumber = null;

		for (Rule rule : rules) {
			assertNotNull(rule.getTitle(), "Rule title");
			assertNotNull(rule.getText(), "Rule text");
			assertTrue(NUMERIC_RULE_PATTERN.matcher(rule.getTitle()).matches(), "Rule title is numeric");

			repeatedRules.merge(rule.getTitle(), 1, Integer::sum);

			int ruleNumber = Integer.parseInt(rule.getTitle().split("\\.")[0]);

			if (lastRuleNumber != null) {
				assertThat(
					"Rule order",
					ruleNumber, anyOf(equalTo(lastRuleNumber), equalTo(lastRuleNumber + 1))
				);
			}

			if (parentRuleNumber != null) {
				assertThat(
					"Child rule with greater number",
					ruleNumber, greaterThanOrEqualTo(parentRuleNumber)
				);

				if (parentRuleNumber < 10) {
					assertEquals(ruleNumber / 100,  parentRuleNumber, "Child rule is a subrule");
				}
			}

			lastRuleNumber = ruleNumber;

			checkRulesRecursively(rule.getSubRules(), repeatedRules, rule);
		}
	}
}
