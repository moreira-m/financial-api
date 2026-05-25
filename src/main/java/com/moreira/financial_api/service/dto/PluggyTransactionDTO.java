package com.moreira.financial_api.service.dto;

import java.math.BigDecimal;

public record PluggyTransactionDTO(
        String id,
        String description,
        BigDecimal amount,
        String date,
        String category,
        String categoryId,
        String accountId
) {}
