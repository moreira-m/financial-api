package com.moreira.financial_api.service;

import com.moreira.financial_api.domain.Account;
import com.moreira.financial_api.domain.Transaction;
import com.moreira.financial_api.repository.AccountRepository;
import com.moreira.financial_api.repository.TransactionRepository;
import com.moreira.financial_api.service.dto.PluggyTransactionDTO;
import com.moreira.financial_api.service.dto.PluggyTransactionPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SyncService {
    
    private final PluggyTransactionClient transactionClient;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public SyncService(PluggyTransactionClient transactionClient,
                        TransactionRepository transactionRepository,
                        AccountRepository accountRepository) {
        this.transactionClient = transactionClient;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void syncAccountTransactions(String pluggyAccountId) {
        //verifica se a conta existe no banco
        Account account = accountRepository.findByPluggyAccountId(pluggyAccountId)
            .orElseThrow(() -> new RuntimeException("Conta nao encontrada para o ID " + pluggyAccountId));

        //busca os dados atualizados
        PluggyTransactionPageResponse response = transactionClient.fetchTransactions(pluggyAccountId);

        if (response.results() == null || response.results().isEmpty()) {
            return;
        }

        //itera sobre a lista da pluggy e salva as transaçoes novas
        for (PluggyTransactionDTO dto : response.results()) {

            //verifica a idempotencia (se já existe pula para o proximo)
            if (transactionRepository.existsByPluggyTransactionId(dto.id())) {
                continue;
            }

            try {
                Transaction newTransaction = new Transaction();
                newTransaction.setPluggyTransactionId(dto.id());
                newTransaction.setDescription(dto.description());
                newTransaction.setAmount(dto.amount());
                
                //converte data no formato ISO
                newTransaction.setDate(ZonedDateTime.parse(dto.date()).toLocalDate());

                newTransaction.setAccount(account);

                transactionRepository.save(newTransaction);
            } catch (Exception e) {
                // Ignore unique constraint violation from parallel sync
                System.out.println("Erro ao salvar transação: " + e.getMessage());
            }
        }
    }
}
