package com.moreira.financial_api.service;

import com.moreira.financial_api.service.dto.PluggyAuthRequest;
import com.moreira.financial_api.service.dto.PluggyAuthResponse;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PluggyAuthService {
    
    private final RestClient restClient;
    private final String apiUrl;
    private final String clientId;
    private final String clientSecret;

    public PluggyAuthService(
            RestClient restClient,
            @Value("${pluggy.api-url}") String apiUrl,
            @Value("${pluggy.client-id}") String clientId,
            @Value("${pluggy.client-secret}") String clientSecret) {
        this.restClient = restClient;
        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        PluggyAuthRequest requestBody = new PluggyAuthRequest(clientId, clientSecret);
        PluggyAuthResponse response = restClient.post()
                .uri(apiUrl + "/auth")
                .body(requestBody)
                .retrieve()
                .body(PluggyAuthResponse.class);

            if (response == null || response.apiKey() == null) {
                throw new RuntimeException("Falha ao obter token de autenticaçao");
            }

            return response.apiKey();
    }
}
