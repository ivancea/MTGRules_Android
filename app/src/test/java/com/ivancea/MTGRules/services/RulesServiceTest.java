package com.ivancea.MTGRules.services;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RulesServiceTest {
	@Test
	public void notRepeatedRules() {
		RulesService rulesService = new RulesService(null);

		long totalDistinctRules = rulesService.getRulesSources().stream()
			.map(r -> r.getDate())
			.distinct()
			.count();

		assertEquals("Repeated rules", rulesService.getRulesSources().size(), totalDistinctRules);
	}
}
