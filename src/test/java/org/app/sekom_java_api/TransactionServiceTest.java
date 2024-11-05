package org.app.sekom_java_api;

import org.app.sekom_java_api.results.Result;
import org.app.sekom_java_api.results.ResultMessageType;
import org.app.sekom_java_api.service.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    @Transactional
    public void testConcurrentWithdraw() throws Exception {
        // Test verisi hazırlama
        String accountNumber = "12345";
        Long initialBalance = 1000L;
        Long withdrawAmount = 500L;

        // Account'u ayarla (örneğin, veritabanında oluştur)
        accountService.saveAccount(accountNumber, initialBalance);

        // İki farklı thread ile aynı anda para çekme işlemi başlatmak için bir ExecutorService kullan
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // İki paralel withdraw işlemi başlat ve sonuçları al
        Future<Result> withdrawTask1 = executor.submit(() -> accountService.withdraw(accountNumber, withdrawAmount));
        Future<Result> withdrawTask2 = executor.submit(() -> accountService.withdraw(accountNumber, withdrawAmount));

        // Task sonuçlarını al ve işlemleri doğrula
        Result result1 = withdrawTask1.get();
        Result result2 = withdrawTask2.get();

        executor.shutdown();

        // Sonuçları doğrula
        // İlk işlem başarılı olmalı
        assertEquals(ResultMessageType.SUCCESS, result1.getMessageType());
        assertEquals("Withdraw is successful", result1.getMessage());

        // İkinci işlem concurrency hatası nedeniyle başarısız olmalı
        assertEquals(ResultMessageType.ERROR, result2.getMessageType());
        assertEquals("Concurrent transaction detected, please try again", result2.getMessage());
    }
}
