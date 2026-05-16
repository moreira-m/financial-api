package com.moreira.financial_api.controller;

import com.moreira.financial_api.controller.dto.TransactionResponse;
import com.moreira.financial_api.repository.TransactionRepository;
import com.moreira.financial_api.service.SyncService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final SyncService syncService;

    public TransactionController(TransactionRepository transactionRepository, SyncService syncService) {
        this.transactionRepository = transactionRepository;
        this.syncService = syncService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<TransactionResponse> transactions = transactionRepository
            .findByDateBetweenOrderByDateDesc(startDate, endDate)
            .stream()
            .map(TransactionResponse::fromEntity)
            .toList();

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/sync/{pluggyAccountId}")
    public ResponseEntity<String> syncTransactions(@PathVariable String pluggyAccountId) {
        syncService.syncAccountTransactions(pluggyAccountId);
        return ResponseEntity.ok("Sincronizaçao concluída com sucesso");
    }
}