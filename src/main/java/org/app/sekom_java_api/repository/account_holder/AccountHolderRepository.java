package org.app.sekom_java_api.repository.account_holder;

import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {

    Optional<AccountHolder> findAccountHolderByIdentityNumber(String identityNumber);
}
