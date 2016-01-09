package com.github.money.keeper.model.report;

import com.github.money.keeper.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author Artem Titov
 */
public class PerMonthCategoryChart {

    private SortedMap<LocalDate, Map<Category, BigDecimal>> chart;


}
