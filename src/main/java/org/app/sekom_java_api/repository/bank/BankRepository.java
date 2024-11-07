package org.app.sekom_java_api.repository.bank;

import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findBankByBankCode(String bankCode);
}
