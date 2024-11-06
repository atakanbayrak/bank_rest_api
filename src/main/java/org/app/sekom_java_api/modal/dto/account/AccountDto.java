package org.app.sekom_java_api.modal.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.entity.bank.Bank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long accountId;
    private String accountNumber;
    private String accountName;
    private Long accountBalance;
    private Bank bank;
    private AccountHolder accountHolder;
    private Long version;
}
