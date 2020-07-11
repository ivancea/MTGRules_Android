package com.ivancea.MTGRules.ui.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import com.ivancea.MTGRules.constants.Actions;
import com.ivancea.MTGRules.MainActivity;
import com.ivancea.MTGRules.R;
import com.ivancea.MTGRules.model.Rule;

public class RuleListAdapter extends RecyclerView.Adapter<RuleListAdapter.ViewHolder> {

    private static final Pattern RULE_LINK_PATTERN = Pattern.compile("\\b(\\d{3}(?:\\.\\d+[a-z]?)?)\\b");
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile("^Example:", Pattern.MULTILINE);

    private List<Rule> rules = Collections.emptyList();

    private final Context context;

    public RuleListAdapter(Context context) {
        this.context = context;
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ruleTitle;
        private TextView ruleText;

        public ViewHolder(View view) {
            super(view);

            this.ruleTitle = view.findViewById(R.id.ruleTitle);
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
        holder.getRuleTitle().setText(rule.getTitle());
        holder.getRuleText().setText(makeRulesTextSpannable(rule.getText()));

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
                Intent intent = new Intent(context, MainActivity.class);
                intent.setAction(Actions.ACTION_READ);
                intent.putExtra(Actions.DATA, rule.getText());

                context.startActivity(intent);
                return true;
            });
        });
    }

    private Spannable makeRulesTextSpannable(String rulesText) {
        Spannable spannable = new SpannableString(rulesText);

        Matcher linkMatcher = RULE_LINK_PATTERN.matcher(rulesText);

        while (linkMatcher.find()) {
            String title = linkMatcher.group();
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

        Matcher exampleMatcher = EXAMPLE_PATTERN.matcher(rulesText);

        if (exampleMatcher.find()) {
            spannable.setSpan(
                new StyleSpan(Typeface.ITALIC),
                exampleMatcher.start(),
                rulesText.length(),
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
