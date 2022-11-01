package com.ivancea.MTGRules.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ivancea.MTGRules.model.RulesSource;

import org.junit.jupiter.api.Test;

import java.util.List;

public class RulesServiceTest {
	@Test
	public void notRepeatedRules() {
		RulesService rulesService = new RulesService(null);

		List<RulesSource> rulesSources = rulesService.getRulesSources();

		long totalDistinctRules = rulesSources.stream()
			.map(RulesSource::getDate)
			.distinct()
			.count();

		assertEquals(rulesSources.size(), totalDistinctRules, "Repeated rules");
	}
}
