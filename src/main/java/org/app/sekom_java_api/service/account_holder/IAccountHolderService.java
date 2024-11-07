package org.app.sekom_java_api.service.account_holder;

import org.app.sekom_java_api.modal.dto.account_holder.AccountHolderDto;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.request.AccountHolderRequest;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;

import java.util.List;

public interface IAccountHolderService {

    DataResult<List<AccountHolder>> getAllAccountHolders();
    DataResult<AccountHolder> getAccountHolderById(Long holderId);
    DataResult<AccountHolderDto> getAccountHolderByIdentityNumber(String identityNumber);
    Result updateAccountHolder(Long holderId, AccountHolderRequest accountHolderRequest);
    Result deleteAccountHolder(Long holderId);
    Result saveAccountHolder(String identityNumber, AccountHolderRequest accountHolderRequest);
}
