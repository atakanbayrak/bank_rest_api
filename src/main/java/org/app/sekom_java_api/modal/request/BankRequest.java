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
public class BankRequest {

    private String bankName;
    private String bankAddress;
    private String bankPhoneNumber;
}
