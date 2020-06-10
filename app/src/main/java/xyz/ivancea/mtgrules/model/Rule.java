package xyz.ivancea.mtgrules.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class Rule {
    private final String title;

    private final String text;

    private final List<Rule> subRules;

    public Rule(@NonNull String title, @NonNull String text, @Nullable List<Rule> subRules) {
        this.title = title;
        this.text = text;
        this.subRules = Collections.unmodifiableList(
            subRules == null || subRules.isEmpty()
                ? Collections.<Rule>emptyList()
                : new ArrayList<>(subRules)
        );
    }
}
