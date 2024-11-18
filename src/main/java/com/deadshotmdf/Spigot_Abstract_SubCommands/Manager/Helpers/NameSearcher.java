package com.deadshotmdf.Spigot_Abstract_SubCommands.Manager.Helpers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NameSearcher {

    private final TrieNode root;

    public NameSearcher() {
        root = new TrieNode();
    }

    public void addName(String name) {
        TrieNode node = root;
        for (char c : name.toCharArray())
            node = node.getChildren().computeIfAbsent(c, k -> new TrieNode());

        node.setEndOfWord(true);
    }

    public List<String> search(String prefix) {
        List<String> result = new LinkedList<>();
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            node = node.getChildren().get(c);
            if (node == null)
                return result;

        }

        dfs(node, new StringBuilder(prefix), result);
        return result;
    }

    private void dfs(TrieNode node, StringBuilder prefix, List<String> result) {
        if (node.isEndOfWord())
            result.add(prefix.toString());

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            prefix.append(entry.getKey());
            dfs(entry.getValue(), prefix, result);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    private static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean endOfWord;

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public boolean isEndOfWord() {
            return endOfWord;
        }

        public void setEndOfWord(boolean endOfWord) {
            this.endOfWord = endOfWord;
        }
    }
}

