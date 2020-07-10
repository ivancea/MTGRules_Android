package com.ivancea.MTGRules.services;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RulesServiceTest {
    @Test
    public void loadRules() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        RulesService rulesService = new RulesService(context);

        assertTrue(rulesService.getRulesSources().size() > 0);

        for (RulesSource rulesSource : rulesService.getRulesSources()) {
            List<Rule> rules = rulesService.loadRules(rulesSource);

            assertNotNull("Load rules " + rulesSource.getFileName(), rules);
        }
    }
}
