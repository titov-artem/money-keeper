package com.github.money.keeper.util.advanced.strings;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

class GeneralSuffixTree {

    static final char FINAL_TERMINATOR = 'ø';
    private static final char ADDING_TERMINATOR = '∆';
    private static final int INFINITY = -1;

    private StringBuilder source = new StringBuilder();
    private Node root = new Node(0, 0, 0); // root is empty string

    public GeneralSuffixTree(Iterable<String> source) {
        root.sufLink = root;
        int i = 0;
        for (String s : source) {
            append(i, s);
            i++;
        }
    }

    Node getRoot() {
        return root;
    }

    private int lastNotEmptyRule = -1;
    private Set<Node> leafNodes = Sets.newHashSet();

    private void append(int n, String source) {
        int sourceStartPos = this.source.length();
        lastNotEmptyRule = sourceStartPos - 1;
        for (char c : source.toCharArray()) {
            extend(c, n);
        }
        extend(ADDING_TERMINATOR, n);
        terminate(n);
    }

    private void terminate(int n) {
        for (final Node leaf : Sets.newHashSet(leafNodes)) {
            assert leaf.charAt(leaf.length() - 1) == ADDING_TERMINATOR;
            leaf.containsInStrings.add(n);
            leaf.setRight(source.length());
        }
        source.replace(source.length() - 1, source.length(), Character.toString(FINAL_TERMINATOR));
        assert leafNodes.isEmpty();
    }

    private void extend(char c, int stringNumber) {
        // extend all current leafs
        source.append(c);
        // process suffixes from previous first empty rule to cur last not empty rule inclusively
        lastNotEmptyRule = processSuffixes(lastNotEmptyRule + 1, stringNumber);
    }

    private int processSuffixes(int startSuffix, int stringNumber) {
        AtomicReference<Node> lastSplitNode = new AtomicReference<>();
        int curSuffixStart = startSuffix;
        Node prevEndNode = root;
        while (curSuffixStart < source.length()) {
            Suffix curSuffix = new Suffix(curSuffixStart);
            SuffixProcessingResult result = processSuffix(curSuffix, prevEndNode, lastSplitNode, stringNumber);
            prevEndNode = result.node;
            if (result.rule == Rule.EMPTY) {
                break;
            }
            curSuffixStart++;
        }
        if (lastSplitNode.get() != null) lastSplitNode.get().sufLink = root;
        return curSuffixStart - 1;
    }

    private SuffixProcessingResult processSuffix(Suffix curSuf,
                                                 Node prevEndNode,
                                                 AtomicReference<Node> lastSplitNode,
                                                 int stringNumber) {
        Node curNode = prevEndNode;
        while (curNode.sufLink == null && curNode.parent != null) {
            curNode = curNode.parent;
        }
        curNode = curNode.sufLink;

        int sufPos = curNode.deep + curNode.length();
        if (!curNode.children.containsKey(curSuf.get(sufPos))) {
            Node node = new Node(curSuf.absPos(sufPos), INFINITY, curNode.deep + curNode.length());
            node.parent = curNode;
            curNode.children.put(curSuf.get(sufPos), node);
            leafNodes.add(node);
            return new SuffixProcessingResult(node, Rule.SPLIT);
        }
        curNode = curNode.children.get(curSuf.get(sufPos));
        int nodePos = 0;
        Rule rule;
        while (true) {
            while (nodePos < curNode.length() && sufPos < curSuf.length()
                    && curNode.charAt(nodePos) == curSuf.get(sufPos)) {
                nodePos++;
                sufPos++;
            }
            if (nodePos == curNode.length() && sufPos == curSuf.length()) {
                // we stop in the end of current node
                rule = Rule.EMPTY;
                break;
            }
            if (sufPos == curSuf.length()) {
                // we stop in the middle of the current node
                rule = Rule.EMPTY;
                break;
            }
            if (nodePos == curNode.length()) {
                // the node ended, but source not. We need to move to the child or create one
                if (curNode.children.containsKey(curSuf.get(sufPos))) {
                    curNode = curNode.children.get(curSuf.get(sufPos));
                    nodePos = 0;
                    continue;
                }
                rule = Rule.SPLIT;
                Node node = new Node(curSuf.absPos(sufPos), INFINITY, curNode.deep + curNode.length());
                node.parent = curNode;
                curNode.children.put(curSuf.get(sufPos), node);
                curNode = node;
                leafNodes.add(node);
                break;
            }
            // if we are here it means we need to split current node on two
            rule = Rule.SPLIT;
            // create nodes
            Node splitNode = new Node(curNode.left, curNode.absPos(nodePos), curNode.deep);
            Node next = new Node(curSuf.absPos(sufPos), INFINITY, curNode.deep + nodePos);
            curNode.left = curNode.absPos(nodePos);
            curNode.deep = curNode.deep + nodePos;
            // set up parents
            splitNode.parent = curNode.parent;
            next.parent = splitNode;
            curNode.parent = splitNode;
            // set up children
            splitNode.children.put(curNode.charAt(0), curNode);
            splitNode.children.put(next.charAt(0), next);
            splitNode.parent.children.put(splitNode.charAt(0), splitNode);

            if (lastSplitNode.get() != null) {
                lastSplitNode.get().sufLink = splitNode;
                lastSplitNode.set(splitNode);
            }

            curNode = next;
            leafNodes.add(next);
            break;
        }
        return new SuffixProcessingResult(curNode, rule);
    }

    private static class SuffixProcessingResult {
        final Node node;
        final Rule rule;

        SuffixProcessingResult(Node node, Rule rule) {
            this.node = node;
            this.rule = rule;
        }
    }

    private class Suffix {
        int start;

        Suffix(int start) {
            this.start = start;
        }

        char get(int i) {
            return source.charAt(start + i);
        }

        int length() {
            return source.length() - start;
        }

        int absPos(int i) {
            return start + i;
        }

        @Override
        public String toString() {
            return source.substring(start);
        }
    }

    private enum Rule {
        CONTINUATION, SPLIT, EMPTY
    }

    class Node {
        private int left;
        private int right; // INFINITY means global right;
        private final Map<Character, Node> children = new HashMap<>();
        private final Set<Integer> containsInStrings = new HashSet<>();

        private Node parent;
        private Node sufLink;
        private int deep;

        private Node(int left, int right, int deep) {
            this.left = left;
            this.right = right;
            this.deep = deep;
            if (this.right == INFINITY) {
                leafNodes.add(this);
            }
        }

        void setRight(int right) {
            if (this.right == INFINITY && right != INFINITY) {
                leafNodes.remove(this);
            }
            this.right = right;
        }

        public char charAt(int i) {
            return source.charAt(left + i);
        }

        public int length() {
            return (right == INFINITY ? source.length() : right) - left;
        }

        private int absPos(int i) {
            return left + i;
        }

        /**
         * @return view of children
         */
        public Map<Character, Node> getChildren() {
            return Collections.unmodifiableMap(children);
        }

        public String getValue() {
            return source.substring(left, left + length());
        }

        /**
         * @return view of numbers of strings, which suffixes ends in this node
         */
        public Set<Integer> getContainsInStrings() {
            return Collections.unmodifiableSet(containsInStrings);
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            appendToWithPadding("", out);
            return out.toString();
        }

        private void appendToWithPadding(String padding, StringBuilder out) {
            out.append(padding).append("Node[").append(source.substring(left, left + length())).append(containsInStrings).append(": \n");
            for (final Map.Entry<Character, Node> entry : children.entrySet()) {
                out.append(padding).append(entry.getKey()).append(": ");
                entry.getValue().appendToWithPadding(padding + "    ", out);
                out.append("\n");
            }
            out.append(padding).append("]");
        }
    }

    @Override
    public String toString() {
        return "GeneralSuffixTree{" +
                "source=" + source +
                ", root=" + root +
                '}';
    }
}