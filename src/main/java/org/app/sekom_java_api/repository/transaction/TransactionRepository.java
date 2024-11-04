package org.app.sekom_java_api.repository.transaction;

import org.app.sekom_java_api.modal.entity.account.Account;
import org.app.sekom_java_api.modal.entity.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByTransactionCode(String transactionCode);
    Optional<List<Transaction>> findTransactionsByTransactionType(String transactionType);
    Optional<List<Transaction>> findTransactionsByAccount(Account account);

}
