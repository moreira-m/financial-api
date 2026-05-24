package com.moreira.financial_api.service;

import com.moreira.financial_api.domain.Account;
import com.moreira.financial_api.repository.AccountRepository;
import com.moreira.financial_api.service.dto.PluggyAccountDTO;
import com.moreira.financial_api.service.dto.PluggyAccountPageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final PluggyAccountClient accountClient;

    public AccountService(AccountRepository accountRepository, PluggyAccountClient accountClient) {
        this.accountRepository = accountRepository;
        this.accountClient = accountClient;
    }

    @Transactional
    public void importAccountsFromPluggy(String itemId) {
        logger.info("Iniciando importação de contas para o itemId: {}", itemId);
        
        PluggyAccountPageResponse response = accountClient.fetchAccountsByItemId(itemId);

        if (response == null || response.results() == null) {
            return;
        }

        for (PluggyAccountDTO dto : response.results()) {
            
            if (accountRepository.findByPluggyAccountId(dto.id()).isPresent()) {
                logger.info("Conta {} já existe no banco. Pulando...", dto.name());
                continue;
            }

            Account newAccount = Account.builder()
                    .name(dto.name())
                    .type(dto.type())
                    .pluggyAccountId(dto.id())
                    .build();
            
            accountRepository.save(newAccount);
            logger.info("Nova conta importada: {}", dto.name());
        }
    }
}
