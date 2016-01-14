package com.github.money.keeper.util.advanced.strings;

import com.github.money.keeper.util.advanced.strings.GeneralSuffixTree.Node;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class StringUtils {

    public static String greatestCommonSubstring(Collection<String> source) {
        GeneralSuffixTree suffixTree = new GeneralSuffixTree(source);
        Node root = suffixTree.getRoot();
        GreatestSubstringLookupResult result = greatestCommonSubstring(root, "", source.size());
        return Optional.ofNullable(result.greatestSubstring).orElse("");
    }

    // really never return null for greatestSubstring because node with only terminal symbols has endings of all strings :)
    private static GreatestSubstringLookupResult greatestCommonSubstring(Node node, String prefix, int stringsCount) {
        if (node.getChildren().isEmpty()) {
            return new GreatestSubstringLookupResult(null, node.getEndedStrings());
        }

        String currentSubstring = prefix + node.getValue();
        Set<Integer> endedStrings = Sets.newHashSet();
        String maxSubstring = null;
        for (final Node child : node.getChildren().values()) {
            GreatestSubstringLookupResult result = greatestCommonSubstring(child, currentSubstring, stringsCount);
            endedStrings.addAll(result.endedStrings);
            if (lengthZeroIfNull(maxSubstring) < lengthZeroIfNull(result.greatestSubstring)) {
                maxSubstring = result.greatestSubstring;
            }
        }
        if (endedStrings.size() == stringsCount) {
            return new GreatestSubstringLookupResult(maxSubstring == null ? currentSubstring : maxSubstring, endedStrings);
        } else {
            return new GreatestSubstringLookupResult(null, endedStrings);
        }
    }

    private static int lengthZeroIfNull(String source) {
        return source == null ? 0 : source.length();
    }

    private static class GreatestSubstringLookupResult {
        final String greatestSubstring;
        final Set<Integer> endedStrings;

        public GreatestSubstringLookupResult(String greatestSubstring, Set<Integer> endedStrings) {
            this.greatestSubstring = greatestSubstring;
            this.endedStrings = endedStrings;
        }
    }

}
