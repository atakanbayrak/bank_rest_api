package org.app.sekom_java_api.modal.entity.account;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.entity.bank.Bank;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Version
    private Long version;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_balance")
    private Long accountBalance;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    //List transactions
    //Holder

    @ManyToOne
    @JoinColumn(name = "holder_id", nullable = false)
    private AccountHolder accountHolder;



}
