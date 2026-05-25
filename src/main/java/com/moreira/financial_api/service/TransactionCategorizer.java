package com.moreira.financial_api.service;

import com.moreira.financial_api.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionCategorizer {

    public TransactionType categorize(String pluggyCategory, String description, BigDecimal amount) {
        if (pluggyCategory == null) {
            return fallbackCategorize(description, amount);
        }

        String cat = pluggyCategory.toLowerCase();

        if (cat.contains("transfer") || cat.contains("transferência") || cat.contains("internal")) {
            return TransactionType.TRANSFER;
        }

        if (cat.contains("invest") || cat.contains("aplicação") || cat.contains("loan")) {
            return TransactionType.INVESTMENT;
        }

        return amount.compareTo(BigDecimal.ZERO) >= 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
    }

    private TransactionType fallbackCategorize(String description, BigDecimal amount) {
        String desc = description.toLowerCase();
        
        if (desc.contains("invest") || desc.contains("cdb")) return TransactionType.INVESTMENT;
        if (desc.contains("transfer") || desc.contains("mesma titularidade")) return TransactionType.TRANSFER;
        
        return amount.compareTo(BigDecimal.ZERO) >= 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
    }
}
