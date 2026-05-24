package com.moreira.financial_api.service.dto;

import com.moreira.financial_api.domain.AccountType;

public record PluggyAccountDTO(
    String id,
    String name,
    AccountType type,
    String currencyCode
) {}
