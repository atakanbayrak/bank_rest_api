package org.app.sekom_java_api.account_holder;
import org.app.sekom_java_api.configuration.redis.CacheConfig;
import org.app.sekom_java_api.modal.dto.account_holder.AccountHolderDto;
import org.app.sekom_java_api.modal.entity.account_holder.AccountHolder;
import org.app.sekom_java_api.modal.request.AccountHolderRequest;
import org.app.sekom_java_api.repository.account_holder.AccountHolderRepository;
import org.app.sekom_java_api.results.*;
import org.app.sekom_java_api.service.account_holder.AccountHolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.app.sekom_java_api.configuration.redis.CacheConfig.redisTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AccountHolderServiceTest {
    @InjectMocks
    private AccountHolderService accountHolderService;

    @Mock
    private AccountHolderRepository accountHolderRepository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllAccountHolders_CacheHit() throws JsonProcessingException {
        // Arrange
        String cachedData = "[{'identityNumber':'12345','accountHolderName':'Aynur','accountHolderSurname':'Aynur','accountHolderPhoneNumber':'123456789','accountHolderEmail':'Aynur.Aynur@example.com','accountHolderAddress':'Ankara'}]";
        when(redisTemplate.opsForValue().get(CacheConfig.HOLDER_CACHE_KEY)).thenReturn(cachedData);
        List<AccountHolder> accountHolders = List.of(new AccountHolder(1L,"12345", "Aynur", "Aynur", "123456789", "Aynur.Aynur@example.com", "Ankara", LocalDateTime.now()));
        when(objectMapper.readValue(cachedData, new TypeReference<List<AccountHolder>>() {})).thenReturn(accountHolders);

        // Act
        DataResult<List<AccountHolder>> result = accountHolderService.getAllAccountHolders();

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(redisTemplate, times(1)).opsForValue().get(CacheConfig.HOLDER_CACHE_KEY);
    }

    @Test
    public void testGetAllAccountHolders_NoCacheHit() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue().get(CacheConfig.HOLDER_CACHE_KEY)).thenReturn(null);
        List<AccountHolder> accountHolders = List.of(new AccountHolder(2L,"12345", "Aynur", "Aynur", "123456789", "Aynur.Aynur@example.com", "Ankara", LocalDateTime.now()));
        when(accountHolderRepository.findAll()).thenReturn(accountHolders);
        String cachedData = "[{'identityNumber':'12345','accountHolderName':'Aynur','accountHolderSurname':'Aynur','accountHolderPhoneNumber':'123456789','accountHolderEmail':'Aynur.Aynur@example.com','accountHolderAddress':'Ankara'}]";
        when(objectMapper.writeValueAsString(accountHolders)).thenReturn(cachedData);

        // Act
        DataResult<List<AccountHolder>> result = accountHolderService.getAllAccountHolders();

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(accountHolderRepository, times(1)).findAll();
        verify(redisTemplate, times(1)).opsForValue().set(CacheConfig.HOLDER_CACHE_KEY, cachedData, Duration.ofMinutes(10));
    }

    @Test
    public void testGetAccountHolderById_Success() {
        // Arrange
        Long holderId = 1L;
        AccountHolder accountHolder = new AccountHolder(3L,"Aynur", "Aynur", "Aynur@hotmail.com", "54123", "Ankara", "12345", LocalDateTime.now());
        when(accountHolderRepository.findById(holderId)).thenReturn(Optional.of(accountHolder));

        // Act
        DataResult<AccountHolder> result = accountHolderService.getAccountHolderById(holderId);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        assertEquals("12345", result.getData().getIdentityNumber());
        verify(accountHolderRepository, times(1)).findById(holderId);
    }

    @Test
    public void testGetAccountHolderById_NotFound() {
        // Arrange
        Long holderId = 1L;
        when(accountHolderRepository.findById(holderId)).thenReturn(Optional.empty());

        // Act
        DataResult<AccountHolder> result = accountHolderService.getAccountHolderById(holderId);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.ERROR);
        verify(accountHolderRepository, times(1)).findById(holderId);
    }

    @Test
    public void testSaveAccountHolder_IdentityNumberNull() {
        // Act
        Result result = accountHolderService.saveAccountHolder("12381823", new AccountHolderRequest());

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.ERROR);
        assertEquals(ResultMessageType.ERROR, result.resultMessage.messageType);
    }

    @Test
    public void testSaveAccountHolder_Success() {
        // Arrange
        String identityNumber = "12345";
        AccountHolderRequest request = new AccountHolderRequest("Aynur", "Aynur", "aynur@hotmail.com", "54321123", "Ankara");
        when(accountHolderRepository.findAccountHolderByIdentityNumber(identityNumber)).thenReturn(Optional.empty());

        // Act
        Result result = accountHolderService.saveAccountHolder(identityNumber, request);

        // Assert
        assertSame(result.resultMessage.messageType, ResultMessageType.SUCCESS);
        verify(accountHolderRepository, times(1)).save(any(AccountHolder.class));
        verify(redisTemplate, times(1)).delete(CacheConfig.HOLDER_CACHE_KEY);
    }

    // Additional test cases for update and delete methods can be added here
}
