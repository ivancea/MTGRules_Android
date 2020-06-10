package xyz.ivancea.mtgrules.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import xyz.ivancea.mtgrules.model.RulesSource;

public class RulesService {

    private static final List<RulesSource> RulesSources;

    static {
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

        RulesSources = rulesSources;
    }

    @Inject
    public RulesService() {
    }
}
