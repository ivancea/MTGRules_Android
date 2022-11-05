package com.ivancea.MTGRules.ui.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.ivancea.MTGRules.R;
import com.ivancea.MTGRules.constants.Symbols;
import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.ui.spans.RuleClickableSpan;
import com.ivancea.MTGRules.utils.IntentSender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;

public class RuleListAdapter extends RecyclerView.Adapter<RuleListAdapter.ViewHolder> {

    private static final Pattern IS_PARENT_RULE_PATTERN = Pattern.compile("^(\\d{1,3}\\.|Glossary)$");
    private static final Pattern RULE_LINK_PATTERN = Pattern.compile("\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letter>[a-z])?)?\\b");
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile("^Example:", Pattern.MULTILINE);
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("\\{(?<symbol>[\\w/]+)\\}");

    private List<Rule> rules = Collections.emptyList();
    private String selectedRuleTitle = null;
    private Pattern searchTextPattern = null;

    private final Context context;
    private final MainViewModel viewModel;

    public RuleListAdapter(FragmentActivity activity) {
        this.context = activity;
        this.viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ruleTitle;
        private final TextView ruleSubtitle;
        private final TextView ruleText;

        public ViewHolder(View view) {
            super(view);

            this.ruleTitle = view.findViewById(R.id.ruleTitle);
            this.ruleSubtitle = view.findViewById(R.id.ruleSubtitle);
            this.ruleText = view.findViewById(R.id.ruleText);
            this.ruleText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.rule_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rule rule = rules.get(position);

        holder.getRuleTitle().setText(makeRuleTitleSpannable(rule.getTitle()));

        if (IS_PARENT_RULE_PATTERN.matcher(rule.getTitle()).matches()) {
            holder.getRuleSubtitle().setText(rule.getText());
            holder.getRuleText().setText("");
            holder.getRuleText().setVisibility(View.GONE);
        } else {
            holder.getRuleSubtitle().setText("");
            holder.getRuleText().setText(makeRuleTextSpannable(holder, rule.getText()));
            holder.getRuleText().setVisibility(View.VISIBLE);
        }

        View.OnClickListener onClickListener = v -> {
            IntentSender.openRule(context, rule.getTitle());
        };

        holder.itemView.setOnClickListener(onClickListener);
        holder.getRuleTitle().setOnClickListener(onClickListener);
        holder.getRuleText().setOnClickListener(onClickListener);

        holder.itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
            menu.add(0, view.getId(), 0, R.string.context_copy).setOnMenuItemClickListener(item -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(rule.getTitle(), rule.getTitle() + ": " + rule.getText());
                clipboard.setPrimaryClip(clip);
                return true;
            });
            menu.add(0, view.getId(), 0, R.string.context_share).setOnMenuItemClickListener(item -> {
                String text = rule.getTitle() + ": " + rule.getText();
                new ShareCompat.IntentBuilder(context)
                    .setType("text/plain")
                    .setText(text)
                    .startChooser();
                return true;
            });
            menu.add(0, view.getId(), 0, R.string.context_read).setOnMenuItemClickListener(item -> {
                IntentSender.readText(context, rule.getText());
                return true;
            });
        });

        holder.itemView.setSelected(rule.getTitle().equals(selectedRuleTitle));
    }

    private Spannable makeRuleTitleSpannable(String ruleTTile) {
        Spannable spannable = new SpannableString(ruleTTile);

        highlightSearchText(spannable, ruleTTile);

        return spannable;
    }

    private Spannable makeRuleTextSpannable(ViewHolder holder, String ruleText) {
        Spannable spannable = new SpannableString(ruleText);

        formatLinks(spannable, ruleText);
        formatExample(spannable, ruleText);
        highlightSearchText(spannable, ruleText);
        replaceSymbols(holder, spannable, ruleText);

        return spannable;
    }

    private void formatLinks(Spannable spannable, String ruleText) {
        formatRuleTitleLinks(spannable, ruleText);
        formatGlossaryLinks(spannable, ruleText);
    }

    private void formatRuleTitleLinks(Spannable spannable, String ruleText) {
        Matcher linkMatcher = RULE_LINK_PATTERN.matcher(ruleText);

        while (linkMatcher.find()) {
            String title = normalizeTitle(
                linkMatcher.group("rule"),
                linkMatcher.group("subRule"),
                linkMatcher.group("letter")
            );
            spannable.setSpan(
                new RuleClickableSpan(title),
                linkMatcher.start(),
                linkMatcher.end(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
    }

    private void formatGlossaryLinks(Spannable spannable, String ruleText) {
        List<Rule> rules = viewModel.getCurrentRules().getValue();

        Rule glosssaryRule = rules.get(rules.size() - 1);

        List<String> glosssaryTerms = glosssaryRule.getSubRules().stream()
            .map(Rule::getTitle)
            .sorted(Comparator.comparingInt(String::length).reversed())
            .collect(Collectors.toList());

        for (String glosssaryTerm : glosssaryTerms) {
            // TODO: Cache patterns on rule set change
            Matcher matcher = Pattern.compile(
                "\\b" + makePluralAcceptingGlossaryRegex(Pattern.quote(glosssaryTerm)) + "\\b", Pattern.CASE_INSENSITIVE
            )
                .matcher(ruleText);

            while (matcher.find()) {
                spannable.setSpan(
                    new RuleClickableSpan(glosssaryTerm),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );
            }
        }
    }

    /**
     * Takes a regex, and returns another regex that also accepts plurals.
     * <br/>
     * Note: This method should be replaced with a proper pluralization library, or should use translations.
     *
     * @param glossaryRegex The glossary regex to pluralize
     * @return A regex accepting plurals
     */
    private String makePluralAcceptingGlossaryRegex(String glossaryRegex) {
        return glossaryRegex + "(?:s|es)?";
    }

    private void formatExample(Spannable spannable, String ruleText) {
        Matcher exampleMatcher = EXAMPLE_PATTERN.matcher(ruleText);

        if (exampleMatcher.find()) {
            spannable.setSpan(
                new StyleSpan(Typeface.ITALIC),
                exampleMatcher.start(),
                ruleText.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
    }

    private void highlightSearchText(Spannable spannable, String ruleText) {
        if (searchTextPattern == null) {
            return;
        }

        Matcher searchTextMatcher = searchTextPattern.matcher(ruleText);

        while (searchTextMatcher.find()) {
            spannable.setSpan(
                new BackgroundColorSpan(ColorUtils.setAlphaComponent(Color.YELLOW, 100)),
                searchTextMatcher.start(),
                searchTextMatcher.end(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
    }

    private void replaceSymbols(ViewHolder holder, Spannable spannable, String ruleText) {
        Matcher symbolMatcher = SYMBOL_PATTERN.matcher(ruleText);

        while (symbolMatcher.find()) {
            String symbol = symbolMatcher.group("symbol");
            Integer drawableId = Symbols.getDrawableId(symbol);

            if (drawableId != null) {
                Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), drawableId, context.getTheme());
                int height = holder.getRuleText().getLineHeight();
                int width = height * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();

                drawable.setBounds(0, 0, width, height);
                spannable.setSpan(
                    new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE),
                    symbolMatcher.start(),
                    symbolMatcher.end(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );;
            }
        }
    }

    private String normalizeTitle(String ruleNumber, String subRuleNumber, String subRuleLetter) {
        if (subRuleNumber == null) {
            return ruleNumber + ".";
        }

        if (subRuleLetter == null) {
            return ruleNumber + "." + subRuleNumber + ".";
        }

        return ruleNumber + "." + subRuleNumber + subRuleLetter;
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public void setRules(@NonNull List<Rule> rules) {
        this.rules = rules;

        notifyDataSetChanged();
    }

    public void setSearchText(@Nullable String searchText) {
        this.searchTextPattern = searchText == null
            ? null
            : Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);

        notifyDataSetChanged();
    }

    public void setSelectedRuleTitle(@Nullable String selectedRuleTitle) {
        this.selectedRuleTitle = selectedRuleTitle;
        this.notifyDataSetChanged();
    }
}
