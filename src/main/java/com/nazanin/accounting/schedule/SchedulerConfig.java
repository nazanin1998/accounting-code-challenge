package com.nazanin.accounting.schedule;

import com.nazanin.accounting.service.AccountingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final AccountingService accountingService;

    @Scheduled(cron = "0 0 1 * * *")
    public void endOfDayBalanceCalculation(){
        accountingService.endOfDaySumCalculation();
    }

}