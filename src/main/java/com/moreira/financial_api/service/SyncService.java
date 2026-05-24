package com.moreira.financial_api.service;

import com.moreira.financial_api.domain.Account;
import com.moreira.financial_api.domain.Transaction;
import com.moreira.financial_api.repository.AccountRepository;
import com.moreira.financial_api.repository.TransactionRepository;
import com.moreira.financial_api.service.dto.PluggyTransactionDTO;
import com.moreira.financial_api.service.dto.PluggyTransactionPageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);
    private final PluggyTransactionClient transactionClient;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionCategorizer categorizer;

    public SyncService(PluggyTransactionClient transactionClient,
                        TransactionRepository transactionRepository,
                        AccountRepository accountRepository,
                        TransactionCategorizer categorizer) {
        this.transactionClient = transactionClient;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categorizer = categorizer;
    }

    @Transactional
    public void syncAccountTransactions(String pluggyAccountId) {
        Account account = accountRepository.findByPluggyAccountId(pluggyAccountId)
            .orElseThrow(() -> new RuntimeException("Conta nao encontrada para o ID " + pluggyAccountId));

        PluggyTransactionPageResponse response = transactionClient.fetchTransactions(pluggyAccountId);

        if (response == null || response.results() == null || response.results().isEmpty()) {
            return;
        }

        for (PluggyTransactionDTO dto : response.results()) {
            if (transactionRepository.existsByPluggyTransactionId(dto.id())) {
                continue;
            }

            try {
                Transaction newTransaction = new Transaction();
                newTransaction.setPluggyTransactionId(dto.id());
                newTransaction.setDescription(dto.description());
                newTransaction.setAmount(dto.amount());
                
                newTransaction.setType(categorizer.categorize(dto.description(), dto.amount()));

                try {
                    newTransaction.setDate(ZonedDateTime.parse(dto.date()).toLocalDate());
                } catch (Exception e) {
                    newTransaction.setDate(java.time.LocalDate.parse(dto.date().substring(0, 10)));
                }

                newTransaction.setAccount(account);
                transactionRepository.save(newTransaction);
            } catch (Exception e) {
                logger.error("Erro ao salvar transação: {}", e.getMessage());
            }
        }
    }
}
