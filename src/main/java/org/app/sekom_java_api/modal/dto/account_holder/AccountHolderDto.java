package org.app.sekom_java_api.modal.dto.account_holder;

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
public class AccountHolderDto {
    private String accountHolderName;
    private String accountHolderSurname;
    private String accountHolderEmail;
    private String accountHolderPhoneNumber;
    private String accountHolderAddress;
    private String identityNumber;
}
