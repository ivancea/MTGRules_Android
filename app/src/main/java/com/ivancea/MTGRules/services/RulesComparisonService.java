package com.ivancea.MTGRules.services;

import android.content.Context;

import com.ivancea.MTGRules.R;
import com.ivancea.MTGRules.model.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class RulesComparisonService {
    private static final Pattern rulePattern = Pattern.compile("\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letters>[a-z](?:-[a-z])?)?)?\\b", Pattern.CASE_INSENSITIVE);

    private final Context context;

    private final RulesService rulesService;

    @Inject
    public RulesComparisonService(@ApplicationContext Context context, RulesService rulesService) {
        this.context = context;
        this.rulesService = rulesService;
    }

    public List<Rule> compareRules(List<Rule> sourceRules, List<Rule> targetRules) {
        List<Rule> li = new ArrayList<>();

        for (Rule r1 : sourceRules) {
            Rule rule = compare(r1, findRule(targetRules, r1.getTitle()));
            if (rule != null) {
                li.add(rule);
            }
            for (Rule r2 : r1.getSubRules()) {
                rule = compare(r2, findRule(targetRules, r2.getTitle()));
                if (rule != null) {
                    li.add(rule);
                }
                for (Rule r3 : r2.getSubRules()) {
                    rule = compare(r3, findRule(targetRules, r3.getTitle()));
                    if (rule != null) {
                        li.add(rule);
                    }
                }
            }
        }

        for (Rule r1 : targetRules) {
            Rule rule = findRule(sourceRules, r1.getTitle());
            if (rule == null) {
                li.add(new Rule("(+) " + r1.getTitle(), r1.getText()));
            }
            for (Rule r2 : r1.getSubRules()) {
                rule = findRule(sourceRules, r2.getTitle());
                if (rule == null) {
                    li.add(new Rule("(+) " + r2.getTitle(), r2.getText()));
                }
                for (Rule r3 : r2.getSubRules()) {
                    rule = findRule(sourceRules, r3.getTitle());
                    if (rule == null) {
                        li.add(new Rule("(+) " + r3.getTitle(), r3.getText()));
                    }
                }
            }
        }

        return li;
    }


    private Rule compare(Rule from, Rule to)
    {
        if (to == null) {
            return new Rule("(-) " + from.getTitle(), from.getText());
        }
        if (from == null) {
            return new Rule("(+) " + to.getTitle(), to.getText());
        }
        if (!from.getTitle().equals(to.getTitle())) {
            return null;
        }

        if (!getRuleWithoutNumbers(from.getText()).equals(getRuleWithoutNumbers(to.getText()))) {
            return new Rule("(M) " + from.getTitle(), from.getText() + "\n\n " +
                context.getString(R.string.compare_changed_to) +
                " \n\n" + to.getText());
        }
        return null;
    }

    private static String getRuleWithoutNumbers(String rule)
    {
        return rulePattern.matcher(rule).replaceAll("@");
    }

    private static Rule findRule(List<Rule> rules, String title)
    {
        for (Rule r1 : rules)
        {
            if (r1.getTitle().equals(title)) {
                return r1;
            }
            for (Rule r2 : r1.getSubRules())
            {
                if (r2.getTitle().equals(title)) {
                    return r2;
                }
                for (Rule r3 : r2.getSubRules())
                {
                    if (r3.getTitle().equals(title)) {
                        return r3;
                    }
                }
            }
        }
        return null;
    }
}
