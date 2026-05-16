package com.moreira.financial_api.service.dto;

import java.util.List;

public record PluggyTransactionPageResponse(
        List<PluggyTransactionDTO> results,
        Integer total,
        Integer totalPages,
        Integer page
) {}