package org.app.sekom_java_api.service.bank;

import org.app.sekom_java_api.modal.dto.bank.BankDto;
import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.app.sekom_java_api.modal.request.BankRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;

import java.util.List;

public interface IBankService {

    DataResult<List<Bank>> getAllBanks();
    DataResult<Bank> getBankById(Long bankId);
    DataResult<BankDto> getBankByCode(String bankCode);
    Result updateBank(Long bankId, BankRequest bankRequest);
    Result deleteBank(Long bankId);
    Result saveBank(BankRequest bankRequest);
}
