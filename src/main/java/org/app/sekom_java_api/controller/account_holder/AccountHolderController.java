package org.app.sekom_java_api.controller.account_holder;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.account_holder.AccountHolderDto;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.request.AccountHolderRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.service.account_holder.AccountHolderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/accountHolder/v1")
@RequiredArgsConstructor
public class AccountHolderController {
    private final AccountHolderService accountHolderService;

    @GetMapping(path = "getAllAccountHolders")
    public DataResult<List<AccountHolder>> getAllAccountHolders() {
        return accountHolderService.getAllAccountHolders();
    }

    @GetMapping(path = "getAccountHolderById")
    public DataResult<AccountHolder> getAccountHolderById(@RequestParam Long holderId) {
        return accountHolderService.getAccountHolderById(holderId);
    }

    @GetMapping(path = "getAccountHolderByIdentityNumber")
    public DataResult<AccountHolderDto> getAccountHolderByIdentityNumber(@RequestParam String identityNumber) {
        return accountHolderService.getAccountHolderByIdentityNumber(identityNumber);
    }

    @PostMapping(path = "updateAccountHolder")
    public Result updateAccountHolder(@RequestParam Long holderId,@RequestBody AccountHolderRequest accountHolderRequest) {
        return accountHolderService.updateAccountHolder(holderId, accountHolderRequest);
    }

    @PostMapping(path = "deleteAccountHolder")
    public Result deleteAccountHolder(@RequestParam Long holderId) {
        return accountHolderService.deleteAccountHolder(holderId);
    }

    @PostMapping(path = "saveAccountHolder")
    public Result saveAccountHolder(@RequestParam String identityNumber,@RequestBody AccountHolderRequest accountHolderRequest) {
        return accountHolderService.saveAccountHolder(identityNumber, accountHolderRequest);
    }




}
