package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.storage.AccountRepo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Artem Titov
 */
public class PerMonthCategoryChart {

    private final SortedMap<LocalDate, Map<String, BigDecimal>> absoluteChart;
    private final SortedMap<LocalDate, Map<String, BigDecimal>> percentageChart;
    private final Set<String> accountNames;

    public PerMonthCategoryChart(SortedMap<LocalDate, Map<String, BigDecimal>> absoluteChart,
                                 SortedMap<LocalDate, Map<String, BigDecimal>> percentageChart,
                                 Set<String> accountNames) {
        this.absoluteChart = absoluteChart;
        this.percentageChart = percentageChart;
        this.accountNames = accountNames;
        fillMissedWithZero(this.absoluteChart);
        fillMissedWithZero(this.percentageChart);
    }

    private void fillMissedWithZero(SortedMap<LocalDate, Map<String, BigDecimal>> chart) {
        List<String> labels = getLabels(chart);
        for (final Map.Entry<LocalDate, Map<String, BigDecimal>> entry : chart.entrySet()) {
            for (final String label : labels) {
                if (!entry.getValue().containsKey(label)) {
                    entry.getValue().put(label, BigDecimal.ZERO);
                }
            }
        }
    }

    @JsonGetter
    public List<Map<String, Object>> getAbsoluteChart() {
        return transformToChart(absoluteChart);
    }

    @JsonGetter
    public List<String> getAbsoluteChartLabels() {
        return getLabels(absoluteChart);
    }

    @JsonGetter
    public List<Map<String, Object>> getPercentageChart() {
        return transformToChart(percentageChart);
    }

    @JsonGetter
    public List<String> getPercentageChartLabels() {
        return getLabels(percentageChart);
    }

    @JsonGetter
    public Set<String> getAccounts() {
        return accountNames;
    }

    private static List<Map<String, Object>> transformToChart(SortedMap<LocalDate, Map<String, BigDecimal>> source) {
        return source.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = Maps.newHashMap();
                    e.getValue().forEach((k, v) -> map.put(k, v));
                    map.put("date", e.getKey());
                    return map;
                })
                .collect(toList());
    }

    private List<String> getLabels(SortedMap<LocalDate, Map<String, BigDecimal>> source) {
        return source.values().stream()
                .flatMap(v -> v.keySet().stream())
                .collect(toSet())
                .stream()
                .sorted()
                .collect(toList());
    }

    public SortedMap<LocalDate, Map<String, BigDecimal>> getAbsolutes() {
        return absoluteChart;
    }

    public SortedMap<LocalDate, Map<String, BigDecimal>> getPercentages() {
        return percentageChart;
    }

    public static final class Builder {
        private static final Logger log = LoggerFactory.getLogger(Builder.class);

        public static final String DEFAULT_TOTAL_CHART_NAME = "TOTAL";
        public static final String DEFAULT_OTHER_CHART_NAME = "OTHER";
        public static final BigDecimal CATEGORY_CHART_REDUCE_THRESHOLD = BigDecimal.valueOf(5000);

        private String totalChartName = DEFAULT_TOTAL_CHART_NAME;
        private String otherChartName = DEFAULT_OTHER_CHART_NAME;
        private final SortedMap<LocalDate, Map<String, BigDecimal>> chart = new TreeMap<>();
        private final Set<Integer> accountIds = new HashSet<>();
        private final CategorizationHelper categorizationHelper;
        private final AccountRepo accountRepo;

        public Builder(CategorizationHelper categorizationHelper, AccountRepo accountRepo) {
            // todo do something if category name clashes with DEFAULT_TOTAL_CHART_NAME
            this.categorizationHelper = categorizationHelper;
            this.accountRepo = accountRepo;
        }

        public void append(UnifiedTransaction transaction) {
            Category category = categorizationHelper.determineCategory(transaction.getStore());
            accountIds.add(transaction.getAccountId());
            LocalDate month = getMonth(transaction.getDate());
            updateAmount(category.getName(), getMonthBucket(month), transaction.getAmount());
            updateAmount(totalChartName, getMonthBucket(month), transaction.getAmount());
        }

        private Map<String, BigDecimal> getMonthBucket(LocalDate monthStart) {
            Map<String, BigDecimal> bucket = chart.get(monthStart);
            if (bucket == null) {
                bucket = Maps.newHashMap();
                chart.put(monthStart, bucket);
            }
            return bucket;
        }

        private <T> void updateAmount(T key, Map<T, BigDecimal> source, BigDecimal delta) {
            BigDecimal curValue = source.get(key);
            BigDecimal newValue = delta;
            if (curValue != null) newValue = newValue.add(curValue);
            source.put(key, newValue);
        }

        private LocalDate getMonth(LocalDate date) {
            return date.withDayOfMonth(1);
        }

        public PerMonthCategoryChart build() {
            SortedMap<LocalDate, Map<String, BigDecimal>> chart = reduceChart(this.chart);
            SortedMap<LocalDate, Map<String, BigDecimal>> percentageChart = Maps.newTreeMap();
            for (Map.Entry<LocalDate, Map<String, BigDecimal>> entry : chart.entrySet()) {
                BigDecimal total = entry.getValue().get(totalChartName);
                Map<String, BigDecimal> monthValues = Maps.newHashMap();
                for (Map.Entry<String, BigDecimal> monthEntry : entry.getValue().entrySet()) {
                    if (totalChartName.equals(monthEntry.getKey())) continue;
                    monthValues.put(
                            monthEntry.getKey(),
                            monthEntry.getValue().divide(total, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                    );
                }
                percentageChart.put(entry.getKey(), monthValues);
            }
            return new PerMonthCategoryChart(
                    chart,
                    percentageChart,
                    accountRepo.load(accountIds).stream().map(Account::getName).collect(toSet())
            );
        }

        private SortedMap<LocalDate, Map<String, BigDecimal>> reduceChart(SortedMap<LocalDate, Map<String, BigDecimal>> chart) {
            // get whole chart's categories list
            Set<String> categoriesToReduce = chart.values().stream()
                    .flatMap(m -> m.keySet().stream())
                    .collect(toSet());
            // now we will remove categories which has at least CATEGORY_CHART_REDUCE_THRESHOLD in some month
            chart.values().stream()
                    .flatMap(m -> m.entrySet().stream())
                    .forEach(e -> {
                        if (e.getValue().compareTo(CATEGORY_CHART_REDUCE_THRESHOLD) > 0) {
                            categoriesToReduce.remove(e.getKey());
                        }
                    });
            log.info("Will be reduced {} categories: {}", categoriesToReduce.size(), categoriesToReduce);
            chart.values().stream()
                    .forEach(categoryToAmount -> {
                        BigDecimal reducedAmount = BigDecimal.ZERO;
                        for (final String category : categoriesToReduce) {
                            BigDecimal amount = categoryToAmount.remove(category);
                            if (amount == null) continue;
                            reducedAmount = reducedAmount.add(amount);
                        }
                        categoryToAmount.put(otherChartName, reducedAmount);
                    });
            return chart;
        }

    }


}
