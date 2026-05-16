package com.moreira.financial_api.controller.dto;

import com.moreira.financial_api.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    String description,
    BigDecimal amount,
    LocalDate date,
    String accountName
) {
    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getDescription(),
            transaction.getAmount(),
            transaction.getDate(),
            transaction.getAccount().getName()
        );
    }
}
