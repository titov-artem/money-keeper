package com.github.money.keeper.view.contoller.dto;

import java.time.LocalDate;
import java.util.Set;

public class PeriodReportForm {
    public LocalDate from;
    public LocalDate to;
    public Set<Integer> accountIds;
}
