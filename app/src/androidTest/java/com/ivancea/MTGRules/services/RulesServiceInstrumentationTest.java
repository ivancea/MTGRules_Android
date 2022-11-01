package com.ivancea.MTGRules.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Pattern;
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
		assertTrue(rulesService.getRulesSources().size() > 0);
	}

	@ParameterizedTest
	@MethodSource("getRulesSources")
	public void validRules(RulesSource rulesSource) {
		List<Rule> rules = rulesService.loadRules(rulesSource);

		assertNotNull(rules, "Load rules " + rulesSource.getDate());

		checkRules(rules, null);
	}

	private void checkRules(List<Rule> rules, @Nullable Rule parentRule) {
		Integer parentRuleNumber = null;
		Integer lastRuleNumber = null;

		if (parentRule != null && NUMERIC_RULE_PATTERN.matcher(parentRule.getTitle()).matches()) {
			parentRuleNumber = Integer.parseInt(parentRule.getTitle().split("\\.")[0]);
		}

		for (Rule rule : rules) {
			assertNotNull(rule.getTitle(), "Rule title");
			assertNotNull(rule.getText(), "Rule text");

			if (NUMERIC_RULE_PATTERN.matcher(rule.getTitle()).matches()) {
				int ruleNumber = Integer.parseInt(rule.getTitle().split("\\.")[0]);

				if (lastRuleNumber != null) {
					assertThat(
						"Rule order",
						ruleNumber, greaterThanOrEqualTo(lastRuleNumber)
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
			}


			checkRules(rule.getSubRules(), rule);
		}
	}
}
