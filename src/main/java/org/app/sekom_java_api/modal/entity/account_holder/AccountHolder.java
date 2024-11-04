package org.app.sekom_java_api.modal.entity.account_holder;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.sekom_java_api.modal.entity.account.Account;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_holders")
@Builder
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountHolderId;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "account_holder_surname")
    private String accountHolderSurname;

    @Column(name = "account_holder_email")
    private String accountHolderEmail;

    @Column(name = "account_holder_phone_number")
    private String accountHolderPhoneNumber;

    @Column(name = "account_holder_address")
    private String accountHolderAddress;

    @Column(name = "account_holder_identity_number")
    private String identityNumber;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;
}
