package org.app.sekom_java_api.modal.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.sekom_java_api.modal.entity.account.Account;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_amount")
    private Long transactionAmount;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "transaction_description")
    private String transactionDescription;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "transaction_code")
    private String transactionCode;

}
