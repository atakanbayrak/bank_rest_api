package org.app.sekom_java_api.repository.account;

import org.app.sekom_java_api.modal.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(Long accountId);
    Optional<Account> findByAccountNumber(String accountNumber);
}
