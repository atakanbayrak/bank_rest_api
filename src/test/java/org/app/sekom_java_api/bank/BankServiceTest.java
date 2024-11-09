package org.app.sekom_java_api.bank;
import org.app.sekom_java_api.modal.entity.bank.Bank;
import org.app.sekom_java_api.modal.request.BankRequest;
import org.app.sekom_java_api.repository.bank.BankRepository;
import org.app.sekom_java_api.results.*;
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
public class BankServiceTest {
    @InjectMocks
    private BankService bankService;

    @Mock
    private BankRepository bankRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBanks_Success() {
        // Arrange
        List<Bank> bankList = Arrays.asList(
                new Bank(1L, "Bank A", "001", "123 Street", "123456789", LocalDateTime.now()),
                new Bank(2L, "Bank B", "002", "456 Street", "987654321", LocalDateTime.now())
        );
        when(bankRepository.findAll()).thenReturn(bankList);

        // Act
        DataResult<List<Bank>> result = bankService.getAllBanks();

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertEquals(2, result.getData().size());
        verify(bankRepository, times(1)).findAll();
    }

    @Test
    void testGetBankById_Found() {
        // Arrange
        Bank bank = new Bank(1L, "Bank A", "001", "123 Street", "123456789", LocalDateTime.now());
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        // Act
        DataResult<Bank> result = bankService.getBankById(1L);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertNotNull(result.getData());
        assertEquals("Bank A", result.getData().getBankName());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBankById_NotFound() {
        // Arrange
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        DataResult<Bank> result = bankService.getBankById(1L);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.ERROR);
        assertNull(result.getData());
        verify(bankRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveBank_Success() {
        // Arrange
        BankRequest bankRequest = new BankRequest("Bank A", "123 Street", "123456789");
        when(bankRepository.save(any(Bank.class))).thenReturn(new Bank());

        // Act
        Result result = bankService.saveBank(bankRequest);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        verify(bankRepository, times(1)).save(any(Bank.class));
    }

    @Test
    void testSaveBank_InvalidRequest() {
        // Arrange
        BankRequest bankRequest = new BankRequest("", "", "");

        // Act
        Result result = bankService.saveBank(bankRequest);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.ERROR);
        assertEquals("Bank name cannot be empty", result.resultMessage.messageText);
        verify(bankRepository, times(0)).save(any(Bank.class));
    }

    @Test
    void testDeleteBank_Success() {
        // Arrange
        Bank bank = new Bank(1L, "Bank A", "001", "123 Street", "123456789", LocalDateTime.now());
        when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        doNothing().when(bankRepository).delete(bank);

        // Act
        Result result = bankService.deleteBank(1L);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        verify(bankRepository, times(1)).findById(1L);
        verify(bankRepository, times(1)).delete(bank);
    }

    @Test
    void testDeleteBank_NotFound() {
        // Arrange
        when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Result result = bankService.deleteBank(1L);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.ERROR);
        assertEquals("There is no bank", result.resultMessage.messageText);
        verify(bankRepository, times(1)).findById(1L);
        verify(bankRepository, times(0)).delete(any(Bank.class));
    }
}
