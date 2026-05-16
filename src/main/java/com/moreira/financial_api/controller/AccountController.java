package com.moreira.financial_api.controller;

import com.moreira.financial_api.controller.dto.AccountResponse;
import com.moreira.financial_api.domain.Account;
import com.moreira.financial_api.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listAccounts() {

        List<AccountResponse> accounts = accountRepository.findAll()
            .stream()
            .map(AccountResponse::fromEntity)
            .toList();

        return ResponseEntity.ok(accounts);
    }
}
