package xyz.ivancea.mtgrules.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rule {
    private String title;

    private String text;

    private final List<Rule> subRules = new ArrayList<>();
}
