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
        String greatestSubstring = Optional.ofNullable(result.greatestSubstring).orElse("");
        return greatestSubstring.endsWith(Character.toString(GeneralSuffixTree.FINAL_TERMINATOR))
                ? greatestSubstring.substring(0, greatestSubstring.length() - 1)
                : greatestSubstring;
    }

    // really never return null for greatestSubstring because node with only terminal symbols has endings of all strings :)
    private static GreatestSubstringLookupResult greatestCommonSubstring(Node node, String prefix, int stringsCount) {
        String currentSubstring = prefix + node.getValue();

        if (node.getChildren().isEmpty()) {
            return new GreatestSubstringLookupResult(
                    node.getContainsInStrings().size() == stringsCount ? currentSubstring : null,
                    node.getContainsInStrings()
            );
        }

        Set<Integer> containsInStrings = Sets.newHashSet();
        String maxSubstring = null;
        for (final Node child : node.getChildren().values()) {
            GreatestSubstringLookupResult result = greatestCommonSubstring(child, currentSubstring, stringsCount);
            containsInStrings.addAll(result.containsInStrings);
            if (lengthZeroIfNull(maxSubstring) < lengthZeroIfNull(result.greatestSubstring)) {
                maxSubstring = result.greatestSubstring;
            }
        }
        if (containsInStrings.size() == stringsCount) {
            return new GreatestSubstringLookupResult(maxSubstring == null ? currentSubstring : maxSubstring, containsInStrings);
        } else {
            return new GreatestSubstringLookupResult(null, containsInStrings);
        }
    }

    private static int lengthZeroIfNull(String source) {
        return source == null ? 0 : source.length();
    }

    private static class GreatestSubstringLookupResult {
        final String greatestSubstring;
        final Set<Integer> containsInStrings;

        public GreatestSubstringLookupResult(String greatestSubstring, Set<Integer> containsInStrings) {
            this.greatestSubstring = greatestSubstring;
            this.containsInStrings = containsInStrings;
        }
    }

}
