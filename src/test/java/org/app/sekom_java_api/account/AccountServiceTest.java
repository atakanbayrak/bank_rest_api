package org.app.sekom_java_api.account;
import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.entity.account.Account;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.app.sekom_java_api.modal.request.AccountRequest;
import org.app.sekom_java_api.repository.account.AccountRepository;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.results.ResultMessageType;
import org.app.sekom_java_api.service.account.AccountService;
import org.app.sekom_java_api.service.account_holder.AccountHolderService;
import org.app.sekom_java_api.service.bank.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHolderService accountHolderService;

    @Mock
    private BankService bankService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAccountById_Found() {
        Account mockAccount = new Account(1L, 1L, "c2bd1109-db6a-45e8-823a-7085509677fc", "Account-1", 10000L,LocalDateTime.now(),  new Bank(),new AccountHolder());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        DataResult<Account> result = accountService.getAccountById(1L);

        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertNotNull(result.getData());
        assertEquals("Account-1", result.getData().getAccountName());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAccountById_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        DataResult<Account> result = accountService.getAccountById(1L);

        assertNotSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertNull(result.getData());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteAccount_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Result result = accountService.deleteAccount(1L);

        assertNotEquals(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        verify(accountRepository, times(1)).findById(1L);
    }
}
