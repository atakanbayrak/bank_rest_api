package org.app.sekom_java_api.transaction;

import org.app.sekom_java_api.modal.dto.account.AccountDto;
import org.app.sekom_java_api.modal.dto.transaction.TransactionDto;
import org.app.sekom_java_api.modal.entity.transaction.Transaction;
import org.app.sekom_java_api.modal.request.TransactionRequest;
import org.app.sekom_java_api.repository.transaction.TransactionRepository;
import org.app.sekom_java_api.results.DataResult;
import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.results.ResultMessageType;
import org.app.sekom_java_api.service.account.AccountService;
import org.app.sekom_java_api.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTransactions_Success() {
        List<Transaction> mockTransactions = List.of(new Transaction());
        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        DataResult<List<Transaction>> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertEquals(mockTransactions, result.getData());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionById_Success() {
        Transaction mockTransaction = new Transaction();
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        DataResult<Transaction> result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertEquals(mockTransaction, result.getData());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        DataResult<Transaction> result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertNotSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertTrue(result.resultMessage.messageText.contains("There is no transaction"));
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        AccountDto mockAccount = new AccountDto();
        mockAccount.setAccountId(1L);
        mockAccount.setAccountBalance(50L);
        when(accountService.getAccountByAccountNumber("12345")).thenReturn(new DataResult<>(mockAccount, Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is found successfully")));

        Result result = transactionService.withdraw("12345", 100L);

        assertNotNull(result);
        assertNotSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertTrue(result.resultMessage.messageText.contains("Insufficient balance"));
        verify(accountService, times(0)).updateBalance(anyString(), anyLong());
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testTransfer_InsufficientBalance() {
        AccountDto fromAccount = new AccountDto();
        fromAccount.setAccountBalance(50L);
        when(accountService.getAccountByAccountNumber("12345")).thenReturn(new DataResult<>(fromAccount, Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is found successfully")));
        when(accountService.getAccountByAccountNumber("67890")).thenReturn(new DataResult<>(new AccountDto(), Result.showMessage(Result.SUCCESS, ResultMessageType.SUCCESS, "Account is found successfully")));

        Result result = transactionService.transfer("12345", "67890", 100L);

        assertNotNull(result);
        assertNotSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertTrue(result.resultMessage.messageText.contains("Insufficient balance"));
        verify(accountService, times(0)).updateBalance(anyString(), anyLong());
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }
}
