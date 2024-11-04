package org.app.sekom_java_api.service.account_holder;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.dto.account_holder.AccountHolderDto;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.request.AccountHolderRequest;
import org.app.sekom_java_api.repository.account_holder.AccountHolderRepository;
import org.app.sekom_java_api.results.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountHolderService implements IAccountHolderService {

    private final AccountHolderRepository accountHolderRepository;

    @Override
    public DataResult<List<AccountHolder>> getAllAccountHolders() {
        Optional<List<AccountHolder>> accountHolders = Optional.of(accountHolderRepository.findAll());
        if(accountHolders.isPresent()){
            return new SuccessDataResult<>(accountHolders.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holders are listed successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no account holder"));
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
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(holderId);
        if(accountHolder.isPresent()){
            accountHolder.get().setAccountHolderName(accountHolderRequest.getAccountHolderName());
            accountHolder.get().setAccountHolderAddress(accountHolderRequest.getAccountHolderAddress());
            accountHolder.get().setAccountHolderEmail(accountHolderRequest.getAccountHolderEmail());
            accountHolder.get().setAccountHolderPhoneNumber(accountHolderRequest.getAccountHolderPhoneNumber());
            accountHolder.get().setAccountHolderSurname(accountHolderRequest.getAccountHolderSurname());
            accountHolderRepository.save(accountHolder.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is updated successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder is not found");
    }

    @Override
    public Result deleteAccountHolder(Long holderId) {
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(holderId);
        if(accountHolder.isPresent()){
            accountHolderRepository.delete(accountHolder.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is deleted successfully");
        }
        return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Account holder is not found");
    }

    @Override
    public Result saveAccountHolder(String identityNumber, AccountHolderRequest accountHolderRequest) {
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
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account holder is saved successfully");
    }
}
