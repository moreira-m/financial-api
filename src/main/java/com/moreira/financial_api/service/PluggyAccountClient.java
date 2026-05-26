package com.moreira.financial_api.service;

import com.moreira.financial_api.service.dto.PluggyAccountPageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PluggyAccountClient {

    private final RestClient restClient;
    private final PluggyAuthService authService;
    private final String apiUrl;

    public PluggyAccountClient(
            RestClient restClient,
            PluggyAuthService authService,
            @Value("${pluggy.api-url}") String apiUrl) {
        this.restClient = restClient;
        this.authService = authService;
        this.apiUrl = apiUrl;
    }

    public PluggyAccountPageResponse fetchAccountsByItemId(String itemId) {
        String token = authService.getAccessToken();
        
        return restClient.get()
                .uri(apiUrl + "/accounts?itemId={itemId}", itemId)
                .header("X-API-KEY", token)
                .retrieve()
                .body(PluggyAccountPageResponse.class);
    }
}