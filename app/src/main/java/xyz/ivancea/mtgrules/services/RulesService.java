package xyz.ivancea.mtgrules.services;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.Getter;
import xyz.ivancea.mtgrules.model.Rule;
import xyz.ivancea.mtgrules.model.RulesSource;

public class RulesService {
    private final Context context;

    @Getter
    private final List<RulesSource> RulesSources;

    @Inject
    public RulesService(Context context) {
        this.context = context;

        List<RulesSource> rulesSources = Collections.emptyList();

        try {
            rulesSources = Arrays.asList(
                new RulesSource("MagicCompRules_20150123.txt",
                    new URI("http://media.wizards.com/2015/docs/MagicCompRules_20150123.txt"),
                    LocalDate.of(2015, 1, 23),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules_20160722.txt",
                    new URI("http://media.wizards.com/2016/docs/MagicCompRules_20160722.txt"),
                    LocalDate.of(2016, 7, 22),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules_CN2_Update_20160826.txt",
                    new URI("http://media.wizards.com/2016/docs/MagicCompRules_CN2_Update_20160826.txt"),
                    LocalDate.of(2016, 8, 26),
                    Charset.forName("UTF-16")),

                new RulesSource("MagicCompRules_20160930.txt",
                    new URI("http://media.wizards.com/2016/docs/MagicCompRules_20160930.txt"),
                    LocalDate.of(2016, 9, 30),
                    Charset.forName("UTF-16")),

                new RulesSource("MagicCompRules_20161111.txt",
                    new URI("http://media.wizards.com/2016/docs/MagicCompRules_20161111.txt"),
                    LocalDate.of(2016, 11, 11),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules_20170119.txt",
                    new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170119.txt"),
                    LocalDate.of(2017, 1, 19),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules_20170428.txt",
                    new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170428.txt"),
                    LocalDate.of(2017, 4, 28),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules_20170605.txt",
                    new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170605.txt"),
                    LocalDate.of(2017, 6, 5),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules_20170707.txt",
                    new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170707.txt"),
                    LocalDate.of(2017, 7, 7),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules 20170825.txt",
                    new URI("http://media.wizards.com/2017/downloads/MagicCompRules%2020170825.txt"),
                    LocalDate.of(2017, 8, 25),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules 20170925.txt",
                    new URI("http://media.wizards.com/2017/downloads/MagicCompRules%2020170925.txt"),
                    LocalDate.of(2017, 9, 25),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020180119.txt",
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180119.txt"),
                    LocalDate.of(2018, 1, 19),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020180413.txt",
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180413.txt"),
                    LocalDate.of(2018, 4, 13),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020180608.txt",
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180608.txt"),
                    LocalDate.of(2018, 6, 8),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020180713.txt",
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180713.txt"),
                    LocalDate.of(2018, 7, 13),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020180810.txt",
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180810.txt"),
                    LocalDate.of(2018, 8, 10),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020181005.txt",
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020181005.txt"),
                    LocalDate.of(2018, 10, 5),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020190125.txt",
                    new URI("http://media.wizards.com/2019/downloads/MagicCompRules%2020190125.txt"),
                    LocalDate.of(2019, 1, 25),
                    Charset.forName("windows-1252")),

                new RulesSource("MagicCompRules%2020190712.txt",
                    new URI("https://media.wizards.com/2019/downloads/MagicCompRules%2020190712.txt"),
                    LocalDate.of(2019, 7, 12),
                    Charset.forName("UTF-8")),

                new RulesSource("MagicCompRules%2020190823.txt",
                    new URI("https://media.wizards.com/2019/downloads/MagicCompRules%2020190823.txt"),
                    LocalDate.of(2019, 8, 23),
                    Charset.forName("UTF-8")),

                new RulesSource("MagicCompRules%2020191004.txt",
                    new URI("https://media.wizards.com/2019/downloads/MagicCompRules%2020191004.txt"),
                    LocalDate.of(2019, 10, 04),
                    Charset.forName("UTF-8")),

                new RulesSource("MagicCompRules%2020200122.txt",
                    new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200122.txt"),
                    LocalDate.of(2020, 01, 22),
                    Charset.forName("UTF-8")),

                new RulesSource("MagicCompRules%2020200417.txt",
                    new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200417.txt"),
                    LocalDate.of(2020, 04, 17),
                    Charset.forName("UTF-8"))
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.RulesSources = rulesSources;
    }

    public List<Rule> loadRules(RulesSource rulesSource) {
        List<Rule> rules = new ArrayList<>();

        try(BufferedReader reader =
            new BufferedReader(
                new InputStreamReader(
                    context.getResources().openRawResource(
                        context.getResources().getIdentifier(rulesSource.getFileName(), "raw", context.getPackageName())
                    ),
                    rulesSource.getEncoding()
                )
            )) {

            List<String> lines = reader.lines()
                .map(this::sanitize)
                .collect(Collectors.toList());

            int lineIndex = 0;

            String t;
            do {
                t = lines.get(lineIndex++);
                if (lineIndex >= lines.size()) {
                    return null;
                }
            } while (!t.equals("Credits"));

            int blankLines = 0;
            while (lineIndex < lines.size()) { // Rules
                t = lines.get(lineIndex++);
                if (t.length() > 0) {
                    if (blankLines > 0 && (t.charAt(0) < '1' || t.charAt(0) > '9')) { // Ended rules
                        break;
                    }
                    if ((t.indexOf(' ') - 1) >= 0 && t.charAt(t.indexOf(' ') - 1) == '.' && t.indexOf(' ') - 1 == t.indexOf('.')) {
                        int pos = t.indexOf(' ');

                        Rule r = new Rule(
                            t.substring(0, pos),
                            t.substring(pos + 1)
                        );

                        if (t.indexOf('.') == 1) {
                            rules.add(r);
                        } else {
                            last(rules).getSubRules().add(r);
                        }
                    } else {
                        if (blankLines == 0) {
                            Rule rule = last(last(last(rules).getSubRules()).getSubRules());
                            rule.setText(rule.getText() + "\n" + t);
                        } else {
                            int pos = t.indexOf(' ');
                            Rule r = new Rule(
                                t.substring(0, pos),
                                t.substring(pos + 1)
                            );
                            last(last(rules).getSubRules()).getSubRules().add(r);
                        }
                    }

                    blankLines = 0;
                } else {
                    blankLines++;
                }
            }

            Rule glosary = new Rule("Glosary", null);
            blankLines = 0;
            String key = "";
            String value = "";
            while (lineIndex < lines.size()) { // Glosary
                t = lines.get(lineIndex++);

                if (t.length() > 0) {
                    if (blankLines == 1) {
                        key = t;
                    } else {
                        if (value.length() > 0)
                            value += "\n";
                        value += t;
                    }
                    blankLines = 0;
                } else {
                    if (key.length() > 0) {
                        glosary.getSubRules().add(new Rule(key, value));
                    }
                    key = "";
                    value = "";

                    blankLines++;
                    if (blankLines >= 2) {
                        break;
                    }
                }
            }

            rules.add(glosary);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rules;
    }

    private String sanitize(String text) {
        return text
            .replace('“', '"')
            .replace('”', '"')
            .replace('’', '\'')
            .replace('—', '-')
            .replace('–', '-');
    }

    private <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }
}
