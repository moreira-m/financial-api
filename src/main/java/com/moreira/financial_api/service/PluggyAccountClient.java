package com.moreira.financial_api.service;

import com.moreira.financial_api.service.dto.PluggyAccountPageResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PluggyAccountClient {

    private final RestClient restClient;

    public PluggyAccountClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PluggyAccountPageResponse fetchAccountsByItemId(String itemId) {
        return restClient.get()
                .uri("/accounts?itemId={itemId}", itemId)
                .retrieve()
                .body(PluggyAccountPageResponse.class);
    }
}