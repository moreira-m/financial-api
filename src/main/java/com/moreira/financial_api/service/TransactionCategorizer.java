package com.moreira.financial_api.service;

import com.moreira.financial_api.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionCategorizer {

    public TransactionType categorize(String description, BigDecimal amount) {
        String desc = description.toLowerCase();

        if (desc.contains("investimento") || desc.contains("aplicação") || desc.contains("aplicacao") || 
            desc.contains("cdb") || desc.contains("tesouro") || desc.contains("resgate")) {
            return TransactionType.INVESTMENT;
        }

        if (desc.contains("transferência entre contas") || desc.contains("transferencia entre contas") || 
            desc.contains("pix enviado") && desc.contains("mesma titularidade") || 
            desc.contains("pix recebido") && desc.contains("mesma titularidade")) {
            return TransactionType.TRANSFER;
        }

        return amount.compareTo(BigDecimal.ZERO) >= 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
    }
}
