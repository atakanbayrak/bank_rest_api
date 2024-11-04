package org.app.sekom_java_api.modal.dto.bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.sekom_java_api.modal.entity.account.Account;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDto {

    private String bankName;
    private String bankCode;
    private String bankAddress;
    private String bankPhoneNumber;
    private List<Account> accounts;
}
