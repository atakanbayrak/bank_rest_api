package org.app.sekom_java_api.controller.account;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.entity.account.Account;
import org.app.sekom_java_api.modal.request.AccountRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.service.account.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/account/v1")
public class AccountController {

    private final AccountService accountService;

    @GetMapping(path = "getAllAccounts")
    public DataResult<List<Account>> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping(path = "getAccountById")
    public DataResult<Account> getAccountById(@RequestParam Long accountId) {
        return accountService.getAccountById(accountId);
    }

    @GetMapping(path = "getAccountByAccountNumber")
    public DataResult<AccountDto> getAccountByAccountNumber(@RequestParam String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }

    @PostMapping(path = "updateAccount")
    public Result updateAccount(@RequestParam String accountNumber, @RequestParam String accountName) {
        return accountService.updateAccount(accountNumber, accountName);
    }

    @PostMapping(path = "deleteAccount")
    public Result deleteAccount(@RequestParam Long accountId) {
        return accountService.deleteAccount(accountId);
    }

    @PostMapping(path = "saveAccount")
    public Result saveAccount(@RequestBody AccountRequest accountRequest) {
        return accountService.saveAccount(accountRequest);
    }

    @GetMapping(path = "getAccountBalance")
    public DataResult<Long> getAccountBalance(@RequestParam String accountNumber) {
        return accountService.getAccountBalance(accountNumber);
    }






}
