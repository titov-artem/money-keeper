package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.service.CategorizationHelper;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Artem Titov
 */
public class PerMonthCategoryChart {

    private SortedMap<LocalDate, Map<String, BigDecimal>> absoluteChart;
    private SortedMap<LocalDate, Map<String, BigDecimal>> percentageChart;

    public PerMonthCategoryChart(SortedMap<LocalDate, Map<String, BigDecimal>> absoluteChart, SortedMap<LocalDate, Map<String, BigDecimal>> percentageChart) {
        this.absoluteChart = absoluteChart;
        this.percentageChart = percentageChart;
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
        public static final String DEFAULT_TOTAL_CHART_NAME = "TOTAL";

        private String totalChartName = DEFAULT_TOTAL_CHART_NAME;
        private final CategorizationHelper categorizationHelper;
        private final SortedMap<LocalDate, Map<String, BigDecimal>> chart = Maps.newTreeMap();

        public Builder(CategorizationHelper categorizationHelper) {
            // todo do something if category name clashes with DEFAULT_TOTAL_CHART_NAME
            this.categorizationHelper = categorizationHelper;
        }

        public void append(UnifiedTransaction transaction) {
            Category category = categorizationHelper.determineCategory(transaction);
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
            return new PerMonthCategoryChart(chart, percentageChart);
        }

    }


}
