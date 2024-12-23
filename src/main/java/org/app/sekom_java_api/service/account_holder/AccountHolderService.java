package org.app.sekom_java_api.service.account_holder;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.configuration.redis.CacheConfig;

import org.app.sekom_java_api.modal.dto.account_holder.AccountHolderDto;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.request.AccountHolderRequest;
import org.app.sekom_java_api.repository.account_holder.AccountHolderRepository;
import org.app.sekom_java_api.results.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.app.sekom_java_api.configuration.redis.CacheConfig.redisTemplate;

@Service
@RequiredArgsConstructor
public class AccountHolderService implements IAccountHolderService {

    private final AccountHolderRepository accountHolderRepository;
    private final ObjectMapper objectMapper;


    @Override
    public DataResult<List<AccountHolder>> getAllAccountHolders() {
        String regCached = (String) redisTemplate.opsForValue().get(CacheConfig.HOLDER_CACHE_KEY);
        if(regCached != null)
        {
            try{
                List<AccountHolder> accountHolders = objectMapper.readValue(regCached, new TypeReference<List<AccountHolder>>() {});
                if(accountHolders.isEmpty())
                    return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account holders"));
                return new DataResult<>(accountHolders, Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account Holders are listed from cache successfully"));
            }
            catch(Exception e){
                return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Error while reading cache"));
            }
        }
        Optional<List<AccountHolder>> accountHolders = Optional.of(accountHolderRepository.findAll());
        try{
            String regJson = objectMapper.writeValueAsString(accountHolders);
            redisTemplate.opsForValue().set(CacheConfig.HOLDER_CACHE_KEY, regJson, Duration.ofMinutes(10));
        }
        catch(Exception e){
            return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Error while reading cache"));
        }
        return new SuccessDataResult<>(accountHolders.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holders are listed successfully"));
    }

    @Override
    public DataResult<AccountHolder> getAccountHolderById(Long holderId) {
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(holderId);
        if(accountHolder.isPresent()){
            return new SuccessDataResult<>(accountHolder.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account holder"));
    }

    @Override
    public DataResult<AccountHolderDto> getAccountHolderByIdentityNumber(String identityNumber) {
        if(identityNumber == null || identityNumber.isEmpty() || identityNumber.isBlank())
            return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Identity number cannot be empty"));
        Optional<AccountHolder> accountHolder = accountHolderRepository.findAccountHolderByIdentityNumber(identityNumber);
        if(accountHolder.isPresent()){
            return new SuccessDataResult<>(AccountHolderDto.builder()
                    .identityNumber(accountHolder.get().getIdentityNumber())
                    .accountHolderName(accountHolder.get().getAccountHolderName())
                    .accountHolderSurname(accountHolder.get().getAccountHolderSurname())
                    .accountHolderPhoneNumber(accountHolder.get().getAccountHolderPhoneNumber())
                    .accountHolderEmail(accountHolder.get().getAccountHolderEmail())
                    .accountHolderAddress(accountHolder.get().getAccountHolderAddress())
                    .build(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account holder"));
    }

    @Override
    public Result updateAccountHolder(Long holderId, AccountHolderRequest accountHolderRequest) {
        Result result = checkConstraints(accountHolderRequest);
        if(result.resultMessage.messageType == ResultMessageType.ERROR)
            return result;
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(holderId);
        if(accountHolder.isPresent()){
            accountHolder.get().setAccountHolderName(accountHolderRequest.getAccountHolderName());
            accountHolder.get().setAccountHolderAddress(accountHolderRequest.getAccountHolderAddress());
            accountHolder.get().setAccountHolderEmail(accountHolderRequest.getAccountHolderEmail());
            accountHolder.get().setAccountHolderPhoneNumber(accountHolderRequest.getAccountHolderPhoneNumber());
            accountHolder.get().setAccountHolderSurname(accountHolderRequest.getAccountHolderSurname());
            accountHolderRepository.save(accountHolder.get());
            redisTemplate.delete(CacheConfig.HOLDER_CACHE_KEY);
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is updated successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder is not found");
    }

    @Override
    public Result deleteAccountHolder(Long holderId) {
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(holderId);
        if(accountHolder.isPresent()){
            accountHolderRepository.delete(accountHolder.get());
            redisTemplate.delete(CacheConfig.HOLDER_CACHE_KEY);
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is deleted successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder is not found");
    }

    @Override
    @CacheEvict(value = CacheConfig.HOLDER_CACHE_KEY, allEntries = true)
    public Result saveAccountHolder(String identityNumber, AccountHolderRequest accountHolderRequest) {
        if(identityNumber == null || identityNumber.isEmpty() || identityNumber.isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Identity number cannot be empty");
        Result result = checkConstraints(accountHolderRequest);
        if(result.resultMessage.messageType == ResultMessageType.ERROR)
            return result;
        Optional<AccountHolder> accountHolder = accountHolderRepository.findAccountHolderByIdentityNumber(identityNumber);
        if(accountHolder.isPresent()){
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder is already exist");
        }
        AccountHolder newAccountHolder = AccountHolder.builder()
                .identityNumber(identityNumber)
                .accountHolderName(accountHolderRequest.getAccountHolderName())
                .accountHolderSurname(accountHolderRequest.getAccountHolderSurname())
                .accountHolderAddress(accountHolderRequest.getAccountHolderAddress())
                .accountHolderEmail(accountHolderRequest.getAccountHolderEmail())
                .accountHolderPhoneNumber(accountHolderRequest.getAccountHolderPhoneNumber())
                .creationTime(LocalDateTime.now())
                .build();
        accountHolderRepository.save(newAccountHolder);
        redisTemplate.delete(CacheConfig.HOLDER_CACHE_KEY);
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is saved successfully");
    }

    private Result checkConstraints(AccountHolderRequest accountHolderRequest){
        if(accountHolderRequest.getAccountHolderName() == null || accountHolderRequest.getAccountHolderName().isEmpty() || accountHolderRequest.getAccountHolderName().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder name is empty");
        if( accountHolderRequest.getAccountHolderSurname() == null || accountHolderRequest.getAccountHolderSurname().isEmpty() || accountHolderRequest.getAccountHolderSurname().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder surname is empty");
        if(accountHolderRequest.getAccountHolderAddress() == null || accountHolderRequest.getAccountHolderAddress().isEmpty() || accountHolderRequest.getAccountHolderAddress().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder address is empty");
        if(accountHolderRequest.getAccountHolderEmail() == null || accountHolderRequest.getAccountHolderEmail().isEmpty() || accountHolderRequest.getAccountHolderEmail().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder email is empty");
        if(accountHolderRequest.getAccountHolderPhoneNumber() == null || accountHolderRequest.getAccountHolderPhoneNumber().isEmpty() || accountHolderRequest.getAccountHolderPhoneNumber().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder phone number is empty");
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Constraints are checked successfully");
    }
}
