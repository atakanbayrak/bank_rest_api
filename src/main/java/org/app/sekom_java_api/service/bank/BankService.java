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
        return new SuccessDataResult<>(banks.get(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Banks are listed successfully"));
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
        if(bankCode == null || bankCode.isEmpty() || bankCode.isBlank())
            return new ErrorDataResult<>(Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Bank code cannot be empty"));
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
        Result result = checkConstraints(bankRequest);
        if(result.resultMessage.messageType == ResultMessageType.ERROR)
            return result;
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
        Result result = checkConstraints(bankRequest);
        if(result.resultMessage.messageType == ResultMessageType.ERROR)
            return result;
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

    private Result checkConstraints(BankRequest bankRequest) {
        if(bankRequest.getBankName() == null || bankRequest.getBankName().isEmpty() || bankRequest.getBankName().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Bank name cannot be empty");
        if(bankRequest.getBankAddress() == null || bankRequest.getBankAddress().isEmpty() || bankRequest.getBankAddress().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Bank address cannot be empty");
        if(bankRequest.getBankPhoneNumber() == null || bankRequest.getBankPhoneNumber().isEmpty() || bankRequest.getBankPhoneNumber().isBlank())
            return Result.showMessage(Result.SERVER_ERROR, ResultMessageType.ERROR, "Bank phone number cannot be empty");
        return Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Constraints are checked successfully");
    }
}
