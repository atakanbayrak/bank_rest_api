package org.app.sekom_java_api.modal.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String transactionType;
    private Long transactionAmount;
    private LocalDateTime transactionTime;
    private String transactionDescription;

}
