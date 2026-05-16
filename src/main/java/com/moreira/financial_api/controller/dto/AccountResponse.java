package com.moreira.financial_api.controller.dto;

import com.moreira.financial_api.domain.Account;

public record AccountResponse(Long id, String name, String type) {
    //construtor que transforma a entidade do banco nesse DTO limpo
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getName(),
            account.getType()
        );
    }
}