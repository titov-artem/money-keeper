package com.github.money.keeper.model.comparators;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;

import java.util.Comparator;

public final class Comparators {

    public static final class Transactions {

        public static Comparator<RawTransaction> natural() {
            Comparator<SalePoint> salePointComparator = SalePoints.natural();
            return (t1, t2) -> {
                int compareResult = compare(t1.getDate(), t2.getDate(), Nulls.FORBIDDEN);
                if (compareResult != 0) return compareResult;
                compareResult = compare(t1.getSalePoint(), t2.getSalePoint(), Nulls.FORBIDDEN, salePointComparator);
                if (compareResult != 0) return compareResult;
                return compare(t1.getAmount(), t2.getAmount(), Nulls.FORBIDDEN);
            };
        }

    }

    public static final class SalePoints {
        public static Comparator<SalePoint> natural() {
            return (s1, s2) -> {
                int compareResult = compare(s1.getName(), s2.getName(), Nulls.FORBIDDEN);
                if (compareResult != 0) return compareResult;
                return compare(s1.getCategoryDescription(), s2.getCategoryDescription(), Nulls.LAST);
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
