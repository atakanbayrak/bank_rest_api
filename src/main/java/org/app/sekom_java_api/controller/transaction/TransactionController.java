package org.app.sekom_java_api.controller.transaction;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.transaction.TransactionDto;
import org.app.sekom_java_api.modal.entity.transaction.Transaction;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.service.transaction.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transaction/v1")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping(path = "getAllTransactions")
    public DataResult<List<Transaction>> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping(path = "getTransactionById")
    public DataResult<Transaction> getTransactionById(@RequestParam Long transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @GetMapping(path = "getTransactionByCode")
    public DataResult<TransactionDto> getTransactionByCode(@RequestParam String code) {
        return transactionService.getTransactionByCode(code);
    }

    @PostMapping(path = "withdraw")
    public Result withdraw(@RequestParam String accountNumber, @RequestParam Long amount) {
        return transactionService.withdraw(accountNumber, amount);
    }

    @PostMapping(path = "deposit")
    public Result deposit(@RequestParam String accountNumber, @RequestParam Long amount) {
        return transactionService.deposit(accountNumber, amount);
    }

    @PostMapping(path = "transfer")
    public Result transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam Long amount) {
        return transactionService.transfer(fromAccountNumber, toAccountNumber, amount);
    }

    @GetMapping(path = "getTransactionsByAccount")
    public DataResult<List<Transaction>> getTransactionsByAccount(@RequestParam Long accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }
}
