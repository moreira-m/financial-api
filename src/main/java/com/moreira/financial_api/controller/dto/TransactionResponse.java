package com.moreira.financial_api.controller.dto;

import com.moreira.financial_api.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    String description,
    BigDecimal amount,
    LocalDate date,
    String accountName,
    Long categoryId,
    String categoryName,
    String categoryColor
) {
    public static TransactionResponse fromEntity(Transaction transaction) {
        BigDecimal displayAmount = transaction.getAmount();
        String accType = transaction.getAccount().getType();
        if ("CREDIT_CARD".equals(accType) || "CREDIT".equals(accType)) {
            displayAmount = displayAmount.negate();
        }

        return new TransactionResponse(
            transaction.getId(),
            transaction.getDescription(),
            displayAmount,
            transaction.getDate(),
            transaction.getAccount().getName(),
            transaction.getCategory() != null ? transaction.getCategory().getId() : null,
            transaction.getCategory() != null ? transaction.getCategory().getName() : null,
            transaction.getCategory() != null ? transaction.getCategory().getColor() : null
        );
    }
}
