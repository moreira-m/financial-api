package com.moreira.financial_api.controller;

import com.moreira.financial_api.controller.dto.DashboardSummaryResponse;
import com.moreira.financial_api.domain.Transaction;
import com.moreira.financial_api.repository.TransactionRepository;

import tools.jackson.databind.cfg.DateTimeFeature;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    private final TransactionRepository transactionRepository;

    public DashboardController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {


        List<Transaction> transactions = transactionRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);

        BigDecimal incomes = transactions.stream()
            .map(Transaction::getAmount)
            .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal expenses = transactions.stream()
            .map(Transaction::getAmount)
            .filter(amount -> amount.compareTo(BigDecimal.ZERO) < 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = incomes.add(expenses);

        return ResponseEntity.ok(new DashboardSummaryResponse(incomes, expenses, balance));
        }
}
