package com.github.money.keeper.math;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class UnionFind<T> {

    private final BiMap<T, Integer> index = HashBiMap.create();
    private final int[] parent;
    private final int[] size;

    public UnionFind(Iterable<T> source) {
        int n = 0;
        for (final T item : source) {
            index.put(item, n);
            n++;
        }
        this.parent = new int[n];
        this.size = new int[n];
        for (int i = 0; i < n; i++) {
            size[i] = 1;
            parent[i] = i;
        }
    }

    public void union(T a, T b) {
        Preconditions.checkArgument(index.containsKey(a));
        Preconditions.checkArgument(index.containsKey(b));

        int aRoot = rootFor(index.get(a));
        int bRoot = rootFor(index.get(b));
        if (aRoot == bRoot) return;
        if (size[aRoot] > size[bRoot]) {
            parent[bRoot] = parent[aRoot];
            size[aRoot] = size[aRoot] + size[bRoot];
        } else {
            parent[aRoot] = parent[bRoot];
            size[bRoot] = size[aRoot] + size[bRoot];
        }
    }

    public T rootFor(T item) {
        Preconditions.checkArgument(index.containsKey(item));
        int pos = index.get(item);
        int p = rootFor(pos);
        return index.inverse().get(p);
    }

    private int rootFor(int pos) {
        int p = parent[pos];
        while (p != parent[p]) {
            p = parent[p];
        }
        compress(pos, p);
        return p;
    }

    private void compress(int startPos, int root) {
        int pos = startPos;
        while (parent[pos] != root) {
            pos = parent[pos];
            parent[pos] = root;
        }
    }
}
