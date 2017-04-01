package com.github.money.keeper.util.structure;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class TrieMap<T> {

    private static final class Node<T> {
        String key;
        Map<Character, Node<T>> children = Maps.newHashMap();
        boolean isTerminal;
        T value;

        public Node(String key, T value) {
            this.key = key;
            this.value = value;
            this.isTerminal = true;
        }
    }

    private final Node<T> root;

    public TrieMap() {
        root = new Node<>("", null);
        root.isTerminal = false;
    }

    public T put(String key, T value) {
        return addInternal(key, 0, root, value);
    }

    private T addInternal(String source, int pos, Node<T> node, T value) {
        int sourcePos = pos;
        int nodePos = 0;
        while (sourcePos < source.length() && nodePos < node.key.length()
                && source.charAt(sourcePos) == node.key.charAt(nodePos)) {
            sourcePos++;
            nodePos++;
        }
        if (sourcePos >= source.length() && nodePos >= node.key.length()) {
            // source and node ended, so this node corresponding to end of the source. Set terminal and value
            node.isTerminal = true;
            T prevValue = node.value;
            node.value = value;
            return prevValue;
        }
        if (nodePos >= node.key.length()) {
            // node ended, source not - get next symbol and go to the child or create one
            Character next = source.charAt(sourcePos);
            Node<T> nextNode = node.children.get(next);
            if (nextNode == null) {
                // no corresponding child - create one
                nextNode = new Node<>(source.substring(sourcePos), value);
                node.children.put(next, nextNode);
                return null;
            }
            return addInternal(source, sourcePos, nextNode, value);
        }
        if (sourcePos >= source.length()) {
            // source ended, split cur node to create node for source
            Node<T> newNode = new Node<>(node.key.substring(nodePos), value);
            newNode.children.putAll(node.children);
            newNode.isTerminal = node.isTerminal;
            node.children.clear();
            node.children.put(source.charAt(sourcePos), newNode);
            node.key = node.key.substring(0, nodePos);
            node.isTerminal = true;
            return null;
        }

        // found different char - split cur node and create node for source end
        Node<T> splitNode = new Node<>(node.key.substring(nodePos), node.value);
        splitNode.children.putAll(node.children);
        splitNode.isTerminal = node.isTerminal;
        node.children.clear();
        node.children.put(node.key.charAt(nodePos), splitNode);
        node.key = node.key.substring(0, nodePos);
        node.isTerminal = false;
        node.value = null;

        Node<T> nextNode = new Node<T>(source.substring(sourcePos), value);
        node.children.put(source.charAt(sourcePos), nextNode);
        return null;
    }

    /**
     * Look up trie by the key and return value corresponding to shortest prefix of the key
     *
     * @param key
     * @return shortest prefix with existing value and this value or null if nothing found
     */
    public Pair<String, T> getByFirstPrefix(String key) {
        return getByFirstPrefix(key, 0, root);
    }

    private Pair<String, T> getByFirstPrefix(String source, int pos, Node<T> node) {
        int sourcePos = pos;
        int nodePos = 0;
        while (sourcePos < source.length() && nodePos < node.key.length()
                && source.charAt(sourcePos) == node.key.charAt(nodePos)) {
            sourcePos++;
            nodePos++;
        }
        if (sourcePos >= source.length() && nodePos >= node.key.length()) {
            // source and node ended, if this is terminal node, return value, null otherwise
            if (node.isTerminal) {
                return Pair.of(source, node.value);
            } else {
                return null;
            }
        }
        if (nodePos >= node.key.length()) {
            // node ended, source not - if node is terminal - return value, else find next child or return null;
            if (node.isTerminal) return Pair.of(source.substring(0, sourcePos), node.value);
            Node<T> next = node.children.get(source.charAt(sourcePos));
            if (next == null) return null;
            return getByFirstPrefix(source, sourcePos, next);
        }
        if (sourcePos >= source.length()) {
            // source ended, node not - return null
            return null;
        }
        // found different symbols - return null
        return null;
    }
}
