package org.app.sekom_java_api.service.account;

import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.entity.account.Account;
import org.app.sekom_java_api.modal.request.AccountRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;

import java.util.List;

public interface IAccountService {

    DataResult<List<Account>> getAllAccounts();
    DataResult<Account> getAccountById(Long accountId);
    DataResult<AccountDto> getAccountByAccountNumber(String username);
    Result updateAccount(String accountNumber, String accountName);
    Result deleteAccount(Long accountId);
    Result saveAccount(AccountRequest accountRequest);
    DataResult<Long> getAccountBalance(String accountNumber);
    Result updateBalance(String accountNumber, Long amount);


}
