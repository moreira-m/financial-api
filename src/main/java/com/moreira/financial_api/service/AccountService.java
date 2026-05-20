package com.moreira.financial_api.service;

import com.moreira.financial_api.domain.Account;
import com.moreira.financial_api.repository.AccountRepository;
import com.moreira.financial_api.service.dto.PluggyAccountDTO;
import com.moreira.financial_api.service.dto.PluggyAccountPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PluggyAccountClient accountClient;

    public AccountService(AccountRepository accountRepository, PluggyAccountClient accountClient) {
        this.accountRepository = accountRepository;
        this.accountClient = accountClient;
    }

    @Transactional
    public void importAccountsFromPluggy(String itemId) {
        PluggyAccountPageResponse response = accountClient.fetchAccountsByItemId(itemId);

        if (response.results() == null || response.results().isEmpty()) {
            return;
        }

        for (PluggyAccountDTO dto : response.results()) {
            if (accountRepository.findByPluggyAccountId(dto.id()).isPresent()) {
                continue;
            }

            Account newAccount = new Account();
            newAccount.setName(dto.name());
            newAccount.setType(dto.type());
            newAccount.setPluggyAccountId(dto.id());
            
            accountRepository.save(newAccount);
        }
    }
}