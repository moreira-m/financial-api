package com.moreira.financial_api.controller.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
    BigDecimal totalIncomes,
    BigDecimal totalExpenses,
    BigDecimal balance
) {}
