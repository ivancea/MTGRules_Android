package com.ivancea.MTGRules.services;

import android.content.Context;

import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class RulesService {
	private final Context context;

	private final List<RulesSource> rulesSources;

	public List<RulesSource> getRulesSources() {
		return rulesSources;
	}

	@Inject
	public RulesService(@ApplicationContext Context context) {
		this.context = context;

		List<RulesSource> rulesSources = Collections.emptyList();

		try {
			rulesSources = Arrays.asList(
				new RulesSource(
					new URI("http://media.wizards.com/2015/docs/MagicCompRules_20150123.txt"),
					LocalDate.of(2015, 1, 23),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2016/docs/MagicCompRules_20160722.txt"),
					LocalDate.of(2016, 7, 22),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2016/docs/MagicCompRules_CN2_Update_20160826.txt"),
					LocalDate.of(2016, 8, 26),
					StandardCharsets.UTF_16
				),

				new RulesSource(
					new URI("http://media.wizards.com/2016/docs/MagicCompRules_20160930.txt"),
					LocalDate.of(2016, 9, 30),
					StandardCharsets.UTF_16
				),

				new RulesSource(
					new URI("http://media.wizards.com/2016/docs/MagicCompRules_20161111.txt"),
					LocalDate.of(2016, 11, 11),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170119.txt"),
					LocalDate.of(2017, 1, 19),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170428.txt"),
					LocalDate.of(2017, 4, 28),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170605.txt"),
					LocalDate.of(2017, 6, 5),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2017/downloads/MagicCompRules_20170707.txt"),
					LocalDate.of(2017, 7, 7),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2017/downloads/MagicCompRules%2020170825.txt"),
					LocalDate.of(2017, 8, 25),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2017/downloads/MagicCompRules%2020170925.txt"),
					LocalDate.of(2017, 9, 25),
					Charset.forName("windows-1252")
				),

                /* NOT FOUND
                new RulesSource(
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180119.txt"),
                    LocalDate.of(2018, 1, 19),
                    Charset.forName("windows-1252")
                ),
                 */

                /* NOT FOUND
                new RulesSource(
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180413.txt"),
                    LocalDate.of(2018, 4, 13),
                    Charset.forName("windows-1252")
                ),
                 */

                /* NOT FOUND
                new RulesSource(
                    new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180608.txt"),
                    LocalDate.of(2018, 6, 8),
                    Charset.forName("windows-1252")
                ),
                 */

				new RulesSource(
					new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180713.txt"),
					LocalDate.of(2018, 7, 13),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020180810.txt"),
					LocalDate.of(2018, 8, 10),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2018/downloads/MagicCompRules%2020181005.txt"),
					LocalDate.of(2018, 10, 5),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("http://media.wizards.com/2019/downloads/MagicCompRules%2020190125.txt"),
					LocalDate.of(2019, 1, 25),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("https://media.wizards.com/2019/downloads/MagicCompRules%2020190712.txt"),
					LocalDate.of(2019, 7, 12),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2019/downloads/MagicCompRules%2020190823.txt"),
					LocalDate.of(2019, 8, 23),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2019/downloads/MagicCompRules%2020191004.txt"),
					LocalDate.of(2019, 10, 4),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200122.txt"),
					LocalDate.of(2020, 1, 22),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200417.txt"),
					LocalDate.of(2020, 4, 17),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200703.txt"),
					LocalDate.of(2020, 7, 3),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200807.txt"),
					LocalDate.of(2020, 8, 7),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020200925.txt"),
					LocalDate.of(2020, 9, 25),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2020/downloads/MagicCompRules%2020201120.txt"),
					LocalDate.of(2020, 11, 20),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%2020210202.txt"),
					LocalDate.of(2021, 2, 2),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%2020210224.txt"),
					LocalDate.of(2021, 2, 24),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%2020210419.txt"),
					LocalDate.of(2021, 4, 19),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%2020210609.txt"),
					LocalDate.of(2021, 6, 9),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%2020210712.txt"),
					LocalDate.of(2021, 7, 12),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%20202109224.txt"),
					LocalDate.of(2021, 9, 24),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2021/downloads/MagicCompRules%2020211115.txt"),
					LocalDate.of(2021, 11, 15),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/MagicCompRules%2020220218.txt"),
					LocalDate.of(2022, 2, 18),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/MagicCompRules%2020220429.txt"),
					LocalDate.of(2022, 4, 29),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/MagicCompRules%2020220610.txt"),
					LocalDate.of(2022, 6, 10),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/MagicCompRules%2020220708.txt"),
					LocalDate.of(2022, 7, 8),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/MagicCompRules%2020220908.txt"),
					LocalDate.of(2022, 9, 8),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/Comprehensive%20Rules%2020221007.txt"),
					LocalDate.of(2022, 10, 7),
					Charset.forName("windows-1252")
				),

				new RulesSource(
					new URI("https://media.wizards.com/2022/downloads/MagicCompRules%2020221118.txt"),
					LocalDate.of(2022, 11, 18),
					StandardCharsets.UTF_16BE
				),

				new RulesSource(
					new URI("https://media.wizards.com/2023/downloads/MagicComp%20Rules%2020230203.txt"),
					LocalDate.of(2023, 2, 3),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2023/downloads/MagicCompRules%2020230414.txt"),
					LocalDate.of(2023, 4, 14),
					StandardCharsets.UTF_8
				),

				new RulesSource(
					new URI("https://media.wizards.com/2023/downloads/MagicCompRules20230616.txt"),
					LocalDate.of(2023, 6, 16),
					StandardCharsets.UTF_8
				)
			);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		this.rulesSources = rulesSources;
	}

	public RulesSource getLatestRulesSource() {
		return rulesSources.get(rulesSources.size() - 1);
	}

	public List<Rule> loadRules(RulesSource rulesSource) {
		List<Rule> rules = new ArrayList<>();

		String fileName = "rules_" + rulesSource.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		try (BufferedReader reader =
			     new BufferedReader(
				     new InputStreamReader(
					     context.getResources().openRawResource(
						     context.getResources().getIdentifier(fileName, "raw", context.getPackageName())
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

			String glossaryTitle = lines.get(lineIndex - 1).trim();

			Rule glossary = new Rule(glossaryTitle, "");
			blankLines = 0;
			String key = "";
			String value = "";
			while (lineIndex < lines.size()) { // Glossary
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
						glossary.getSubRules().add(new Rule(key, value));
					}
					key = "";
					value = "";

					blankLines++;
					if (blankLines >= 2) {
						break;
					}
				}
			}

			rules.add(glossary);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		String gptText = rules.stream().flatMap(rule1 ->
			Stream.concat(
				Stream.of(rule1.getTitle() + rule1.getText()),
				rule1.getTitle().equals("Glossary")
					? rule1.getSubRules().stream().map(rule2 -> " - " + rule2.getTitle())
					: rule1.getSubRules().stream().map(rule2 -> " - " + rule2.getTitle() + rule2.getText())
			)
		).collect(Collectors.joining("\n"));

		return rules;
	}

	private String sanitize(String text) {
		return text
			.replace('“', '"')
			.replace('”', '"')
			.replace('’', '\'')
			.replace('—', '-')
			.replace('–', '-')
			.replaceAll("^ $", "");
	}

	private <T> T last(List<T> list) {
		return list.get(list.size() - 1);
	}
}
