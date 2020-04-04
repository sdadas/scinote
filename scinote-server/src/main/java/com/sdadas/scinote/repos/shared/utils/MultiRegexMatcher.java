package com.sdadas.scinote.repos.shared.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class MultiRegexMatcher {

    private List<Pattern> patterns;

    public MultiRegexMatcher(int flags, String... regexes) {
        this.patterns = Arrays.stream(regexes).map(val -> Pattern.compile(val, flags)).collect(Collectors.toList());
    }

    public MultiRegexMatcher(String... regexes) {
        this(Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE, regexes);
    }

    public boolean matches(String text) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if(matcher.matches())  {
                return true;
            }
        }
        return false;
    }

    public Matcher matched(String text) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if(matcher.matches()) {
                return matcher;
            }
        }
        return null;
    }
}
