package org.app.sekom_java_api.controller.bank;

import lombok.RequiredArgsConstructor;
import org.app.sekom_java_api.modal.dto.bank.BankDto;
import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.app.sekom_java_api.modal.request.BankRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.service.bank.BankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/bank/v1")
@RestController
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping(path = "getAllBanks")
    public DataResult<List<Bank>> getAllBanks() {
        return bankService.getAllBanks();
    }

    @GetMapping(path = "getBankById")
    public DataResult<Bank> getBankById(@RequestParam Long bankId) {
        return bankService.getBankById(bankId);
    }

    @GetMapping(path = "getBankByCode")
    public DataResult<BankDto> getBankByCode(@RequestParam String bankCode) {
        return bankService.getBankByCode(bankCode);
    }

    @PostMapping(path = "updateBank")
    public Result updateBank(@RequestParam Long bankId,@RequestBody BankRequest bankRequest) {
        return bankService.updateBank(bankId, bankRequest);
    }

    @PostMapping(path = "deleteBank")
    public Result deleteBank(@RequestParam Long bankId) {
        return bankService.deleteBank(bankId);
    }

    @PostMapping(path = "saveBank")
    public Result saveBank(@RequestBody BankRequest bankRequest) {
        return bankService.saveBank(bankRequest);
    }

}
