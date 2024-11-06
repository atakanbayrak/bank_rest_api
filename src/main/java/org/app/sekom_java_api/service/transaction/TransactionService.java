package org.app.sekom_java_api.service.transaction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.dto.transaction.TransactionDto;
import org.app.sekom_java_api.modal.entity.account.Account;
import org.app.sekom_java_api.modal.entity.transaction.Transaction;
import org.app.sekom_java_api.modal.request.TransactionRequest;
import org.app.sekom_java_api.repository.transaction.TransactionRepository;
import org.app.sekom_java_api.results.*;
import org.app.sekom_java_api.service.account.AccountService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService{
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;


    @Override
    public DataResult<List<Transaction>> getAllTransactions() {
        Optional<List<Transaction>> transactions = Optional.of(transactionRepository.findAll());
        if(transactions.isPresent()){
            return new SuccessDataResult<>(transactions.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transactions are listed successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no transaction"));
    }

    @Override
    public DataResult<Transaction> getTransactionById(Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if(transaction.isPresent()){
            return new SuccessDataResult<>(transaction.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transaction is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no transaction"));
    }

    @Override
    public DataResult<TransactionDto> getTransactionByCode(String code) {
        Optional<Transaction> transaction = transactionRepository.findTransactionByTransactionCode(code);
        if(transaction.isPresent()){
            return new SuccessDataResult<>(TransactionDto.builder()
                    .transactionType(transaction.get().getTransactionType())
                    .transactionAmount(transaction.get().getTransactionAmount())
                    .transactionTime(transaction.get().getTransactionTime())
                    .transactionDescription(transaction.get().getTransactionDescription())
                    .build(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transaction is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no transaction"));
    }

    @Override
    @Transactional
    public Result withdraw(String accountNumber, Long amount) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber).getData();
        if(account != null){
            if(account.getAccountBalance() >= amount){
                try{
                    updateBalanceWithVersionCheck(accountNumber, account.getAccountBalance() - amount, account.getVersion());

                    //accountService.updateBalance(accountNumber, account.getAccountBalance() - amount);
                    Result result = saveTransaction(TransactionRequest.builder()
                            .transactionType("Withdraw")
                            .transactionAmount(amount)
                            .transactionDescription("Withdraw from " + accountNumber)
                            .accountId(account.getAccountId())
                            .build());
                    if(result.resultMessage.messageType != ResultMessageType.ERROR){
                        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Withdraw is successful");
                    }
                    return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Withdraw is not successful");
                } catch (OptimisticLockingFailureException e) {
                    return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Concurrent transaction detected, please try again");
                }
            }
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Insufficient balance");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found");
    }

    public void updateBalanceWithVersionCheck(String accountNumber, Long newBalance, Long version) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber).getData();

        if(account != null && account.getVersion().equals(version)) {
            accountService.updateBalance(accountNumber, newBalance); // JPA burada otomatik olarak version kontrolü yapar
        } else {
            throw new OptimisticLockingFailureException("Version conflict detected");
        }
    }


    @Override
    @Transactional
    public Result deposit(String accountNumber, Long amount) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber).getData();
        if(account != null){
            accountService.updateBalance(accountNumber, account.getAccountBalance() + amount);
            Result result = saveTransaction(TransactionRequest.builder()
                    .transactionType("Deposit")
                    .transactionAmount(amount)
                    .transactionDescription("Deposit to " + accountNumber)
                    .accountId(account.getAccountId())
                    .build());
            if(result.resultMessage.messageType != ResultMessageType.ERROR){
                return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Deposit is successful");
            }
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Deposit is not successful because of transaction error");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    @Transactional
    public Result transfer(String fromAccountNumber, String toAccountNumber, Long amount) {
        AccountDto fromAccount = accountService.getAccountByAccountNumber(fromAccountNumber).getData();
        AccountDto toAccount = accountService.getAccountByAccountNumber(toAccountNumber).getData();
        if(fromAccount != null && toAccount != null){
            if(fromAccount.getAccountBalance() >= amount){
                accountService.updateBalance(fromAccountNumber, fromAccount.getAccountBalance() - amount);
                accountService.updateBalance(toAccountNumber, toAccount.getAccountBalance() + amount);
                Result result = saveTransaction(TransactionRequest.builder()
                        .transactionType("Transfer")
                        .transactionAmount(amount)
                        .transactionDescription("Transfer from " + fromAccountNumber + " to " + toAccountNumber)
                        .accountId(fromAccount.getAccountId())
                        .build());
                if(result.resultMessage.messageType != ResultMessageType.ERROR){
                    return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transfer is successful");
                }
                return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transfer is not successful because of transaction error");
            }
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Insufficient balance");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public Result saveTransaction(TransactionRequest transactionRequest) {
        Optional<Account> account = Optional.ofNullable(accountService.getAccountById(transactionRequest.getAccountId()).getData());
        if(account.isPresent()){
            Transaction transaction = Transaction.builder()
                    .transactionType(transactionRequest.getTransactionType())
                    .transactionAmount(transactionRequest.getTransactionAmount())
                    .transactionTime(LocalDateTime.now())
                    .transactionDescription(transactionRequest.getTransactionDescription())
                    .transactionCode(randomUUID().toString())
                    .transactionStatus("Success")
                    .account(account.get())
                    .build();
            transactionRepository.save(transaction);
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transaction is saved successfully");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public DataResult<List<Transaction>> getTransactionsByAccount(Long accountId) {
        Optional<Account> account = Optional.ofNullable(accountService.getAccountById(accountId).getData());
        if(account.isPresent()){
            Optional<List<Transaction>> transactions = transactionRepository.findTransactionsByAccount(account.get());
            if(transactions.isPresent()){
                return new SuccessDataResult<>(transactions.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transactions are listed successfully"));
            }
            return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no transaction"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found"));
    }
}
