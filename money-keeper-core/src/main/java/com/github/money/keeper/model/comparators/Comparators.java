package com.github.money.keeper.model.comparators;

import com.github.money.keeper.model.core.RawTransaction;

import java.util.Comparator;

public final class Comparators {

    public static final class RawTransactions {

        public static Comparator<RawTransaction> natural() {
            return (t1, t2) -> {
                int compareResult = compare(t1.getDate(), t2.getDate(), Nulls.FORBIDDEN);
                if (compareResult != 0) return compareResult;
                compareResult = compare(t1.getSalePointId(), t2.getSalePointId(), Nulls.FORBIDDEN);
                if (compareResult != 0) return compareResult;
                return compare(t1.getAmount(), t2.getAmount(), Nulls.FORBIDDEN);
            };
        }

    }

    public static <T extends Comparable<T>> int compare(T t1, T t2, Nulls mode) {
        return compare(t1, t2, mode, Comparator.naturalOrder());
    }

    private static <T> int compare(T t1, T t2, Nulls mode, Comparator<T> comparator) {
        if (t1 != null && t2 != null) return comparator.compare(t1, t2);
        if (mode == Nulls.FORBIDDEN)
            throw new NullPointerException(String.format("Can't compare %s and %s because nulls are forbidden",
                    t1, t2));
        if (t1 == null && t2 == null) return 0;
        if (t1 == null) {
            if (mode == Nulls.FIRST) return 1;
            else return -1;
        }
        if (mode == Nulls.FIRST) return -1;
        else return 1;
    }


    public enum Nulls {
        FORBIDDEN, FIRST, LAST
    }

}
