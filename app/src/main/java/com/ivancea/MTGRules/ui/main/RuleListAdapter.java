package com.ivancea.MTGRules.ui.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.ivancea.MTGRules.MainActivity;
import com.ivancea.MTGRules.R;
import com.ivancea.MTGRules.constants.Actions;
import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.utils.IntentSender;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class RuleListAdapter extends RecyclerView.Adapter<RuleListAdapter.ViewHolder> {

    private static final Pattern IS_PARENT_RULE_PATTERN = Pattern.compile("^(\\d{1,3}\\.|Glossary)$");
    private static final Pattern RULE_LINK_PATTERN = Pattern.compile("\\b(?<rule>\\d{3})(?:\\.(?<subRule>\\d+)(?<letter>[a-z])?)?\\b");
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile("^Example:", Pattern.MULTILINE);

    private List<Rule> rules = Collections.emptyList();
    private String selectedRuleTitle = null;
    private Pattern searchTextPattern = null;

    private final Context context;

    public RuleListAdapter(Context context) {
        this.context = context;
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
            holder.getRuleText().setText(makeRuleTextSpannable(rule.getText()));
            holder.getRuleText().setVisibility(View.VISIBLE);
        }

        View.OnClickListener onClickListener = v -> {
            Intent intent = new Intent(context, MainActivity.class);

            intent.setAction(Actions.ACTION_NAVIGATE_RULE);
            intent.putExtra(Actions.DATA, rule.getTitle());

            context.startActivity(intent);
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

    private Spannable makeRuleTextSpannable(String ruleText) {
        Spannable spannable = new SpannableString(ruleText);

        makeLinks(spannable, ruleText);
        makeExample(spannable, ruleText);
        highlightSearchText(spannable, ruleText);

        return spannable;
    }

    private void makeLinks(Spannable spannable, String ruleText) {
        Matcher linkMatcher = RULE_LINK_PATTERN.matcher(ruleText);

        while (linkMatcher.find()) {
            String title = normalizeTitle(
                linkMatcher.group("rule"),
                linkMatcher.group("subRule"),
                linkMatcher.group("letter")
            );
            spannable.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setAction(Actions.ACTION_NAVIGATE_RULE);
                        intent.putExtra(Actions.DATA, title);

                        context.startActivity(intent);
                    }
                },
                linkMatcher.start(),
                linkMatcher.end(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
    }

    private void makeExample(Spannable spannable, String ruleText) {
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
