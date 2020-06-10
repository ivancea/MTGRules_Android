package xyz.ivancea.mtgrules.ui.main;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import xyz.ivancea.mtgrules.R;
import xyz.ivancea.mtgrules.model.Rule;

public class RuleListAdapter extends RecyclerView.Adapter<RuleListAdapter.ViewHolder> {

    private static final Pattern RULE_LINK_PATTERN = Pattern.compile("(\\d{3}(?:\\.\\d+[a-z]?)?)");
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile("^Example:.*", Pattern.MULTILINE | Pattern.DOTALL);

    private List<Rule> rules;

    public RuleListAdapter(List<Rule> rules) {
        this.rules = rules;
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView ruleTitle;
        private TextView ruleText;

        public ViewHolder(View view) {
            super(view);

            this.ruleTitle = view.findViewById(R.id.ruleTitle);
            this.ruleText = view.findViewById(R.id.ruleText);
            this.ruleText.setMovementMethod(LinkMovementMethod.getInstance());

            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

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
        holder.getRuleTitle().setText(rules.get(position).getTitle());
        holder.getRuleText().setText(makeRulesTextSpannable(rules.get(position).getText()));
    }

    private Spannable makeRulesTextSpannable(String rulesText) {
        Spannable spannable = new SpannableString(rulesText);

        Matcher linkMatcher = RULE_LINK_PATTERN.matcher(rulesText);

        while (linkMatcher.find()) {
            spannable.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {

                    }
                },
                linkMatcher.start(),
                linkMatcher.end(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }

        Matcher exampleMatcher = EXAMPLE_PATTERN.matcher(rulesText);

        while (exampleMatcher.find()) {
            spannable.setSpan(
                new StyleSpan(Typeface.ITALIC),
                exampleMatcher.start(),
                exampleMatcher.end(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }

        return spannable;
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public void setRules(@NonNull List<Rule> rules) {
        this.rules = rules;

        notifyDataSetChanged();
    }
}
