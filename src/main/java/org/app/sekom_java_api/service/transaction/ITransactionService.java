package org.app.sekom_java_api.service.transaction;


import org.app.sekom_java_api.modal.dto.transaction.TransactionDto;
import org.app.sekom_java_api.modal.entity.transaction.Transaction;
import org.app.sekom_java_api.modal.request.TransactionRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;

import java.util.List;

public interface ITransactionService {

    DataResult<List<Transaction>> getAllTransactions();
    DataResult<Transaction> getTransactionById(Long transactionId);
    DataResult<TransactionDto> getTransactionByCode(String code);
    Result withdraw(String accountNumber, Long amount);
    Result deposit(String accountNumber, Long amount);
    Result transfer(String fromAccountNumber, String toAccountNumber, Long amount);
    Result saveTransaction(TransactionRequest transactionRequest);
    DataResult<List<Transaction>> getTransactionsByAccount(Long accountId);



}
