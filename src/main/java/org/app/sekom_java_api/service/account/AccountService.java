package org.app.sekom_java_api.service.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.configuration.redis.CacheConfig;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.app.sekom_java_api.configuration.redis.CacheConfig.redisTemplate;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService{

    private final AccountRepository accountRepository;
    private final AccountHolderService accountHolderService;
    private final BankService bankService;
    private final ObjectMapper objectMapper;


    @Override
    public DataResult<List<Account>> getAllAccounts() {
        String regCached = (String) redisTemplate.opsForValue().get(CacheConfig.ACCOUNT_CACHE_KEY);
        if(regCached != null)
        {
            try{
                List<Account> accounts = objectMapper.readValue(regCached, new TypeReference<List<Account>>() {});
                if(accounts.isEmpty())
                    return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There are no accounts received from cache"));
                return new DataResult<>(accounts, Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Accounts are listed from cache successfully"));
            }
            catch(Exception e){
                return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Error while reading cache"));
            }
        }
        Optional<List<Account>> accounts = Optional.of(accountRepository.findAll());
        try{
            String regJson = objectMapper.writeValueAsString(accounts);
            redisTemplate.opsForValue().set(CacheConfig.ACCOUNT_CACHE_KEY, regJson, Duration.ofMinutes(10));
        }
        catch(Exception e){
            return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Error while reading cache"));
        }
        return new SuccessDataResult<>(accounts.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Accounts are listed successfully"));
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
    public DataResult<AccountDto> getAccountByAccountNumber(String accountNumber) {
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank())
            return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account number cannot be empty"));
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            return new SuccessDataResult<>(AccountDto.builder()
                    .accountName(account.get().getAccountName())
                    .accountBalance(account.get().getAccountBalance())
                    .accountHolder(account.get().getAccountHolder())
                    .bank(account.get().getBank())
                    .accountId(account.get().getAccountId())
                    .accountNumber(account.get().getAccountNumber())
                    .version(account.get().getVersion())
                    .build(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account"));
    }

    @Override
    public Result updateAccount(String accountNumber, String accountName) {
        if(accountName == null || accountName.isEmpty() || accountName.isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account name cannot be empty");
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account number cannot be empty");
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            account.get().setAccountName(accountName);
            accountRepository.save(account.get());
            redisTemplate.delete(CacheConfig.ACCOUNT_CACHE_KEY);
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is updated successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public Result deleteAccount(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            accountRepository.deleteById(accountId);
            redisTemplate.delete(CacheConfig.ACCOUNT_CACHE_KEY);
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is deleted successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account is not found");
    }

    @Override
    public Result saveAccount(AccountRequest accountRequest) {
        Result result = checkConstraints(accountRequest);
        if(result.resultMessage.messageType == ResultMessageType.ERROR)
            return result;

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
        redisTemplate.delete(CacheConfig.ACCOUNT_CACHE_KEY);
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is saved successfully");
    }

    @Override
    public DataResult<Long> getAccountBalance(String accountNumber) {
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank())
            return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account number cannot be empty"));
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            return new SuccessDataResult<>(account.get().getAccountBalance(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account balance is: " + account.get().getAccountBalance()));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account"));
    }

    @Override
    public Result updateBalance(String accountNumber, Long amount) {
        if(accountNumber == null || accountNumber.isEmpty() || accountNumber.isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account number cannot be empty");
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            account.get().setAccountBalance(amount);
            accountRepository.save(account.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Balance is updated successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account is not found");
    }

    private Result checkConstraints(AccountRequest request){
        if(request.getAccountName() == null || request.getAccountName().isEmpty() || request.getAccountName().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account name is empty");
        if(request.getBankId() == null || request.getBankId() == 0)
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Bank id is empty");
        if(request.getHolderId() == null || request.getHolderId() == 0)
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Holder id is empty");
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Constraints are checked successfully");
    }
}
