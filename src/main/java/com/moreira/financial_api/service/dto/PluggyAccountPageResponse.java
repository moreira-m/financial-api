package com.moreira.financial_api.service.dto;

import java.util.List;

public record PluggyAccountPageResponse(
    List<PluggyAccountDTO> results
) {}