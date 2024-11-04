package org.app.sekom_java_api.service.account;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.entity.account.Account;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.app.sekom_java_api.modal.request.AccountRequest;
import org.app.sekom_java_api.repository.account.AccountRepository;
import org.app.sekom_java_api.results.*;
import org.app.sekom_java_api.service.account_holder.AccountHolderService;
import org.app.sekom_java_api.service.bank.BankService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService{

    private final AccountRepository accountRepository;
    private final AccountHolderService accountHolderService;
    private final BankService bankService;


    @Override
    public DataResult<List<Account>> getAllAccounts() {
        Optional<List<Account>> accounts = Optional.of(accountRepository.findAll());
        if(accounts.isPresent()){
            return new SuccessDataResult<>(accounts.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Accounts are listed successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account"));
    }

    @Override
    public DataResult<Account> getAccountById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            return new SuccessDataResult<>(account.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account"));
    }

    @Override
    public DataResult<AccountDto> getAccountByAccountNumber(String username) {
        Optional<Account> account = accountRepository.findByAccountNumber(username);
        if(account.isPresent()){
            return new SuccessDataResult<>(AccountDto.builder()
                    .accountName(account.get().getAccountName())
                    .accountBalance(account.get().getAccountBalance())
                    .accountHolder(account.get().getAccountHolder())
                    .bank(account.get().getBank())
                    .accountId(account.get().getAccountId())
                    .accountNumber(account.get().getAccountNumber())
                    .build(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account"));
    }

    @Override
    public Result updateAccount(String accountNumber, String accountName) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            account.get().setAccountName(accountName);
            accountRepository.save(account.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is updated successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public Result deleteAccount(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            accountRepository.deleteById(accountId);
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is deleted successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public Result saveAccount(AccountRequest accountRequest) {
        AccountHolder accountHolder = accountHolderService.getAccountHolderById(accountRequest.getHolderId()).getData();
        if(accountHolder == null){
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder is not found");
        }

        Bank bank = bankService.getBankById(accountRequest.getBankId()).getData();
        if(bank == null){
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Bank is not found");
        }

        //Bankayı ve holderı ekle
        Account account = Account.builder()
                .accountName(accountRequest.getAccountName())
                .accountHolder(accountHolder)
                .accountBalance(0L)
                .accountNumber(randomUUID().toString())
                .creationTime(LocalDateTime.now())
                .bank(bank)
                .build();
        accountRepository.save(account);
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is saved successfully");
    }

    @Override
    public DataResult<Long> getAccountBalance(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            return new SuccessDataResult<>(account.get().getAccountBalance(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account balance is: " + account.get().getAccountBalance()));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account"));
    }

    @Override
    public Result updateBalance(String accountNumber, Long amount) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            account.get().setAccountBalance(amount);
            accountRepository.save(account.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Balance is updated successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account is not found");
    }
}
