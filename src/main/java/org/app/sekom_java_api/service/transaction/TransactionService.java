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
        if(code == null || code.isEmpty() || code.isBlank()){
            return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Transaction code is invalid"));
        }
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
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank()){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account number is invalid");
        }
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
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank()){
            throw new IllegalArgumentException("Account number is invalid");
        }
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
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank()){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account number is invalid");
        }
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber).getData();
        if(account != null){
            try {
                updateBalanceWithVersionCheck(accountNumber, account.getAccountBalance() + amount, account.getVersion());
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
            } catch (OptimisticLockingFailureException e) {
                return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Concurrent transaction detected, please try again");
            }
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    @Transactional
    public Result transfer(String fromAccountNumber, String toAccountNumber, Long amount) {
        if(fromAccountNumber == null || fromAccountNumber.isEmpty() || fromAccountNumber.isBlank()){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "From account number is invalid");
        }
        if(toAccountNumber == null || toAccountNumber.isEmpty() || toAccountNumber.isBlank()){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "To account number is invalid");
        }
        AccountDto fromAccount = accountService.getAccountByAccountNumber(fromAccountNumber).getData();
        AccountDto toAccount = accountService.getAccountByAccountNumber(toAccountNumber).getData();
        if(fromAccount != null && toAccount != null){
            if(fromAccount.getAccountBalance() >= amount){
                try{
                    updateBalanceWithVersionCheck(fromAccountNumber, fromAccount.getAccountBalance() - amount, fromAccount.getVersion());
                    updateBalanceWithVersionCheck(toAccountNumber, toAccount.getAccountBalance() + amount, toAccount.getVersion());
                    Result result = saveTransaction(TransactionRequest.builder()
                            .transactionType("Transfer")
                            .transactionAmount(amount)
                            .transactionDescription("Transfer from " + fromAccountNumber + " to " + toAccountNumber)
                            .accountId(fromAccount.getAccountId())
                            .build());

                    Result result2 = saveTransaction(TransactionRequest.builder()
                            .transactionType("Transfer")
                            .transactionAmount(amount)
                            .transactionDescription("Recieved transfer from " + fromAccountNumber + " to " + toAccountNumber)
                            .accountId(toAccount.getAccountId())
                            .build());
                    if(result.resultMessage.messageType != ResultMessageType.ERROR && result2.resultMessage.messageType != ResultMessageType.ERROR){
                        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transfer is successful");
                    }
                    return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transfer is not successful because of transaction error");
                } catch (OptimisticLockingFailureException e) {
                    return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Concurrent transaction detected, please try again");
                }
                //accountService.updateBalance(fromAccountNumber, fromAccount.getAccountBalance() - amount);
                //accountService.updateBalance(toAccountNumber, toAccount.getAccountBalance() + amount);
            }
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Insufficient balance");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public Result saveTransaction(TransactionRequest transactionRequest) {
        Result result = checkConstraints(transactionRequest);
        if(result.resultMessage.messageType == ResultMessageType.ERROR)
            return result;
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

    private Result checkConstraints(TransactionRequest transactionRequest){
        if(transactionRequest.getTransactionAmount() == null || transactionRequest.getTransactionAmount() <= 0){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Transaction amount is invalid");
        }
        if(transactionRequest.getTransactionType() == null || transactionRequest.getTransactionType().isEmpty() || transactionRequest.getTransactionType().isBlank()){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Transaction type is invalid");
        }
        if(transactionRequest.getTransactionDescription() == null || transactionRequest.getTransactionDescription().isEmpty() || transactionRequest.getTransactionDescription().isBlank()){
            return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "Transaction description is invalid");
        }
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Transaction is valid");
    }
}
