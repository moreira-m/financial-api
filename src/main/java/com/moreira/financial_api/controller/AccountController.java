package com.moreira.financial_api.controller;

import com.moreira.financial_api.controller.dto.AccountResponse;
import com.moreira.financial_api.repository.AccountRepository;
import com.moreira.financial_api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AccountController(AccountRepository accountRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> listAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    @PostMapping("/import/{itemId}")
    public ResponseEntity<Void> importAccounts(@PathVariable String itemId) {
        accountService.importAccountsFromPluggy(itemId);
        return ResponseEntity.ok().build();
    }
}