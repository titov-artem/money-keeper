package com.github.money.keeper.math;

public class LevenshteinDistance {

    public static int distance(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                d[i][j] = min(
                        d[i - 1][j] + 1,
                        d[i][j - 1] + 1,
                        a.charAt(i - 1) == b.charAt(j - 1) ? d[i - 1][j - 1] : d[i - 1][j - 1] + 1
                );
            }
        }
        return d[n][m];
    }

    private static int min(int a1, int... a) {
        int min = a1;
        for (final int v : a) {
            if (v < min) min = v;
        }
        return min;
    }

}
