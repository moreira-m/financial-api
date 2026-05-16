package com.moreira.financial_api.service;

import com.moreira.financial_api.service.dto.PluggyTransactionPageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PluggyTransactionClient {
    
    private final RestClient restClient;
    private final PluggyAuthService authService;
    private final String apiUrl;

    public PluggyTransactionClient(
            RestClient restClient,
            PluggyAuthService authService,
            @Value("${pluggy.api-url}") String apiUrl) {
        this.restClient = restClient;
        this.authService = authService;
        this.apiUrl = apiUrl;
    }

    public PluggyTransactionPageResponse fetchTransactions(String pluggyAccountId) {
        String token = authService.getAccessToken();

        return restClient.get()
                .uri(apiUrl + "/transactions?accountId=" + pluggyAccountId)
                .header("X-API-KEY", token)
                .retrieve()
                .body(PluggyTransactionPageResponse.class);
    }
}
