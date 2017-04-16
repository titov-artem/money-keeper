package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.*;

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

    @JsonGetter
    public SortedMap<LocalDate, Map<String, BigDecimal>> getAbsolutes() {
        return absoluteChart;
    }

    @JsonGetter
    public SortedMap<LocalDate, Map<String, BigDecimal>> getPercentages() {
        return percentageChart;
    }

    public static final class Builder {
        private static final Logger log = LoggerFactory.getLogger(Builder.class);

        public static final String DEFAULT_TOTAL_CHART_NAME = "TOTAL";
        public static final Long TOTAL_CATEGORY_ID = -1L;
        public static final String DEFAULT_OTHER_CHART_NAME = "OTHER";
        public static final Long OTHER_CATEGORY_ID = -2L;
        public static final BigDecimal CATEGORY_CHART_REDUCE_THRESHOLD = BigDecimal.valueOf(5000);

        private String totalChartName = DEFAULT_TOTAL_CHART_NAME;
        private String otherChartName = DEFAULT_OTHER_CHART_NAME;
        private final SortedMap<LocalDate, Map<Long, BigDecimal>> chart = new TreeMap<>();
        private final Set<Long> accountIds = new HashSet<>();

        private final AccountRepo accountRepo;
        private final CategoryRepo categoryRepo;

        public Builder(AccountRepo accountRepo, CategoryRepo categoryRepo) {
            // todo do something if category name clashes with DEFAULT_TOTAL_CHART_NAME
            this.accountRepo = accountRepo;
            this.categoryRepo = categoryRepo;
        }

        public void append(UnifiedTransaction transaction) {
            accountIds.add(transaction.getAccountId());
            LocalDate month = getMonth(transaction.getDate());
            updateAmount(transaction.getStore().getCategoryId(), getMonthBucket(month), transaction.getAmount());
            updateAmount(TOTAL_CATEGORY_ID, getMonthBucket(month), transaction.getAmount());
        }

        private Map<Long, BigDecimal> getMonthBucket(LocalDate monthStart) {
            return chart.computeIfAbsent(monthStart, k -> Maps.newHashMap());
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
                    accountRepo.get(accountIds).stream().map(Account::getName).collect(toSet())
            );
        }

        private SortedMap<LocalDate, Map<String, BigDecimal>> reduceChart(SortedMap<LocalDate, Map<Long, BigDecimal>> chart) {
            // get whole chart's categories list
            Set<Long> categoriesToReduce = chart.values().stream()
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
                    .forEach(categoryIdToAmount -> {
                        BigDecimal reducedAmount = BigDecimal.ZERO;
                        for (final Long categoryId : categoriesToReduce) {
                            BigDecimal amount = categoryIdToAmount.remove(categoryId);
                            if (amount == null) continue;
                            reducedAmount = reducedAmount.add(amount);
                        }
                        categoryIdToAmount.put(OTHER_CATEGORY_ID, reducedAmount);
                    });

            return replaceCategoryIdsOnNames(chart);
        }

        private SortedMap<LocalDate, Map<String, BigDecimal>> replaceCategoryIdsOnNames(SortedMap<LocalDate, Map<Long, BigDecimal>> chart) {
            Set<Long> categoryIds = chart.values().stream()
                    .flatMap(m -> m.keySet().stream())
                    .collect(toSet());
            Map<Long, String> categoryNameById = categoryRepo.get(categoryIds).stream()
                    .collect(toMap(Category::getId, Category::getName));
            categoryNameById.put(TOTAL_CATEGORY_ID, totalChartName);
            categoryNameById.put(OTHER_CATEGORY_ID, otherChartName);
            SortedMap<LocalDate, Map<String, BigDecimal>> out = new TreeMap<>();
            chart.forEach((month, categoryIdToAmount) -> {
                Map<String, BigDecimal> categoryNameToAmount = new HashMap<>();
                categoryIdToAmount.forEach((id, amount) -> categoryNameToAmount.put(categoryNameById.get(id), amount));
                out.put(month, categoryNameToAmount);
            });
            return out;
        }

    }


}
