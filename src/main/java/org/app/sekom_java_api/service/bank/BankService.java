package org.app.sekom_java_api.service.bank;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.bank.BankDto;
import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.app.sekom_java_api.modal.request.BankRequest;
import org.app.sekom_java_api.repository.bank.BankRepository;
import org.app.sekom_java_api.results.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class BankService implements IBankService {
    private final BankRepository bankRepository;

    @Override
    public DataResult<List<Bank>> getAllBanks() {
        Optional<List<Bank>> banks = Optional.of(bankRepository.findAll());
        if(banks.isPresent()){
            return new SuccessDataResult<>(banks.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Banks are listed successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no bank"));
    }

    @Override
    public DataResult<Bank> getBankById(Long bankId) {
        Optional<Bank> bank = bankRepository.findById(bankId);
        if(bank.isPresent()){
            return new SuccessDataResult<>(bank.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Bank is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no bank"));
    }

    @Override
    public DataResult<BankDto> getBankByCode(String bankCode) {
        Optional<Bank> bank = bankRepository.findBankByBankCode(bankCode);
        if(bank.isPresent()){
            return new SuccessDataResult<>(BankDto.builder()
                    .bankName(bank.get().getBankName())
                    .bankAddress(bank.get().getBankAddress())
                    .bankPhoneNumber(bank.get().getBankPhoneNumber())
                    .bankCode(bank.get().getBankCode())
                    .build(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Bank is found successfully"));
        }
        return new ErrorDataResult<>(Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no bank"));
    }

    @Override
    public Result updateBank(Long bankId, BankRequest bankRequest) {
        Optional<Bank> bank = bankRepository.findById(bankId);
        if(bank.isPresent()){
            bank.get().setBankName(bankRequest.getBankName());
            bank.get().setBankAddress(bankRequest.getBankAddress());
            bank.get().setBankPhoneNumber(bankRequest.getBankPhoneNumber());
            bankRepository.save(bank.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Bank is updated successfully");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no bank");
    }

    @Override
    public Result deleteBank(Long bankId) {
        Optional<Bank> bank = bankRepository.findById(bankId);
        if(bank.isPresent()){
            bankRepository.delete(bank.get());
            return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Bank is deleted successfully");
        }
        return Result.showMessage(Result.SUCCESS_EMPTY, ResultMessageType.ERROR, "There is no bank");
    }

    @Override
    public Result saveBank(BankRequest bankRequest) {
        Bank bank = Bank.builder()
                .bankName(bankRequest.getBankName())
                .bankCode(randomUUID().toString())
                .bankAddress(bankRequest.getBankAddress())
                .bankPhoneNumber(bankRequest.getBankPhoneNumber())
                .bankCreationTime(LocalDateTime.now())
                .build();
        bankRepository.save(bank);
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Bank is saved successfully");
    }
}
