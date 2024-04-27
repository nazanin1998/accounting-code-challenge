package com.nazanin.accounting;

import com.nazanin.accounting.controller.AccountingController;
import com.nazanin.accounting.dto.AddMoneyRequestDto;
import com.nazanin.accounting.dto.AddMoneyResponseDto;
import com.nazanin.accounting.dto.GetBalanceResponseDto;
import com.nazanin.accounting.service.AccountingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountingApplicationTests {

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private AccountingController accountingController;

    @Test
    void contextLoads() {
    }

    @Test
    public void successControllerTestOfAddMoney() {
        AddMoneyResponseDto addMoney = accountingController.addMoney(new AddMoneyRequestDto()
                .setAmount(213L)
                .setUserId(23L));
    }


    @Test
    public void successAddMoney() {
        AddMoneyResponseDto addMoney = accountingService.addMoney(new AddMoneyRequestDto()
                .setAmount(2000L)
                .setUserId(120L));
    }

    @Test
    public void getBalanceForUserThatHasNoTransactionRecord() {
        GetBalanceResponseDto responseDto = accountingService.getBalance(121L);
    }

    @Test
    public void successControllerTestOfGetBalance() {
        accountingController.getBalance(120L);
    }

    @Test
    public void successGetBalance() {
        GetBalanceResponseDto responseDto = accountingService.getBalance(120L);
    }

    @Test
    public void successTestOfEndOfDaySumCalc() {
        accountingService.endOfDaySumCalculation();
    }
}
