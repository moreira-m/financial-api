package com.moreira.financial_api.controller.dto;

import com.moreira.financial_api.domain.Account;

public record AccountResponse(Long id, String name, String type, String pluggyAccountId) {
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getName(),
            account.getType() != null ? account.getType().name() : null,
            account.getPluggyAccountId()
        );
    }
}
