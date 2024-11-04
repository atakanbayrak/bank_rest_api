package org.app.sekom_java_api.modal.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolderRequest {
    private String accountHolderName;
    private String accountHolderSurname;
    private String accountHolderEmail;
    private String accountHolderPhoneNumber;
    private String accountHolderAddress;
}
