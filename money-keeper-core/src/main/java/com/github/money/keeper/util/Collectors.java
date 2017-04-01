package com.github.money.keeper.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Collectors {

    public static <T, K, U> Collector<T, ?, ListMultimap<K, U>> toListMultimap(Function<? super T, ? extends K> keyMapper,
                                                                               Function<? super T, ? extends U> valueMapper) {
        return new Collector<T, ListMultimap<K, U>, ListMultimap<K, U>>() {
            @Override
            public Supplier<ListMultimap<K, U>> supplier() {
                return ArrayListMultimap::create;
            }

            @Override
            public BiConsumer<ListMultimap<K, U>, T> accumulator() {
                return (c, v) -> c.put(keyMapper.apply(v), valueMapper.apply(v));
            }

            @Override
            public BinaryOperator<ListMultimap<K, U>> combiner() {
                return (c1, c2) -> {
                    c1.putAll(c2);
                    return c1;
                };
            }

            @Override
            public Function<ListMultimap<K, U>, ListMultimap<K, U>> finisher() {
                return c -> c;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.singleton(
                        Characteristics.IDENTITY_FINISH
                );
            }
        };
    }

}
